package ir.index;

import ir.config.Configuration;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.util.Strings;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

public class Indexer {
    private EnglishAnalyzer enAnalyzer;
    private Directory       indexDir;
    private IndexWriter     indexWriter;

    public Indexer(Directory indexDir) {
        this.indexDir = indexDir;
        enAnalyzer = setUpEnAnalyzer();
        indexWriter = getIndexWriter();
    }

    public void indexParsedDocument(ParsedComment document) {
        Preconditions.checkNotNull(indexWriter,
                "The index writer is not initialized");
        Document newDoc = new Document();
        newDoc.add(new TextField(ParsedComment.Fields.SEARCHABLE_TEXT.name(),
                document.fullSearchableText(), Field.Store.YES));
        newDoc.add(new StringField(ParsedComment.Fields.ID.name(),
                document.id, Field.Store.YES));
        newDoc.add(new StringField(ParsedComment.Fields.PRODUCT_NAME.name(),
                document.productName, Field.Store.YES));
        newDoc.add(new StringField(ParsedComment.Fields.COMMENT.name(),
                document.comment, Field.Store.YES));
        newDoc.add(new StringField(ParsedComment.Fields.URL.name(),
                document.commentUrl, Field.Store.YES));
        newDoc.add(new StringField(ParsedComment.Fields.SOURCE.name(),
                document.source.name(), Field.Store.YES));
        try {
            indexWriter.addDocument(newDoc);
            indexWriter.commit();
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not write new document to the index directory", e);
        }
    }

    public Analyzer getAnalyzer() {
        return enAnalyzer;
    }

    public void closeIndexWriter() {
        if (indexWriter != null) {
            try {
                indexWriter.close();
            } catch (IOException e) {
                throw new RuntimeException("Unable to close indexWriter", e);
            }
        }
    }

    private EnglishAnalyzer setUpEnAnalyzer() {
        return  new EnglishAnalyzer(getStopWordSet());
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
            throw new RuntimeException("Unable to initialize index writer: ", e);
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
}
