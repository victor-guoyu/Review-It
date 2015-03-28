package ir.index;

import ir.config.Configuration;
import ir.config.RTCConfig;
import ir.crawler.Crawler;
import ir.crawler.RealTimeCrawler;
import ir.crawler.Source;
import ir.crawler.twitter.Tweet;
import ir.crawler.youtube.Video;
import ir.utils.Pair;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class SearchEngine {
    private Indexer                         indexer;
    private Directory                       indexDir;
    private IndexSearcher                   searcher;
    private QueryParser                     queryParser;
    private List<Crawler>                   crawlers;
    private Map<String, RealTimeCrawler<?>> realTimeCrawlers;

    private SearchEngine() {
        realTimeCrawlers = getRealTimeCrawlers();
        crawlers = initializeCrawlers();
        indexDir = getIndexDirectory();
        indexer = new Indexer(indexDir);
        queryParser = new QueryParser(
                ParsedComment.Fields.SEARCHABLE_TEXT.name(),
                indexer.getAnalyzer());
        try {
            searcher = new IndexSearcher(DirectoryReader.open(indexDir));
        } catch (IOException e) {
            throw new RuntimeException("Unable to open index path:", e);
        }
    }

    public List<ParsedComment> searchComments(String userQuery) {
        Query query = parseUserQuery(userQuery);
        TopScoreDocCollector docCollector = TopScoreDocCollector.create(
                Configuration.getInstance().getResultSize(), true);
        try {
            searcher.search(query, docCollector);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  parseScoreDocsToList(docCollector.topDocs().scoreDocs);
    }

    /**
     * Retrieve top rated video from YouTube
     * @param query
     * @return Video
     */
    public Video getTopRatedVideo(String query) {
        return (Video) realTimeCrawlers.get(Source.YOUTUBE.name()).fetch(query);
    }

    /**
     * Return top 20 related tweets from twitter
     * @param query
     * @return List<Tweet>
     */
    @SuppressWarnings("unchecked")
    public List<Tweet> getTopTweets(String query) {
        return (List<Tweet>) (List<?>) realTimeCrawlers.get(
                Source.TWITTER.name()).fetch(query);
    }

    /**
     * Index list of parsedComment
     * @param newDocs
     */
    public void indexDocuments(List<ParsedComment> newDocs) {
        newDocs.stream().forEach((doc) -> {
            indexer.indexParsedDocument(doc);
        });
    }

    /**
     * Crawlers will retrieve and index comments in the system
     * @param List of product name queries
     *        Lucene will index all the data being retrieved
     */
    public void retriveData(List<String> queries) {
        crawlers.stream()
        .forEach((crawler) -> {
            crawler.fetch(queries);
        });
    }

    public List<ParsedComment> parseScoreDocsToList(ScoreDoc[] scoreDocs) {
        List<ScoreDoc> scoreDocsList = Arrays.asList(scoreDocs);
        Iterable<ParsedComment> parsedDocList = Iterables.transform(scoreDocsList,
                new Function<ScoreDoc, ParsedComment>() {

                @Override
                public ParsedComment apply(ScoreDoc scoreDoc) {
                    Document hitDoc = null;
                    try {
                        hitDoc = searcher.doc(scoreDoc.doc);
                    } catch (IOException e) {
                        throw new RuntimeException(String.format(
                                "Unable to locate hit doc #%s", scoreDoc.doc), e);
                    }
                    return new ParsedComment
                                .Builder(hitDoc.get(ParsedComment.Fields.ID.name()),
                                        Source.valueOf(hitDoc.get(ParsedComment.Fields.SOURCE.name())))
                                .productName(hitDoc.get(ParsedComment.Fields.PRODUCT_NAME.name()))
                                .comment(hitDoc.get(ParsedComment.Fields.COMMENT.name()))
                                .commentUrl(hitDoc.get(ParsedComment.Fields.URL.name()))
                                .build();
                }
            });
        return ImmutableList.copyOf(parsedDocList);
    }

    /**
     * Load and initialize all the crawlers
     */
    private List<Crawler> initializeCrawlers() {
        Iterable<Crawler> crawlers = Iterables.transform(Configuration.getInstance().getCrawlers(),
                new Function<String, Crawler>() {

                    @Override
                    public Crawler apply(String className) {
                        Crawler crawler = null;
                        try {
                            crawler = (Crawler) Class.forName(className)
                                    .newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return crawler;
                    }
                });

        return ImmutableList.copyOf(crawlers);
    }

    private Function<RTCConfig, Pair<String, RealTimeCrawler<?>>>  toRealTimeMapFunction() {
        return new Function<RTCConfig, Pair<String, RealTimeCrawler<?>>>(){
            @Override
            public Pair<String, RealTimeCrawler<?>> apply(RTCConfig config) {
                try {
                    Class<?> clazz= Class.forName(config.getClassName());
                    RealTimeCrawler<?> crawler = (RealTimeCrawler<?>) clazz.newInstance();
                    return new Pair<String, RealTimeCrawler<?>>(
                                config.getType(),
                                crawler);
                } catch (Exception e) {
                    throw new RuntimeException("Error occur while initialing real-time crawler", e);
                }
            }
        };
    }

    private Map<String, RealTimeCrawler<?>> getRealTimeCrawlers() {
        List<Pair<String, RealTimeCrawler<?>>> entries = ImmutableList.copyOf(Iterables
                .transform(Configuration.getInstance().getRealTimeCrawlers(),
                            toRealTimeMapFunction()));

        return entries.stream().collect(
                Collectors.toMap(Pair<String, RealTimeCrawler<?>>::getKey,
                                 Pair<String, RealTimeCrawler<?>>::getValue));
    }

    private Query parseUserQuery(String userQuery) {
        try {
            return queryParser.parse(userQuery);
        } catch (ParseException e) {
            throw new RuntimeException(String.format(
                    "Unable to parse the user query %s", userQuery), e);
        }
    }

    private Directory getIndexDirectory() {
        File indexPath = new File(Configuration.getInstance().getIndexDir());
        if (!indexPath.exists()) {
            indexPath.mkdirs();
        }

        try {
            return FSDirectory.open(indexPath);
        } catch (IOException e) {
            throw new RuntimeException("Unbale to find index path: ", e);
        }
    }

    public static SearchEngine getSearchEngine() {
        return SingletonSearchEngine.INSTANCE;
    }

    private static class SingletonSearchEngine {
        private static final SearchEngine INSTANCE = new SearchEngine();
    }
}
