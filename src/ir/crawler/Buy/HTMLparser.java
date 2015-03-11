package ir.crawler.Buy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ir.index.ParsedDocument;

interface HTMLparser {
    default List<ParsedDocument> parse(String CommentSelector,
            String TitleSelector, String weburl) throws IOException {
        Document doc = Jsoup.connect(weburl).get();
        Elements CommentElement = doc.select(CommentSelector);

        List<ParsedDocument> parsedList = new ArrayList<ParsedDocument>();

        String ProductTitle = getProductTitle(doc, TitleSelector);
        
        for (Element comment : CommentElement) {
            parsedList.add(new ParsedDocument.Builder("ID")
                    .productName(ProductTitle)
                    .comment(comment.html()).commentUrl(weburl).build());
            System.out.println("Title-" + ProductTitle + ": " + comment.html());
        }

        return parsedList;
    }

    default String inquiryURL(String unformattedURL, String[] args) {
        return String.format(unformattedURL, args);
    }

    default List<String> GenerateProductURL(Document doc, String productSelector) {
        Elements productList = doc.select(productSelector);

        List<String> ProductURLs = new ArrayList<String>();

        for (Element product : productList) {
            ProductURLs.add(product.attr("href"));
        }
        return ProductURLs;
    }

    String getProductTitle(Document doc, String TitleSelector);
}
