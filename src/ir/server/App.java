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
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import ir.config.Configuration;
import ir.config.ServletConfig;
import ir.crawler.Crawler;
import ir.crawler.TrainingData;

public class App {
    private final Server  server;
    private List<Crawler> crawlers;
    private Logger        mainLog;
    private Configuration config;

    private App() {
        mainLog = LogManager.getLogger(App.class);
        startServerLog();
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
        crawlers = initializeCrawlers();
        // Build the initial data set before the user can retrieve from the
        // server
        retriveData(TrainingData.INSTANCE.getTraingQueries());
        servletInit();
        server.start();
        server.join();
    }

    /**
     * Setup log4j & Start server log
     */
    private void startServerLog() {
        String loggerConfig = ServerConstants.LOGGER_CONFIG_FILE;
        if (Strings.isNotEmpty(loggerConfig)) {
            System.setProperty(ServerConstants.LOGGER_SYSTEM_PROPERTY,
                    loggerConfig);
        }
    }

    /**
     * Load and initialize all the crawlers
     */
    private List<Crawler> initializeCrawlers() {
        Iterable<Crawler> crawlers = Iterables.transform(config.getCrawlers(),
                new Function<String, Crawler>() {

                    @Override
                    public Crawler apply(String className) {
                        Crawler crawler = null;
                        try {
                            crawler = (Crawler) Class.forName(className)
                                    .newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return crawler;
                    }
                });

        return ImmutableList.copyOf(crawlers);
    }

    /**
     * Add all the servlets to the server
     */
    private void servletInit() {
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

    /**
     * @param List of product name queries
     *        Lucene will index all the data being retrieved
     */
    public void retriveData(List<String> queries) {
        crawlers.stream()
        .forEach((crawler) -> {
            crawler.fetch(queries);
        });
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
