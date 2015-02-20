package ir.server;

import ir.crawler.amazon.AWSCrawler;

public class Server {

    public void init() {
        AWSCrawler amazonCrawler = new AWSCrawler();
        try {
            amazonCrawler.init();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Server server = new Server();
        server.init();
    }
}
