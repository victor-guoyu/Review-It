package ir.server;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.logging.log4j.util.Strings;

public class Configuration {

    public static final String CONFIG_FILE            = "properties/app.properties";
    public static final String LOGGER_CONFIG          = "logger.config";
    public static final String LOGGER_SYSTEM_PROPERTY = "log4j.configurationFile";

    public static final String AWS_ACCESS_ID          = "awsAccessKeyId";
    public static final String AWS_SECRET_KEY         = "awsSecretKey";
    public static final String AWS_ENDPOINT           = "awsEndpoint";

    public static final String YOUTUBE_API_KEY        = "youtubeAPIKey";

    private Properties         properties;

    private Configuration() {
        loadProperties();
        String loggerConfig = getLoggerConfig();
        if (Strings.isNotEmpty(loggerConfig)) {
            System.setProperty(LOGGER_SYSTEM_PROPERTY, loggerConfig);
        }
    }

    public String getAWSAccessId() {
        return properties.getProperty(AWS_ACCESS_ID);
    }

    public String getAWSSecreKey() {
        return properties.getProperty(AWS_SECRET_KEY);
    }

    /**
     * @return the AWSEnpoint
     */
    public String getAWSEndpoint() {
        return properties.getProperty(AWS_ENDPOINT);
    }

    public String getYoutubeApiKey(){
        return properties.getProperty(YOUTUBE_API_KEY);
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

    public static Configuration getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final Configuration INSTANCE = new Configuration();
    }
}