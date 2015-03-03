package ir.server;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.logging.log4j.util.Strings;

import com.google.common.base.Optional;
import com.google.common.primitives.Ints;

public class Configuration {

    public static final String CONFIG_FILE            = "properties/app.properties";
    public static final String LOGGER_CONFIG          = "logger.config";
    public static final String LOGGER_SYSTEM_PROPERTY = "log4j.configurationFile";

    public static final String SERVER_PORT            = "server.port";
    public static final String PUBLIC_DIR             = "public.dir";

    public static final String AWS_ACCESS_ID          = "awsAccessKeyId";
    public static final String AWS_SECRET_KEY         = "awsSecretKey";
    public static final String AWS_ENDPOINT           = "awsEndpoint";

    private Properties         properties;

    private Configuration() {
        loadProperties();
        String loggerConfig = getLoggerConfig();
        if (Strings.isNotEmpty(loggerConfig)) {
            System.setProperty(LOGGER_SYSTEM_PROPERTY, loggerConfig);
        }
    }

    public Optional<Integer> getServerPort() {
        Integer port = Ints.tryParse(properties.getProperty(SERVER_PORT));
        if (port == null) {
            return Optional.absent();
        } else {
            return Optional.of(port);
        }
    }

    public String getPublicDir() {
        return properties.getProperty(PUBLIC_DIR);
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