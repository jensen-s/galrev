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
    private static final GalRevSettings instance = new GalRevSettings();

    private String persistenceUnit;
    private boolean developerMode;
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

    public static boolean isDeveloperMode() {
        return getInstance().developerMode;
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
                developerMode = readBooleanProperty(props, "developerMode", false);
            } catch (IOException e) {
                logger.error("Could not read settings file " + SETTINGS_FILE, e);
            }
        }

    }

    private boolean readBooleanProperty(Properties props, String propertyName, boolean defaultValue) {
        String value = props.getProperty(fullPropertyName(propertyName), "" + defaultValue);
        return "true".equalsIgnoreCase(value);
    }

    private String readStringProperty(Properties props, String propertyName, String defaultValue) {
        return props.getProperty(fullPropertyName(propertyName), defaultValue);
    }

    private String fullPropertyName(String propertyName) {
        return PREFIX + "." + propertyName;
    }

}
