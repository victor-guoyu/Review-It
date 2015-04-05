package ir.crawler.twitter;

public class Tweet {
    private String screenName;
    private String profileUrl;
    private String createdTime;
    private String tweet;

    private Tweet(Builder builder) {
        this.screenName = builder.screenName;
        this.profileUrl = builder.profileUrl;
        this.createdTime = builder.createdTime;
        this.tweet = builder.tweet;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    @Override
    public String toString() {
        return String
                .format("[screenName = %s, profileUrl: %s, createdTime: %s, tweet: %s]",
                        screenName, profileUrl, createdTime, tweet);
    }

    public static class Builder {
        private String screenName;
        private String profileUrl;
        private String createdTime;
        private String tweet;

        public Builder screenName(String val) {
            this.screenName = val;
            return this;
        }

        public Builder profileUrl(String val) {
            this.profileUrl = val;
            return this;
        }

        public Builder createdTime(String val) {
            this.createdTime = val;
            return this;
        }

        public Builder tweet(String val) {
            this.tweet = val;
            return this;
        }

        public Tweet build() {
            return new Tweet(this);
        }
    }
}
