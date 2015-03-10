package ir.crawler.Buy;

import ir.server.Configuration;
import ir.server.ParsedDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class OverstockCrawler implements HTMLparser {
    private Logger logger;

    public void init() throws Exception {
        logger = LogManager.getLogger(OverstockCrawler.class);
        String keywords = "macbook";

        String inquiredURL = String.format(Configuration.getInstance()
                .getBuyURL(), keywords);
        List<String> ProductURLs = ProductURLGenerator(inquiredURL,
                "#result-products > li > div > a.reviewcountlink");

        logger.info(ProductURLs);

        List<ParsedDocument> parsedList = new ArrayList<ParsedDocument>();
        //generate all the list 
        for (String productURL : ProductURLs) {
            List<ParsedDocument> pl = parse("[id*=reviewText] > p",
                    "head > title", productURL);
            parsedList.addAll(pl);
        }

    }

    public List<String> ProductURLGenerator(String inquiredURL,
            String productSelector) throws IOException {
        Document doc = Jsoup.connect(inquiredURL).get();
        return GenerateProductURL(doc, productSelector);
    }

    @Override
    public String getProductTitle(Document doc, String TitleSelector) {
        String title = doc.select(TitleSelector).first().html();
        title = title.replace(" | Overstock.com", "");
        title = title.replace("Product Reviews: ", "");

        return title;
    }
}
