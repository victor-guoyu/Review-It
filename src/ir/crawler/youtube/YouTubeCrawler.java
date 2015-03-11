package ir.crawler.youtube;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import ir.config.Configuration;
import ir.crawler.Crawler;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YouTubeCrawler implements Crawler{
    private static final long NUMBER_OF_VIDEOS_RETURNED = 1;

    /**
     * Define a global instance of a Youtube object, which will be used to make
     * YouTube Data API requests.
     */
    private static YouTube    youtube;
    private Logger            logger;

    @Override
    public void fetch(List<String> queries) {
        // TODO Auto-generated method stub
        System.out.println("YouTube Crawler called");
    }

    public void init() throws Exception {
        logger = LogManager.getLogger(YouTubeCrawler.class);
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(),
                new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request)
                            throws IOException {
                    }
                }).setApplicationName("Youtube-Crawler").build();

        String keyword = "Macbook";

        YouTube.Search.List search = searchConfig(keyword);

        // Call the API and print results.
        SearchListResponse searchResponse = search.execute();
        List<SearchResult> searchResultList = searchResponse.getItems();
        if (searchResultList != null) {
            prettyPrint(searchResultList.iterator(), keyword);
        }
    }

    private YouTube.Search.List searchConfig(String keyword) throws IOException {
        YouTube.Search.List search = youtube.search().list("id,snippet");
        String apiKey = Configuration.getInstance().getYoutubeApiKey();
        search.setKey(apiKey);
        search.setQ(keyword + " review");

        // Restrict the search results to only include videos. See:
        search.setType("video");

        // To increase efficiency, only retrieve the fields that the
        // application uses.
        search.setFields("items(id/kind,id/videoId,snippet/title,snippet/publishedAt)");

        search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
        search.setOrder("viewCount");
        search.setVideoEmbeddable("true");

        return search;
    }

    /**
     * Prints out all results in the Iterator. For each result, print the title,
     * video ID
     *
     * @param iteratorSearchResults
     *            Iterator of SearchResults to print
     *
     * @param query
     *            Search query (String)
     */
    private void prettyPrint(Iterator<SearchResult> iteratorSearchResults,
            String query) {

        System.out
                .println("\n=============================================================");
        System.out.println("   First " + NUMBER_OF_VIDEOS_RETURNED
                + " videos for search on \"" + query + "\".");
        System.out
                .println("=============================================================\n");

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {

                System.out.println(" Video Id： " + rId.getVideoId());
                System.out.println(" Title: "
                        + singleVideo.getSnippet().getTitle());
                System.out.println(" Publish at: "
                        + singleVideo.getSnippet().getPublishedAt().toString());
                logger.info("<iframe width='420' height='315' src='https://www.youtube.com/embed/"
                        + rId.getVideoId()
                        + "' frameborder'0' allowfullscreen></iframe>");
                System.out
                        .println("\n-------------------------------------------------------------\n");
            }
        }
    }
}
