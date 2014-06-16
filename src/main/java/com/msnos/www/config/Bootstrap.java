package com.msnos.www.config;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.aeonbits.owner.ConfigFactory;
import org.aeonbits.owner.event.ReloadEvent;
import org.aeonbits.owner.event.ReloadListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.msnos.www.core.HotSwappableMessageStore;
import com.msnos.www.core.InmemoryMessageStore;
import com.msnos.www.core.MessageStore;

public class Bootstrap {

	private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

	public static final Bootstrap runtime;
	static {
	    runtime = new Bootstrap(ConfigFactory.create(Configuration.class));
		runtime.boot();
	}

	private Configuration config;
    private ExecutorService asyncExecutors;
    private ScheduledExecutorService asyncScheduler;
	private HotSwappableMessageStore repository;
    
    Bootstrap(Configuration cfg) {
        this.config = cfg;
        log.info("Using configuration file {}", System.getProperty("application.configurationFile"));
		logConfig();

		config.addReloadListener(new ReloadListener() {
			@Override
			public void reloadPerformed(ReloadEvent event) {
				onHotReload();
			}
		});
    }

    public void boot() {
		try {
            repository = new HotSwappableMessageStore(createMessageProcessor());
		} catch (Exception unrecoverable) {
			log.error("[FATAL] Unrecoverable exception during system bootstrap: I will die :(", unrecoverable);
			System.exit(-1);
		}
	}

	public Configuration getConfig() {
		return config;
	}

	public MessageStore getEventRepository() {
		return repository;
	}

    private void onHotReload() {
		log.info("Config hot reload detected, new configuration: " + config, toString());

		synchronized (repository) {
			logConfig();

			try {
				ExecutorService asyncExecs = asyncExecutors;
				ScheduledExecutorService schedExecs = asyncScheduler;

				log.debug("Creating new infrastructure...");
				repository.swapDelegate(createMessageProcessor());

				log.debug("Removing old infrastructure...");
				shutdown(schedExecs);
				shutdown(asyncExecs);

				log.debug("Done!");
			} catch (Exception ex) {
				log.warn("Hot reload failed! The system can be in an inconsistent state :(", ex);
			}
		}
	}

	private void shutdown(ExecutorService service) {
		try {
			service.shutdown();
		} catch (Exception ex) {
			log.debug("Unexpected exception shutting down thread pool", ex);
		}
	}

	private MessageStore createMessageProcessor() {
        asyncScheduler = Executors.newScheduledThreadPool(2);
        asyncExecutors = new ThreadPoolExecutor(
                config.getMaxParallelActions(),
                config.getMaxParallelActions(),
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(3*config.getMaxParallelActions()),
                new ThreadPoolExecutor.CallerRunsPolicy());

        
        return new InmemoryMessageStore();
	}

	private void logConfig() {
		log.info("Config: " + config);
		if (log.isDebugEnabled())
			try {
				config.store(System.err, "Bootstrap properties");
			} catch (IOException ignore) {
			}
	}
}
