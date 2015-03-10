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

public class BuyCrawler implements HTMLparser {
    private Logger logger;

    public void init() throws Exception {
        logger = LogManager.getLogger(BuyCrawler.class);
        String[] args = new String[] { "queryType", "home", "qu", "macbook" };

        String inquiredURL = Configuration.getInstance().getBuyURL();
        List<String> ProductURLs = ProductURLGenerator(
                inquiredURL,
                args,
                "#ProductsContainer>.gridItemContainer:has([id*=ProductGridItemControl_reviewerRating])>h3>a");
        logger.info(ProductURLs);
        
        List<ParsedDocument> parsedList = new ArrayList<ParsedDocument>();

        for (String productURL : ProductURLs) {
            List<ParsedDocument> pl = parse(
                    "[id*=ctlCustomerReviews_customerReviews_reviewContent_]",
                    "#AuthorArtistTitle_productTitle", productURL);
            parsedList.addAll(pl);
        }

    }

    public List<String> ProductURLGenerator(String inquiredURL,
            String[] dataargs, String productSelector) throws IOException {
        Response res = Jsoup.connect(inquiredURL).data(dataargs)
                .method(Method.POST).followRedirects(true).execute();
        
        Map<String, String> cookies = res.cookies();

        Document doc = Jsoup.connect(inquiredURL).cookies(cookies).get();

        return GenerateProductURL(doc, productSelector);
    }
}
