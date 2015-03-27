package ir.crawler.overstock;

import ir.config.Configuration;
import ir.crawler.Crawler;
import ir.crawler.Source;
import ir.crawler.UUIDgenerator;
import ir.index.ParsedComment;
import ir.index.SearchEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OverstockCrawler extends Crawler{
    private Logger logger;
    private static final String PRODUCT_LINK_SELECTOR = "#result-products > li > div > a.reviewcountlink";
    private static final String PRODUCT_TITLE_SELCTOR = "head > title";
    private static final String PRODUCT_COMMENT_SELCTOR = "head > title";

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
            for (String productUrl : productUrls) {
                List<ParsedComment> pl = parse(productUrl);
                parsedList.addAll(pl);
            }
            SearchEngine.getSearchEngine().indexDocuments(parsedList);
        }
    }


    private List<ParsedComment> parse(String productUrl) {
        List<ParsedComment> parsedList = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(productUrl).get();
            Elements CommentElement = doc.select(PRODUCT_COMMENT_SELCTOR);
            String ProductTitle = getProductTitle(doc);

            for (Element comment : CommentElement) {
                parsedList.add(new ParsedComment
                        .Builder(UUIDgenerator.get(), Source.OVERSTOCK)
                        .productName(ProductTitle)
                        .comment(comment.html())
                        .commentUrl(productUrl)
                        .build());
            }
        } catch (IOException e) {
            logger.error("Unable to retrieve product url: %s", productUrl);
        }
        return parsedList;
    }

    /**
     * Retrieve all the product urls listed under the result page
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
}
