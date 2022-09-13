package com.gitee.freakchicken.dbapi.basic.event;

import com.gitee.freakchicken.dbapi.plugin.PluginManager;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LoadPluginOnSpringReady {
		@EventListener
		public void loadPlugins(ApplicationReadyEvent event) {
				PluginManager.loadPlugins();
		}
}