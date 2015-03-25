package ir.config;

import ir.server.ServerConstants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

public final class Configuration {
    // Server configuration
    private int                 serverPort;
    private String              publicDir;
    private List<ServletConfig> servlets;
    private List<String>        crawlers;
    private List<String>        wlecomePages;

    // Indexer configuration
    private String              indexDir;
    private String              stopWord;
    private int                 stopWordSize;
    private int                 resultSize;

    // AWS configuration
    private String              awsAccessKeyId;
    private String              awsSecretKey;
    private String              awsEndPoint;

    // YouTuBe configuration
    private String              youtubeApiKey;

    // Over Stock configuration
    private String              overStockUrl;

    // Twitter configuration
    private String              twitterConsumerKey;
    private String              twitterSecretKey;
    private String              twitterAccessToken;
    private String              twitterAccessTokenSecert;

    private Configuration() {
    }

    public static Configuration getInstance() {
        return SingletonConfiguration.INSTANCE;
    }

    private static Configuration getConfiguration() {
        Configuration config = null;
        try {
            config = new Yaml()
                    .loadAs(Files.newInputStream(Paths
                            .get(ServerConstants.APP_CONFIG_FILE)),
                            Configuration.class);
        } catch (IOException e) {
            throw new RuntimeException("Unable to find  property YAML file", e);
        }
        return config;
    }

    /**
     * @return the serverPort
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * @param serverPort
     *            the serverPort to set
     */
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * @return the publicDir
     */
    public String getPublicDir() {
        return publicDir;
    }

    /**
     * @param publicDir
     *            the publicDir to set
     */
    public void setPublicDir(String publicDir) {
        this.publicDir = publicDir;
    }

    /**
     * @return the servlets
     */
    public List<ServletConfig> getServlets() {
        return servlets;
    }

    /**
     * @param servlets
     *            the servlets to set
     */
    public void setServlets(List<ServletConfig> servlets) {
        this.servlets = servlets;
    }

    /**
     * @return the crawlers
     */
    public List<String> getCrawlers() {
        return crawlers;
    }

    /**
     * @param crawlers
     *            the crawlers to set
     */
    public void setCrawlers(List<String> crawlers) {
        this.crawlers = crawlers;
    }

    /**
     * @return the wlecomePages
     */
    public List<String> getWlecomePages() {
        return wlecomePages;
    }

    /**
     * @param wlecomePages
     *            the wlecomePages to set
     */
    public void setWlecomePages(List<String> wlecomePages) {
        this.wlecomePages = wlecomePages;
    }

    /**
     * @return the awsAccessKeyId
     */
    public String getAwsAccessKeyId() {
        return awsAccessKeyId;
    }

    /**
     * @param awsAccessKeyId
     *            the awsAccessKeyId to set
     */
    public void setAwsAccessKeyId(String awsAccessKeyId) {
        this.awsAccessKeyId = awsAccessKeyId;
    }

    /**
     * @return the awsSecretKey
     */
    public String getAwsSecretKey() {
        return awsSecretKey;
    }

    /**
     * @param awsSecretKey
     *            the awsSecretKey to set
     */
    public void setAwsSecretKey(String awsSecretKey) {
        this.awsSecretKey = awsSecretKey;
    }

    /**
     * @return the awsEndPoint
     */
    public String getAwsEndPoint() {
        return awsEndPoint;
    }

    /**
     * @param awsEndPoint
     *            the awsEndPoint to set
     */
    public void setAwsEndPoint(String awsEndPoint) {
        this.awsEndPoint = awsEndPoint;
    }

    /**
     * @return the youtubeApiKey
     */
    public String getYoutubeApiKey() {
        return youtubeApiKey;
    }

    /**
     * @param youtubeApiKey
     *            the youtubeApiKey to set
     */
    public void setYoutubeApiKey(String youtubeApiKey) {
        this.youtubeApiKey = youtubeApiKey;
    }

    /**
     * @return the overStockUrl
     */
    public String getOverStockUrl() {
        return overStockUrl;
    }

    /**
     * @param overStockUrl
     *            the overStockUrl to set
     */
    public void setOverStockUrl(String overStockUrl) {
        this.overStockUrl = overStockUrl;
    }
    
    /**
     * 
     * @return twitterAccessTokenSecert
     */
    public String getTwitterAccessTokenSecert() {
        return twitterAccessTokenSecert;
    }

    /**
     * 
     * @param twitterAccessTokenSecert 
     *              the twitterAccessTokenSecert to set
     */
    public void setTwitterAccessTokenSecert(String twitterAccessTokenSecert) {
        this.twitterAccessTokenSecert = twitterAccessTokenSecert;
    }

    /**
     * 
     * @return twitterAccessToken
     */
    public String getTwitterAccessToken() {
        return twitterAccessToken;
    }

    /**
     * 
     * @param twitterAccessToken
     *              the twitterAccessToken to set
     */
    public void setTwitterAccessToken(String twitterAccessToken) {
        this.twitterAccessToken = twitterAccessToken;
    }

    /**
     * 
     * @return twitterSecretKey
     */
    public String getTwitterSecretKey() {
        return twitterSecretKey;
    }

    /**
     * 
     * @param twitterSecretKey
     *              the twitterSecretKey to set
     */
    public void setTwitterSecretKey(String twitterSecretKey) {
        this.twitterSecretKey = twitterSecretKey;
    }

    public String getTwitterConsumerKey() {
        return twitterConsumerKey;
    }

    public void setTwitterConsumerKey(String twitterConsumerKey) {
        this.twitterConsumerKey = twitterConsumerKey;
    }

    /**
     * @return the indexDir
     */
    public String getIndexDir() {
        return indexDir;
    }

    /**
     * @param indexDir
     *            the indexDir to set
     */
    public void setIndexDir(String indexDir) {
        this.indexDir = indexDir;
    }

    /**
     * @return the stopWord
     */
    public String getStopWord() {
        return stopWord;
    }

    /**
     * @param stopWord
     *            the stopWord to set
     */
    public void setStopWord(String stopWord) {
        this.stopWord = stopWord;
    }

    /**
     * @return the stopWordSize
     */
    public int getStopWordSize() {
        return stopWordSize;
    }

    /**
     * @param stopWordSize
     *            the stopWordSize to set
     */
    public void setStopWordSize(int stopWordSize) {
        this.stopWordSize = stopWordSize;
    }

    /**
     * @return the resultSize
     */
    public int getResultSize() {
        return resultSize;
    }

    /**
     * @param resultSize the resultSize to set
     */
    public void setResultSize(int resultSize) {
        this.resultSize = resultSize;
    }

    @Override
    public String toString() {
        return String.format(
                "Server listening on port: %s serving directory: %s",
                serverPort, publicDir);
    }

   

    private static class SingletonConfiguration {
        private static final Configuration INSTANCE = Configuration
                                                            .getConfiguration();
    }
}
