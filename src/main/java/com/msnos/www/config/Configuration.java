package com.msnos.www.config;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.HotReload;
import org.aeonbits.owner.Config.HotReloadType;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.Reloadable;

@HotReload(value=5, type=HotReloadType.ASYNC)
@Sources({
    "file:${application.configuration.file}"
})
public interface Configuration extends Config, Reloadable, Accessible {

    @Key("parallel.actions.max.num")
    @DefaultValue("200")    
    int getMaxParallelActions();

}