package ir;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.logging.log4j.util.Strings;

public class Configuration {

    private static final String CONFIG_FILE            = "properties/app.properties";
    private static final String LOGGER_CONFIG          = "logger.config";
    private static final String LOGGER_SYSTEM_PROPERTY = "log4j.configurationFile";
    private Properties          properties;

    private Configuration() {
        loadProperties();
        String loggerConfig = getLoggerConfig();
        if (Strings.isNotEmpty(loggerConfig)) {
            System.setProperty(LOGGER_SYSTEM_PROPERTY, loggerConfig);
        }
    }

    /**
     * loading all app properties
     */
    private void loadProperties() {
        properties = new Properties();
        try {
            properties.load(new FileInputStream(CONFIG_FILE));
        } catch (Exception e) {
            throw new RuntimeException("Unable to find  property file", e);
        }
    }

    /**
     * @return Log4j configure 
     */
    private String getLoggerConfig() {
        return properties.getProperty(LOGGER_CONFIG);
    }

    public Configuration getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final Configuration INSTANCE = new Configuration();
    }
}