package ir.server;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.logging.log4j.util.Strings;

public class Configuration {

    public static final String CONFIG_FILE            = "properties/app.properties";
    public static final String LOGGER_CONFIG          = "logger.config";
    public static final String LOGGER_SYSTEM_PROPERTY = "log4j.configurationFile";

    public static final String EBAY_DEV_ID            = "ebayDevId";
    public static final String EBAY_APP_ID            = "ebayAppId";
    public static final String EBAY_CERTIFICATE       = "ebayCertificate";
    public static final String EBAY_TOKEN             = "ebayCertificate";

    private Properties         properties;

    private Configuration() {
        loadProperties();
        String loggerConfig = getLoggerConfig();
        if (Strings.isNotEmpty(loggerConfig)) {
            System.setProperty(LOGGER_SYSTEM_PROPERTY, loggerConfig);
        }
    }

    public String getEbayDevId() {
        return properties.getProperty(EBAY_DEV_ID);
    }
    
    public String getEbayAppId() {
        return properties.getProperty(EBAY_APP_ID);
    }

    /**
     * @return the ebayCertificate
     */
    public String getEbayCertificate() {
        return properties.getProperty(EBAY_CERTIFICATE);
    }

    /**
     * @return the ebayToken
     */
    public String getEbayToken() {
        return properties.getProperty(EBAY_TOKEN);
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