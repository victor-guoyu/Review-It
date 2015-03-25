package ir.crawler.twitter;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import ir.config.Configuration;
import ir.crawler.Crawler;

import java.util.List;

public class TwitterCrawler implements Crawler {
    @Override
    public void fetch(List<String> queries) {
        // TODO Auto-generated method stub
        System.out.println("Twitter crawler called");

        Twitter twitter = createTwitterInstance();
        try {
            Query query = new Query("Macbook");
            QueryResult result = twitter.search(query);
            List<Status> tweets = result.getTweets();
            int size = tweets.size() < 20 ? tweets.size() : 20;
            for (int i = 0; i < size; i++) {
                System.out.println("@"
                        + tweets.get(i).getUser().getScreenName() + " - "
                        + tweets.get(i).getUser().getBiggerProfileImageURL() + " - "
                        + tweets.get(i).getCreatedAt().toString()  + " - "
                        + tweets.get(i).getText());
            }
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }

    }

    private Twitter createTwitterInstance() {
        String TWITTER_CONSUMER_KEY        = Configuration.getInstance().getTwitterConsumerKey();
        String TWITTER_SECRET_KEY          = Configuration.getInstance().getTwitterSecretKey();
        String TWITTER_ACCESS_TOKEN        = Configuration.getInstance().getTwitterAccessToken();
        String TWITTER_ACCESS_TOKEN_SECRET = Configuration.getInstance().getTwitterAccessTokenSecert();

        
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setDebugEnabled(true).setOAuthConsumerKey(TWITTER_CONSUMER_KEY)
                .setOAuthConsumerSecret(TWITTER_SECRET_KEY)
                .setOAuthAccessToken(TWITTER_ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(TWITTER_ACCESS_TOKEN_SECRET);
        TwitterFactory twitterfactory = new TwitterFactory(builder.build());
        return twitterfactory.getInstance();
    }
}
