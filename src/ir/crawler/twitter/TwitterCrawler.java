package ir.crawler.twitter;

import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import ir.config.Configuration;
import ir.crawler.RealTimeCrawler;
import ir.crawler.youtube.YouTubeCrawler;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TwitterCrawler extends RealTimeCrawler<List<Tweet>>{
    private Logger        logger;
    private Configuration config;
    private Twitter       twitter;
    private String        TWITTER_CONSUMER_KEY;
    private String        TWITTER_SECRET_KEY;
    private String        TWITTER_ACCESS_TOKEN;
    private String        TWITTER_ACCESS_TOKEN_SECRET;

    public void init() {
        logger = LogManager.getLogger(YouTubeCrawler.class);
        config = Configuration.getInstance();
        TWITTER_CONSUMER_KEY = config.getTwitterConsumerKey();
        TWITTER_SECRET_KEY = config.getTwitterSecretKey();
        TWITTER_ACCESS_TOKEN = config.getTwitterAccessToken();
        TWITTER_ACCESS_TOKEN_SECRET = config.getTwitterAccessTokenSecert();
        twitter = createTwitterInstance();
        this.setInitialized(true);
    }

    @Override
    public List<Tweet> fetch(String queryString) {
        List<Tweet> tweets = new LinkedList<>();
        if (!isInitialized()) {
             init();
        }
        try {
            Query query = new Query(queryString);
            List<Status> hits = twitter.search(query).getTweets();
            int size = hits.size() < 20 ? hits.size() : 20;
            for (int i = 0; i < size; i++) {
                Status status = hits.get(i);
                Tweet tweet = new Tweet.Builder()
                    .screenName(status.getUser().getScreenName())
                    .profileUrl(status.getUser().getBiggerProfileImageURL())
                    .createdTime(status.getCreatedAt().toString())
                    .tweet(status.getText())
                    .build();
                tweets.add(tweet);
            }
        } catch (TwitterException e) {
            logger.error("Failed to search tweets: %s", e.getMessage());
            e.printStackTrace();
        }
        return tweets;
    }

    private Twitter createTwitterInstance() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setDebugEnabled(true).setOAuthConsumerKey(TWITTER_CONSUMER_KEY)
        .setOAuthConsumerSecret(TWITTER_SECRET_KEY)
        .setOAuthAccessToken(TWITTER_ACCESS_TOKEN)
        .setOAuthAccessTokenSecret(TWITTER_ACCESS_TOKEN_SECRET);
        TwitterFactory twitterfactory = new TwitterFactory(builder.build());
        return twitterfactory.getInstance();
    }
}
