package com.msnos.www.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aeonbits.owner.event.ReloadEvent;
import org.aeonbits.owner.event.ReloadListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.msnos.www.config.Bootstrap;
import com.msnos.www.config.Configuration;
import com.msnos.www.core.MessageStore;
import com.workshare.msnos.core.Message;
import com.workshare.msnos.core.serializers.WireJsonSerializer;
import com.workshare.msnos.core.serializers.WireSerializer;

@SuppressWarnings("serial")
public final class CoreServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(CoreServlet.class);
    private static final Logger messageLogger = LoggerFactory.getLogger("messages.in");

    private final Configuration config;
    private final MessageStore messages;
    private final WireSerializer serializer;

    public CoreServlet() {
        this(Bootstrap.runtime.getEventRepository(), Bootstrap.runtime.getConfig());
    }

    public CoreServlet(MessageStore aMessageStore, Configuration aConfig) {
        this.messages = aMessageStore;
        this.config = aConfig;
        this.serializer = new WireJsonSerializer();
        
        config.addReloadListener(new ReloadListener() {
            @Override
            public void reloadPerformed(ReloadEvent event) {
                // TODO?
            }
        });
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        dumpIfRequested(request);
        addStandardHeaders(response);

        try {
            UUID cloud = getUUIDFromParameter(request, "cloud", false);
            UUID message = getUUIDFromParameter(request, "message", true);
            if (log.isDebugEnabled()) log.debug("RX from cloud {} based on message {}", cloud, message);
            List<Message> values = messages.load(cloud, message);
            for (Message msg: values) {
                if (msg == null)
                    continue;
                
                if (log.isDebugEnabled()) log.debug("Sending message {}", msg);
                response.getWriter().println(serializer.toText(msg));
            }
            response.setStatus(200);
        }
        catch (Exception ex) {
            log.warn("Unable to process request", ex);
            response.setStatus(400);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        dumpIfRequested(request);
        addStandardHeaders(response);

        BufferedReader reader = request.getReader();
        try {
            UUID cloud = getUUIDFromParameter(request, "cloud", false);
            if (log.isDebugEnabled()) log.debug("TX from cloud {}", cloud);
            String text = null;
            while((text = reader.readLine()) != null) {
                if (!text.trim().isEmpty()) {
                    messageLogger.info(text);
                    try {
                        Message message = serializer.fromText(text, Message.class);
                        messages.store(cloud, message);
                        if (log.isDebugEnabled()) log.debug("Storing message {}",message);
                    } catch (Exception ex) {
                        log.warn("Unable to process message "+text, ex);
                    }
                }
            }
        } 
        catch (Exception ex) {
            log.warn("Unable to process request", ex);
            response.setStatus(400);
        }
        finally {
            reader.close();
        }
    }

    private UUID getUUIDFromParameter(HttpServletRequest request, final String name, boolean acceptsEmpty) {
        final String value = request.getParameter(name);
        if ((value == null || value.isEmpty()) && acceptsEmpty)
            return null;
        
        return UUID.fromString(value);
    }

    private void dumpIfRequested(HttpServletRequest request) {
        if (request.getParameter("dump") != null)
            dumpRequest(request);
    }

    private void addStandardHeaders(HttpServletResponse response) {
        addContentTypeHeader(response);
        addCacheHeaders(response);
        addCORSHeaders(response);
    }

    private void addCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "X-Requested-With");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-credentials", "true");
        response.setHeader("Access-Control-Max-Age", "1728000");
    }

    private void addCacheHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store");
    }

    private void addContentTypeHeader(HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
    }

    @SuppressWarnings("rawtypes")
    private void dumpRequest(HttpServletRequest request) {
        log.debug("Dumping {} request", request.getMethod());

        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0)
            log.debug("Cookies:");
            for (Cookie cookie : cookies) {
                log.debug("- cookie {}={}, domain={}", cookie.getName(), cookie.getValue(), cookie.getDomain());
            }
        
        Map params = request.getParameterMap();
        if (params!= null && params.size() > 0) {
            log.debug("Params:");
            for (Object key : params.keySet()) {
                log.debug("- {}={}", key, params.get(key));
            }
        }

        Enumeration headerNames = request.getHeaderNames();
        if (headerNames != null) {
            log.debug("Headers:");
            while(headerNames.hasMoreElements()) {
                String name = headerNames.nextElement().toString();
                String value = request.getHeader(name);
                log.debug("- {}={}", name, value);
            }
        
        }
    }
}