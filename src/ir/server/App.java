package ir.server;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import ir.config.Configuration;
import ir.crawler.Crawler;

//http://jyaml.sourceforge.net/tutorial.html
public class App {
    private final Server server;
    private List<Crawler> crawlers;
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
        crawlers = getCrawlers();
        crawlers.stream()
        .forEach(
               (crawler) -> {
            crawler.fetch();
        });
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
    private List<Crawler> getCrawlers() {
        Iterable <Crawler> crawlers = Iterables.transform(config.getCrawlers(),
                new Function<String, Crawler>() {

            @Override
            public Crawler apply(String className) {
                Crawler crawler = null;
                try {
                        crawler = (Crawler) Class.forName(className).newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                return crawler;
            }
        });

        return ImmutableList.copyOf(crawlers);
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
