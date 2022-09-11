package com.gitee.freakchicken.dbapi.basic.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class PropertiesUtil {

    private static final String DEFAULT_APPLICATION_PROPERTIES_FILE_NAME = "application.properties";

    private static final Properties PROPERTIES = new Properties();

    static {
        InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(DEFAULT_APPLICATION_PROPERTIES_FILE_NAME);
        try {
            PROPERTIES.load(in);
        } catch (IOException e) {
            log.warn("load {} error", DEFAULT_APPLICATION_PROPERTIES_FILE_NAME, e);
        }
    }

    public static String getKey(String key) {
        return PROPERTIES.getProperty(key);
    }

}