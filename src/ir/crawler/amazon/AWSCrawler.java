package ir.crawler.amazon;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
import ir.crawler.Source;
import ir.crawler.UUIDgenerator;
import ir.index.ParsedComment;
import ir.index.SearchEngine;
import static ir.crawler.amazon.AWSRequestFileds.*;

/**
 *
 * @author Zhiting Lin Amazon customer review crawler
 *
 *         http://docs.aws.amazon.com/AWSECommerceService/latest/DG/ItemSearch.
 *         html
 */

public class AWSCrawler extends Crawler {
    private static final int     MAX_PAGE_NUM   = 1;
    private static final int     MAX_RATING_NUM = 5;
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

    public void init() {
        config = Configuration.getInstance();
        logger = LogManager.getLogger(AWSCrawler.class);
        try {
            helper = SignedRequestsHelper.getInstance(config.getAwsEndPoint(),
                    config.getAwsAccessKeyId(), config.getAwsSecretKey());
        } catch (Exception e) {
            throw new RuntimeException("AWSCrawler: unable to create helper", e);
        }
    }

    /**
     * Retrieve comments based on user query
     * 
     * @param query
     */
    private void retrieveContent(String query) {
        pageLinksToVisit = createURLs(query);
        List<ParsedComment> comments = Lists.newLinkedList();
        for (String link : pageLinksToVisit) {
            List<ParsedComment> pageComments = retrievePageComments(link);
            comments.addAll(pageComments);
        }
        if (!comments.isEmpty()) {
            SearchEngine.getSearchEngine().indexDocuments(comments);
        }
    }

    /**
     * Create the URLs
     */
    private String[] createURLs(String query) {
        String[] linksToVisit = new String[MAX_PAGE_NUM];

        HashMap<String, String> params = Maps.newHashMap();
        params.put(PARAM_KEY_ASSOCIATETAG, "com0fd-20");
        params.put(PARAM_KEY_SERVICE, PARAM_VALUE_SERVICE);
        params.put(PARAM_KEY_OPERATION, PARAM_VALUE_OPERATION);
        params.put(PARAM_KEY_SEARCHINDEX, "All");
        params.put(PARAM_KEY_KEYWORDS, query);
        params.put(PARAM_KEY_INCLUDEREVIEWS, PARAM_VALUE_INCLUDEREVIEWS);

        for (int i = 0; i < MAX_PAGE_NUM; i++) {
            params.put(PARAM_KEY_ITEMPAGE, String.valueOf(i + 1));
            linksToVisit[i] = helper.sign(params);
        }
        return linksToVisit;
    }

    private List<ParsedComment> retrievePageComments(String url) {
        List<ParsedComment> pageComment = Lists.newLinkedList();
        try {
            Document page = Jsoup.connect(url).get();
            Elements items = page.getElementsByTag(ITEM_TAG);
            Iterator<Element> itemsIterator = items.iterator();
            Elements errors = page.getElementsByTag(ERROR_TAG);
            while (itemsIterator.hasNext() && errors.isEmpty()) {
                Element item = itemsIterator.next();
                String title = item.getElementsByTag(TITLE_TAG)
                        .get(FIRST_ELEMENT).text();
                for (Element itemLink : item.getElementsByTag(ITEMLINK_TAG)) {
                    if (itemLink.getElementsByTag(DESCRIPTION_TAG)
                            .get(FIRST_ELEMENT).text()
                            .contains("Customer Reviews")) {

                        pageComment.addAll(RetriveParsedCustomerReview(
                                title,
                                itemLink.getElementsByTag(URL_TAG)
                                        .get(FIRST_ELEMENT).text()));
                    }
                }
            }
        } catch (IOException e) {
            String msg = String.format("Unable to parse link %s", url);
            logger.error(msg);
        }
        return pageComment;

    }

    private List<List<String>> retriveReviews(String pagingUrl) {
        List<List<String>> commentlist = Lists.newLinkedList();
        try {
            Document doc = Jsoup.connect(pagingUrl).get();
            Elements reviewList = doc.select("span.review-text");
            for (int i = 0; i < reviewList.size(); i++) {
                Element ratingElement = reviewList.get(i);
                String comment = ratingElement.html();
                String Label = getLabel(ratingElement);
                List<String> commentNode = Lists.newLinkedList();
                commentNode.add(comment);
                commentNode.add(Label);
                commentlist.add(commentNode);
            }

        } catch (IOException e) {
            String msg = String.format("Unable to parse link %s", pagingUrl);
            logger.error(msg);
        }
        return commentlist;

    }

    /**
     * 
     * @param elem
     *            the rating element to analysis
     * @return the positive/negative...
     */
    private String getLabel(Element elem) {
        Elements ratedNode = elem.parent().parent().select(".a-icon-alt");
        int rate = Ints.tryParse(ratedNode.html());
        return setLabel(rate, MAX_RATING_NUM);
    }

    /**
     *
     * @param ReviewURL
     *            : The URL that need to be extracted
     * @throws Exception
     */
    private List<ParsedComment> RetriveParsedCustomerReview(
            String productTitle, String ReviewURL) {
        Document doc;
        List<ParsedComment> Comments = Lists.newLinkedList();
        try {
            doc = Jsoup.connect(ReviewURL).get();
            // retrive the reference to the paging url
            Elements reviewpageURLList = doc
                    .select("#cm_cr-pagination_bar > ul > li> a");
            // total number of page in the web page.
            Integer num = 1;
            String CommonPagingURL = "";

            for (int j = 0; j < reviewpageURLList.size(); j++) {
                Integer temp = Ints.tryParse(reviewpageURLList.get(j).text());
                // find the paging with max number
                if (temp != null && temp > num) {
                    num = temp;
                    CommonPagingURL = reviewpageURLList.get(j).attr("href");
                }
            }

            if (reviewpageURLList.isEmpty()) {
                logger.info("Not paging avalable");
            } else {

                for (int i = 1; i <= num; i++) {
                    String pagingURL = "http://www.amazon.com"
                            + CommonPagingURL.replace("?ie=UTF8",
                                    "/ref=cm_cr_pr_btm_link_" + i).replace(
                                    "pageNumber=" + num, "pageNumber=" + i);
                    List<List<String>> commentlist = retriveReviews(pagingURL);
                    for (List<String> commentNode : commentlist) {
                        Comments.add(new ParsedComment.Builder(UUIDgenerator
                                .get(), Source.AMAZON)
                                .productName(productTitle)
                                .comment(commentNode.get(0))
                                .commentUrl(pagingURL)
                                .commentLabel(commentNode.get(1)).build());
                    }
                }
            }

        } catch (IOException e) {
            String msg = String.format("Unable to parse link %s", ReviewURL);
            logger.error(msg);
        }

        return Comments;
    }
}
