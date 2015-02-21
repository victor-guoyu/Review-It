package ir.crawler.amazon;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.primitives.Ints;

import ir.server.*;

/**
 * 
 * @author Zhiting Lin Amazon customer review crawler
 *
 */

public class AWSCrawler {
    private Logger               mainLog;
    private SignedRequestsHelper helper;
    private final Level          RESULT = Level.forName("RESULT", 450);
    private String               URL;

    public AWSCrawler() {
        try {
            helper = SignedRequestsHelper.getInstance(Configuration
                    .getInstance().getAWSEndpoint(), Configuration
                    .getInstance().getAWSAccessId(), Configuration
                    .getInstance().getAWSSecreKey());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public Logger getMainLog() {
        return mainLog;
    }

    public void init() {
        Configuration.getInstance();
        mainLog = LogManager.getLogger(AWSCrawler.class);
        mainLog.info("Log Started...");
        try {
            createURL();
            mainLog.info("............FINISH CREATE URL.............");
            XMLReader(URL);
            mainLog.info("............DONE.............");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the REST url
     */
    private void createURL() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("AssociateTag", "com0fd-20");
        params.put("Service", "AWSECommerceService");
        params.put("Operation", "ItemSearch");
        params.put("SearchIndex", "All");
        params.put("Keywords", "fdgsesgegs");
        params.put("IncludeReviewsSummary", "true");
        params.put("ItemPage", "1");

        URL = helper.sign(params);
        mainLog.info(URL);
    }

    /**
     * 
     * @param requestUrl
     *            : callback XML URL
     * @return
     */
    private void XMLReader(String requestUrl) {
        // String title = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(requestUrl);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("Item");

            String title = null;
            String CustomerReviewURL = null;

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node item = nodeList.item(i);
                if (item.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) item;
                    NodeList ItemLinks = element
                            .getElementsByTagName("ItemLink");
                    for (int j = 0; j < ItemLinks.getLength(); j++) {
                        Element CURL = (Element) ItemLinks.item(j);
                        if (CURL.getElementsByTagName("Description").item(0)
                                .getTextContent().contains("Customer Reviews")) {

                            CustomerReviewURL = CURL
                                    .getElementsByTagName("URL").item(0)
                                    .getTextContent();
                            break;

                        }
                    }
                    title = element.getElementsByTagName("Title").item(0)
                            .getTextContent();
                }

                String msg = new StringBuilder()
                .append(title)
                .append(" ")
                .append(CustomerReviewURL).toString();

                mainLog.info(msg);

                RetriveCustomerReview(CustomerReviewURL);
            }
        } catch (Exception e) {
            // throw new RuntimeException(e);
            e.printStackTrace();
        }
    }

    private void RetriveReviews() {

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
        mainLog.info(num);
        mainLog.info(pagingURL);

        // for loop to retrieve all the customer reviews.

        // retrive review
        Elements reviewList = doc.select("div.reviewText");
        for (int i = 0; i < reviewList.size(); i++) {
            String msg = reviewList.get(i).html();
            mainLog.info(msg);
        }
    }
}