package ir.index;

import ir.config.Configuration;
import ir.index.ParsedDocument.Fields;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.util.Strings;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.surround.parser.QueryParser;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

public class Indexer {
    private EnglishAnalyzer enAnalyzer;
    private QueryParser     queryParser;
    private Directory       indexDir;
    private IndexWriter     indexWriter;

    private Indexer() {
        indexDir = getIndexDirectory();
        enAnalyzer = setUpEnAnalyzer();
        indexWriter = getIndexWriter();
    }
//            //A field that is indexed but not tokenized:
//            //For example this might be used for a 'country' field or an 'id' field
//            doc.add(new StringField(TwitterMsg.ID.name(), temp[0], Field.Store.YES));
//            // Indexed, tokenized
//            doc.add(new TextField(TwitterMsg.TWEET.name(), temp[1], Field.Store.YES));
//            indexWriter.addDocument(doc);
//        }
//        reader.close();
//        indexWriter.close();
//    }

    public void indexParsedDocument(ParsedDocument document) {
        Preconditions.checkNotNull(indexWriter, "The index writer is not initialized");
        Document newDoc = new Document();
        newDoc.add(new StringField(ParsedDocument.Fields.DOC_ID.name(), document.documentId, Field.Store.YES));
        newDoc.add(new TextField(ParsedDocument.Fields.DOC_BODY.name(), document.body, Field.Store.YES));
        newDoc.add(new TextField(ParsedDocument.Fields.PRODUCT_NAME.name(), document.productName, Field.Store.YES));
        newDoc.add(new StringField(ParsedDocument.Fields.DOC_URL.name(), document.commentUrl, Field.Store.YES));
        newDoc.add(new StringField(ParsedDocument.Fields.SOURCE.name(), document.source.name(), Field.Store.YES));

    }

    private EnglishAnalyzer setUpEnAnalyzer() {
        return  new EnglishAnalyzer(getStopWordSet());
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

    /**
     * @return IndexWriter based on the Stop words provided
     */
    private IndexWriter getIndexWriter() {
        Preconditions.checkNotNull(indexDir, "No index directory specified!");
        Preconditions.checkNotNull(enAnalyzer, "The English analyzer has not initialized.");
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, enAnalyzer);
        try {
            return new IndexWriter(indexDir, config);
        } catch (IOException e) {
            throw new RuntimeException("Unable to find  Stopwords file: ", e);
        }
    }

    private CharArraySet getStopWordSet() {
        File stopWordsFile = new File(Configuration.getInstance().getStopWord());
        try {
            return Files.readLines(stopWordsFile,
                    Charsets.UTF_8,

                    new LineProcessor<CharArraySet>() {
                        CharArraySet result = new CharArraySet(Configuration
                                                    .getInstance()
                                                    .getStopWordSize(), true);

                        @Override
                        public CharArraySet getResult() {
                            return result;
                        }

                        @Override
                        public boolean processLine(String line) {
                            if (Strings.isNotBlank(line)) {
                                result.add(line.trim());
                            }
                            return true;
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Unable to find  Stopwords file: ", e);
        }
    }

    public static Indexer getIndexer() {
        return SingletonIndexer.INSTANCE;
    }

    private static class SingletonIndexer {
        public static final Indexer INSTANCE = new Indexer();
    }
}
