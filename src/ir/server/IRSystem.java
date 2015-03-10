package ir.server;

import ir.crawler.Buy.OverstockCrawler;
import ir.crawler.amazon.*;
import ir.crawler.youtube.YouTubeCrawler;

public class IRSystem {

    public static void main(String[] args) throws Exception {
        // AWSCrawler amazonCrawler = new AWSCrawler();
        // amazonCrawler.init();

        // YouTubeCrawler youtubeCrawler = new YouTubeCrawler();
        // youtubeCrawler.init();

        OverstockCrawler buyCrawler = new OverstockCrawler();
        buyCrawler.init();

    }
}