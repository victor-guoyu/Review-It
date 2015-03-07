package ir.server;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;

import ir.config.Configuration;
import ir.crawler.amazon.AWSCrawler;

public class App {
    private final Server server;
    private Logger mainLog;
    private Configuration config;

    private App() {

        config = Configuration.getInstance();

        // If port number is not found in the config file, using the default
        // port instead
        server = new Server(config.getServerPort()
                            .or(ServerConstants.DEFAULT_PORT));
        HandlerCollection handler = new HandlerCollection();
        handler.addHandler(handler);

        //for each servlet:
        // initialize the servlet using static method
        //Create a handler with the context
        //Create an optional connector with an optional threadpool
    }

    public void start() {
        Version.upSince = new Date();
        startServerLog();
        startCrawlers();
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
    private void startCrawlers() {
        AWSCrawler amazonCrawler = new AWSCrawler();
        try {
            amazonCrawler.init();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private ThreadPool getThreadPool(String name, int maxThreads,
            int minThreads, int idleTimeout, int queueSize) {

        if (maxThreads <= 0) {
            mainLog.info(String
                    .format("No threadpool for %s will use the server threadpool",
                            name));
            return null;
        }

        mainLog.info(String.format(
                "Creating threadpool %s threads %s queueSize ", name,
                maxThreads, queueSize));

        BlockingQueue<Runnable> queue = null;
        if (queueSize > 0) {
            queue = new ArrayBlockingQueue<Runnable>(queueSize);
        }
        // else it defaults to the unbounded BlockingArrayQueue if null (not a
        // good idea)

        if (idleTimeout == 0) {
            idleTimeout = 60000;
        }

        QueuedThreadPool tp = new QueuedThreadPool(maxThreads, minThreads,
                idleTimeout, queue);
        if (name != null)
            tp.setName(name);

        return tp;
    }

    public static void main(String[] args) {
        App server = AppServer.INSATNCE;
        server.start();
    }

    private static class AppServer {
        private static final App INSATNCE = new App();
    }
}
