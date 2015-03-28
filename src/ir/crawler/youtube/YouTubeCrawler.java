package ir.crawler.youtube;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;

import ir.config.Configuration;
import ir.crawler.RealTimeCrawler;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YouTubeCrawler extends RealTimeCrawler<Video> {
    private static final long NUMBER_OF_VIDEOS_RETURNED = 1;
    private Configuration     config;
    private YouTube           youtube;
    private Logger            logger;

    @Override
    public Video fetch(String query) {
        if (!isInitialized()) {
            init();
        }
        YouTube.Search.List search = searchConfig(query);
        Video video = null;
        try {
            List<SearchResult> searchResultList = search.execute().getItems();
            if (searchResultList != null) {
                String videoId = getVideoId(searchResultList.iterator());
                video = new Video(videoId);
            }
        } catch (IOException e) {
            logger.error("Unable to get video for query %s", query);
        }
        return video;
    }

    public void init() {
        logger = LogManager.getLogger(YouTubeCrawler.class);
        config = Configuration.getInstance();
        youtube = new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(), new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request)
                            throws IOException {
                    }
                }).setApplicationName("Youtube-Crawler").build();
    }

    private YouTube.Search.List searchConfig(String query) {
        try {
            YouTube.Search.List search = youtube.search().list("id,snippet");
            String apiKey = config.getYoutubeApiKey();
            search.setKey(apiKey);
            search.setQ(query + " review");

            // Restrict the search results to only include videos. See:
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/publishedAt)");

            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
            search.setOrder("viewCount");
            search.setVideoEmbeddable("true");
            return search;
        } catch (IOException e) {
            logger.error("Unable to setup searcher for query %s", query);
            throw new RuntimeException();
        }
    }

    /**
     * Get video ID
     * @param iteratorSearchResults
     */
    private String getVideoId(Iterator<SearchResult> iteratorSearchResults) {
        String id = "";
        while (iteratorSearchResults.hasNext()) {
            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();
            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                id = rId.getVideoId();
            }
        }
        return id;
    }
}
