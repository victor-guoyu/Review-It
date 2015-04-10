package ir.crawler.overstock;

import ir.config.Configuration;
import ir.crawler.Crawler;
import ir.crawler.Source;
import ir.crawler.UUIDgenerator;
import ir.index.ParsedComment;
import ir.index.SearchEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

public class OverstockCrawler extends Crawler {
    private Logger              logger;
    private static final String PRODUCT_LINK_SELECTOR          = "#result-products > li > div > a.reviewcountlink";
    private static final String PRODUCT_TITLE_SELCTOR          = "head > title";
    private static final String PRODUCT_COMMENT_SELCTOR        = "[id*=reviewText] > p";
    private static final String PRODUCT_COMMENTRATING_SELECTOR = "[id*= starImage]";
    private static final String PRODUCT_COMMENTTEXT_PREFIX     = "reviewText";
    private static final String PRODUCT_COMMENTRATING_PREFIX   = "starImage";
    private static final int    MAX_STAR_RATING                = 5;

    @Override
    public void fetch(List<String> queries) {
        if (!isInitialized()) {
            init();
        }
        queries.stream().forEach((query) -> {
            retrieveContent(query);
        });
    }

    /**
     * Overstock Crawler Initialization
     */
    private void init() {
        logger = LogManager.getLogger(OverstockCrawler.class);
        setInitialized(true);
    }

    private void retrieveContent(String query) {
        String inquiredURL = String.format(Configuration.getInstance()
                .getOverStockUrl(), query);
        List<String> productUrls = generateProductUrls(inquiredURL);

        if (!productUrls.isEmpty()) {
            List<ParsedComment> parsedList = new ArrayList<>();
            // generate all the list
            productUrls.stream().forEach((url) -> {
                List<ParsedComment> comments = parse(url);
                parsedList.addAll(comments);
            });
            if (!parsedList.isEmpty()) {
                SearchEngine.getSearchEngine().indexDocuments(parsedList);
            }
        }
    }

    private List<ParsedComment> parse(String productUrl) {
        List<ParsedComment> parsedList = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(productUrl).get();
            // create mapping relationship
            Elements CommentElements = doc.select(PRODUCT_COMMENT_SELCTOR);
            HashMap<Integer, String> commentTextMap = getCommentTextMap(
                    CommentElements, PRODUCT_COMMENTTEXT_PREFIX);
            Elements commentRatingElements = doc
                    .select(PRODUCT_COMMENTRATING_SELECTOR);
            HashMap<Integer, String> commentRatingMap = getCommentRatingMap(
                    commentRatingElements, PRODUCT_COMMENTRATING_PREFIX);

            String ProductTitle = getProductTitle(doc);

            commentTextMap.forEach((key, comment) -> {
                String commentLabel = commentRatingMap.get(key);
                parsedList.add(new ParsedComment.Builder(UUIDgenerator.get(),
                        Source.OVERSTOCK)
                        .productName(ProductTitle)
                        .comment(comment)
                        .commentUrl(productUrl)
                        .commentLabel(commentLabel).build());
            });
        } catch (IOException e) {
            logger.error("Unable to retrieve product url: "+ productUrl);
        }
        return parsedList;
    }

    /**
     * Retrieve all the product urls listed under the result page
     *
     * @param inquiredURL
     * @return List of product urls
     */
    private List<String> generateProductUrls(String inquiredURL) {
        List<String> ProductURLs = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(inquiredURL).get();
            Elements productList = doc.select(PRODUCT_LINK_SELECTOR);
            productList.stream().forEach((product) -> {
                ProductURLs.add(product.attr("href"));
            });
        } catch (IOException e) {
            logger.error("Unable to retrieve inquired url: %s", inquiredURL);
        }
        return ProductURLs;
    }

    private String getProductTitle(Document doc) {
        String title = doc.select(PRODUCT_TITLE_SELCTOR).first().html();
        title = title.replace(" | Overstock.com", "");
        title = title.replace("Product Reviews: ", "");
        return title;
    }

    private HashMap<Integer, String> getCommentTextMap(Elements elems,
            String prefix) {
        HashMap<Integer, String> params = Maps.newHashMap();
        elems.stream().forEach((elem) -> {
            String elemId = elem.parent().id().replace(prefix, "");
            Integer Id = Ints.tryParse(elemId);
            params.put(Id, elem.html());
        });
        return params;
    }

    private HashMap<Integer, String> getCommentRatingMap(Elements elems,
            String prefix) {
        HashMap<Integer, String> params = Maps.newHashMap();
        elems.stream().forEach((elem) -> {
            String elemId = elem.id().replace(prefix, "");
            Integer Id = Ints.tryParse(elemId);
            String ratingImgUrl = elem.attr("src");
            String commentLabel = setLabel(ratingImgUrl);
            params.put(Id, commentLabel);
        });
        return params;
    }

    private String setLabel(String imgUrl) {
        String rate = imgUrl.substring(imgUrl.lastIndexOf("s") + 1);
        rate = rate.replace("_0.gif", "");
        String result = setLabel(Ints.tryParse(rate), MAX_STAR_RATING);
        return result;
    }
}
