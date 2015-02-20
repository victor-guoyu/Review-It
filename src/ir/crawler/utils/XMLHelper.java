package ir.crawler.utils;

import org.jsoup.select.Elements;

public class XMLHelper {
    public static String getFirstNodeText(Elements elements) {
        return elements.get(0).text();
    }
}
