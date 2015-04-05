package ir.server;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import ir.config.Configuration;
import ir.config.ServletConfig;
import ir.crawler.TrainingData;
import ir.index.SearchEngine;

public class App {
    private final Server  server;
    private final Logger  mainLog;
    private Configuration config;
    private SearchEngine  searchEngine;

    private App() {
        startServerLogger();
        mainLog = LogManager.getLogger(App.class);
        searchEngine = SearchEngine.getSearchEngine();
        config = Configuration.getInstance();
        Optional<Integer> port = Optional.of(config.getServerPort());
        server = new Server(port.filter(p -> p != 0)
                    .map(p -> p).orElse(ServerConstants.DEFAULT_PORT));
        mainLog.info(String.format("Starting server listening on port: %s",
                port));
    }

    public Logger getMainLog() {
        return mainLog;
    }

    public void start() throws Exception {
        Version.upSince = new Date();
        // Build the initial data set before
        // the user can retrieve from the server
        mainLog.info("Up since: " + Version.upSince.getTime());
        mainLog.info("Building initial data collection using training queries...");
        searchEngine.retrieveData(TrainingData.INSTANCE.getTraingQueries());
        servletInit();
        mainLog.info("==================Ready to serve requests=================");
        server.start();
        server.join();
    }

    /**
     * Setup log4j & Start server log
     */
    private void startServerLogger() {
        String loggerConfig = ServerConstants.LOGGER_CONFIG_FILE;
        if (Strings.isNotEmpty(loggerConfig)) {
            System.setProperty(ServerConstants.LOGGER_SYSTEM_PROPERTY,
                    loggerConfig);
        }
    }

    /**
     * Add all the servlets to the server
     * including resource handler serving static files
     */
    private void servletInit() {
        mainLog.info("Initializing servlets");
        HandlerCollection handlers = new HandlerCollection();
        Iterable<Handler> servletHandler = Iterables.transform(Configuration.getInstance().getServlets(),
                new Function<ServletConfig, Handler>() {

                    @Override
                    public Handler apply(ServletConfig servletConfig) {
                        return buildServletHandler(servletConfig);
                    }
        });
        servletHandler.forEach((handler) -> {
            handlers.addHandler(handler);
        });
        handlers.addHandler(buildResourceHandler());
        server.setHandler(handlers);
    }

    /**
     * Build servlet handler based on servlet configuration
     * @param servletConfig
     * @return ServletContextHandler servlet handler
     */
    private Handler buildServletHandler(ServletConfig servletConfig) {

        ServletContextHandler contextHandler = new ServletContextHandler();
        try {
            contextHandler.setContextPath(servletConfig.getContextPath());
            contextHandler.addServlet(servletConfig.getClassName(),
                    ServerConstants.ROOT_PATH);
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Unable to create handler for servlet: %s",
                    servletConfig.getClassName()), e);
        }
        mainLog.info(String.format("Added servlet handler: %s to path: ",
                servletConfig.getClassName(), servletConfig.getContextPath()));
        return contextHandler;
    }

    private Handler buildResourceHandler() {
        ResourceHandler ctx = new ResourceHandler();
        ctx.setDirectoriesListed(true);
        List<String> wp = config.getWlecomePages();
        ctx.setWelcomeFiles(wp.toArray(new String[wp.size()]));
        ctx.setResourceBase(config.getPublicDir());
        return ctx;
    }

    public static void main(String[] args) {
        App server = AppServer.INSATNCE;
        try {
            server.start();
            server.getMainLog().info(Version.showInfo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class AppServer {
        private static final App INSATNCE = new App();
    }
}
