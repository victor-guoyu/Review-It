package ir.index;

import ir.config.Configuration;
import ir.crawler.Source;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
    private Indexer indexer;
    private Directory indexDir;
    private IndexSearcher searcher;
    private QueryParser queryParser;

    private SearchEngine() {
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

    public List<ParsedComment> search(String userQuery) {
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

    public void indexDocuments(List<ParsedComment> newDocs) {
        newDocs.stream().forEach((doc) -> {
            indexer.indexParsedDocument(doc);
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
