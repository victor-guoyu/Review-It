package ir.server;

import ir.crawler.amazon.*;

public class IRSystem {
    
    
    public static void main(String[] args) {
        AWSCrawler amazonCrawler = new AWSCrawler();
        amazonCrawler.init();
    }
}
