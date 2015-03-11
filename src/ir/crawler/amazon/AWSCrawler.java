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

import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

import ir.config.Configuration;
import ir.crawler.Crawler;
import static ir.crawler.amazon.AWSRequestFileds.*;

/**
 *
 * @author Zhiting Lin Amazon customer review crawler
 *
 *         http://docs.aws.amazon.com/AWSECommerceService/latest/DG/ItemSearch.
 *         html
 */

public class AWSCrawler implements Crawler {
    private Logger               logger;
    private SignedRequestsHelper helper;
    private String[]             pageLinksToVisit;
    private HashMap<String, String> titleToReviews;

    @Override
    public void fetch(List<String> queries) {
        // TODO Auto-generated method stub
        System.out.println("Amazon crawler called");

    }

    public void init() throws Exception {
        logger = LogManager.getLogger(AWSCrawler.class);
        helper = SignedRequestsHelper
                .getInstance(
                        Configuration.getInstance().getAwsEndPoint(),
                        Configuration.getInstance().getAwsAccessKeyId(),
                        Configuration.getInstance().getAwsSecretKey());
        pageLinksToVisit = createURLs();
        for (String link : pageLinksToVisit) {
            titleToReviews = retrieveTitleToReviews(link);
            for(Entry<String, String> entry : titleToReviews.entrySet()) {
                System.out.println(entry.getKey());
                System.out.println(entry.getValue());
            }
        }
    }

    /**
     * Create the URLs
     * TODO max to retrieve 10 pages
     */
    private String [] createURLs() {
        String[] linksToVisit = new String [10];

        HashMap<String, String> params = Maps.newHashMap();
        params.put(PARAM_KEY_ASSOCIATETAG, "com0fd-20");
        params.put(PARAM_KEY_SERVICE, PARAM_VALUE_SERVICE);
        params.put(PARAM_KEY_OPERATION, PARAM_VALUE_OPERATION);
        params.put(PARAM_KEY_SEARCHINDEX, "All");
        params.put(PARAM_KEY_KEYWORDS, "macbook");
        params.put(PARAM_KEY_INCLUDEREVIEWS, PARAM_VALUE_INCLUDEREVIEWS);

        for(int i=0; i< linksToVisit.length; i++) {
            params.put(PARAM_KEY_ITEMPAGE, String.valueOf(i+1));
            linksToVisit[i] = helper.sign(params);
        }
        return linksToVisit;
    }

    private HashMap<String, String> retrieveTitleToReviews(String url) throws IOException {
        Document page = null;
        HashMap<String, String> titleToReviews = Maps.newHashMap();
        try {
            page = Jsoup.connect(url).get();
        } catch (IOException e) {
            return titleToReviews;
        }
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
        return titleToReviews;
    }

    private void retriveReviews(org.jsoup.nodes.Document doc) {
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
        org.jsoup.nodes.Document doc = Jsoup.connect(ReviewURL).get();

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
