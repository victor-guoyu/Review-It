package ir.config;

import ir.server.ServerConstants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Optional;

public final class Configuration {
    // Server configuration
    private int                 SERVER_PORT;
    private String              PUBLIC_DIR;
    private List<ServletConfig> SERVLETS;

    // AWS configuration
    private String              AWS_ACCESS_KEY_ID;
    private String              AWS_SECRET_KEY;
    private String              AWS_ENDPOINT;

    private Configuration() {
    }

    private static Configuration getConfiguration() {
        Configuration config = null;
        try {
            config = new Yaml()
                    .loadAs(Files.newInputStream(Paths
                            .get(ServerConstants.APP_CONFIG_FILE)),
                            Configuration.class);
        } catch (IOException e) {
            throw new RuntimeException("Unable to find  property YAML file", e);
        }
        return config;
    }

    public Optional<Integer> getServerPort() {
        if (SERVER_PORT == 0) {
            return Optional.absent();
        } else {
            return Optional.of(SERVER_PORT);
        }
    }

    public String getPubDir() {
        return PUBLIC_DIR;
    }

    public List<ServletConfig> getServlets() {
        return SERVLETS;
    }

    public String getAwsAccessKeyId() {
        return AWS_ACCESS_KEY_ID;
    }
    public String getAwsEndPoint() {
        return AWS_ENDPOINT;
    }

    public String getAwsSecretKey() {
        return AWS_SECRET_KEY;
    }

    public static Configuration getInstance() {
        return SingletonConfiguration.INSTANCE;
    }

    @Override
    public String toString() {
        return String.format(
                "Server listening on port: %s serving directory: %s",
                SERVER_PORT, PUBLIC_DIR);
    }

    private static class SingletonConfiguration {
        private static final Configuration INSTANCE = Configuration
                                                            .getConfiguration();
    }
}
