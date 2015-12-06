package org.jensen.galrev.settings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by jensen on 06.12.15.
 */
public class GalRevSettings {
    private static final String SETTINGS_FILE = "galrev.properties";
    private static final String PREFIX = "galrev";
    private static GalRevSettings instance = new GalRevSettings();

    private String persistenceUnit;
    private static final Logger logger = LogManager.getLogger(GalRevSettings.class);

    private GalRevSettings(){
        loadSettings();
    }


    private static GalRevSettings getInstance() {
        return instance;
    }

    public static String getPersistenceUnit(){
        return getInstance().persistenceUnit;
    }

    private void loadSettings() {
        InputStream inStream = GalRevSettings.class.getResourceAsStream("/" + SETTINGS_FILE);
        if (inStream == null){
            logger.error("Missing settings file: " + SETTINGS_FILE);
        }else{
            Properties props = new Properties();
            try {
                props.load(inStream);
                persistenceUnit  = readStringProperty(props, "persistenceUnit", "galrev");
            } catch (IOException e) {
                logger.error("Could not read settings file " + SETTINGS_FILE, e);
            }
        }

    }

    private String readStringProperty(Properties props, String propertyName, String defaultValue) {
        String name=PREFIX+"."+propertyName;
        return props.getProperty(name, defaultValue);
    }

}
