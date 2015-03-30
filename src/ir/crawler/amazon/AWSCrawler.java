package ir.crawler.amazon;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

import ir.config.Configuration;
import ir.crawler.Crawler;
import ir.index.ParsedComment;
import static ir.crawler.amazon.AWSRequestFileds.*;

/**
 *
 * @author Zhiting Lin Amazon customer review crawler
 *
 *         http://docs.aws.amazon.com/AWSECommerceService/latest/DG/ItemSearch.
 *         html
 */

public class AWSCrawler extends Crawler {
    private static final int     MAX_PAGE_NUM = 10;
    private Configuration        config;
    private Logger               logger;
    private SignedRequestsHelper helper;
    private String[]             pageLinksToVisit;

    @Override
    public void fetch(List<String> queries) {
        if (!isInitialized()) {
            init();
        }
        queries.stream().forEach((query) -> {
               retrieveContent(query);
        });
    }

    public void init()  {
        config = Configuration.getInstance();
        logger = LogManager.getLogger(AWSCrawler.class);
        try {
            helper = SignedRequestsHelper
                    .getInstance(
                            config.getAwsEndPoint(),
                            config.getAwsAccessKeyId(),
                            config.getAwsSecretKey());
        } catch (Exception e) {
            throw new RuntimeException("AWSCrawler: unable to create helper", e);
        }
    }

    private void retrieveContent(String query) {
        pageLinksToVisit = createURLs();
        List<ParsedComment> comments = Lists.newLinkedList();
        for (String link : pageLinksToVisit) {
           List<ParsedComment>  pageComments = retrievePageComments(link);
        }
    }

    /**
     * Create the URLs
     */
    private String [] createURLs() {
        String[] linksToVisit = new String [MAX_PAGE_NUM];

        HashMap<String, String> params = Maps.newHashMap();
        params.put(PARAM_KEY_ASSOCIATETAG, "com0fd-20");
        params.put(PARAM_KEY_SERVICE, PARAM_VALUE_SERVICE);
        params.put(PARAM_KEY_OPERATION, PARAM_VALUE_OPERATION);
        params.put(PARAM_KEY_SEARCHINDEX, "All");
        params.put(PARAM_KEY_KEYWORDS, "macbook");
        params.put(PARAM_KEY_INCLUDEREVIEWS, PARAM_VALUE_INCLUDEREVIEWS);

        for(int i=0; i< MAX_PAGE_NUM; i++) {
            params.put(PARAM_KEY_ITEMPAGE, String.valueOf(i+1));
            linksToVisit[i] = helper.sign(params);
        }
        return linksToVisit;
    }

    private List<ParsedComment> retrievePageComments(String url) {
        try {
            Document page = Jsoup.connect(url).get();
            List<ParsedComment> pageComment = Lists.newLinkedList();
            Elements items = page.getElementsByTag(ITEM_TAG);
            Iterator<Element> itemsIterator = items.iterator();
            Elements errors = page.getElementsByTag(ERROR_TAG);
            while (itemsIterator.hasNext() && errors.isEmpty()) {
                Element item = itemsIterator.next();
                String title = item.getElementsByTag(TITLE_TAG).get(FIRST_ELEMENT).text();
                for (Element itemLink : item.getElementsByTag(ITEMLINK_TAG)) {
                    if (itemLink.getElementsByTag(DESCRIPTION_TAG).get(FIRST_ELEMENT).text()
                            .contains("Customer Reviews")) {
                        titleToReviews.put(title, itemLink.getElementsByTag(URL_TAG)
                                .get(FIRST_ELEMENT).text());
                    }
                }
            }
        } catch (IOException e) {

        }
    }

    private void retriveReviews(Document doc) {
        Elements reviewList = doc.select("div.reviewText");
        for (int i = 0; i < reviewList.size(); i++) {
            String msg = reviewList.get(i).html();
            logger.info(msg);
        }
    }

    /**
     *
     * @param ReviewURL
     *            : The URL that need to be extracted
     * @throws Exception
     */
    private void RetriveCustomerReview(String ReviewURL) throws Exception {
        Document doc = Jsoup.connect(ReviewURL).get();

        // retrive the reference to the page url
        Elements reviewpageURLList = Jsoup.connect(ReviewURL).get()
                .select(".paging a");
        // total number of page in the web page.
        Integer num = 1;
        String pagingURL = "Not paging avalable";

        for (int j = 0; j < reviewpageURLList.size(); j++) {
            Integer temp = Ints.tryParse(reviewpageURLList.get(j).text());
            if (temp != null && temp > num) {
                num = temp;
                pagingURL = reviewpageURLList.get(j).attr("href");
            }
        }
        // pagingURL = reviewpageURLList.first().attr("href");
        logger.info(num);
        logger.info(pagingURL);

        // for loop to retrieve all the customer reviews.

        // retrive review
        retriveReviews(doc);
    }
}
