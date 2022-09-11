package com.gitee.freakchicken.dbapi.plugin;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class PluginConf {

    private static final String PLUGIN_PROPERTIES_FILE_NAME = "plugin.properties";

    private static final Properties properties = new Properties();

    static {
        InputStream in = PluginConf.class.getClassLoader().getResourceAsStream(PLUGIN_PROPERTIES_FILE_NAME);
        try {
            properties.load(in);
        } catch (IOException e) {
            log.warn("load {} error", PLUGIN_PROPERTIES_FILE_NAME, e);
        }
    }

    public static String getKey(String key) {
        return properties.getProperty(key);
    }

}
