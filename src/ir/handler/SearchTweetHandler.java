package ir.handler;

import com.google.common.base.Strings;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.MessageContext;
import com.thetransactioncompany.jsonrpc2.server.RequestHandler;
import ir.crawler.twitter.Tweet;
import ir.index.SearchEngine;

import java.util.List;
import java.util.Map;

public class SearchTweetHandler implements RequestHandler {

    private static final String TWEET_SEARCH = "tweetSearch";
    private static final String SEARCH_TEXT = "text";

    @Override
    public String[] handledRequests() {
        return new String[]{TWEET_SEARCH};
    }

    @Override
    public JSONRPC2Response process(JSONRPC2Request request, MessageContext messageContext) {
        if (request.getMethod().equalsIgnoreCase(TWEET_SEARCH)) {
            return handleTweetSearch(request);
        } else {
            return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND, request.getID());
        }
    }

    /**
     * Twitter Search handler
     *
     * @param request
     * @return List of Tweet in JSON
     */
    private JSONRPC2Response handleTweetSearch(JSONRPC2Request request) {
        Map<String, Object> param = request.getNamedParams();
        if (param != null) {
            String text = String.valueOf(param.get(SEARCH_TEXT));
            if (!Strings.isNullOrEmpty(text)) {
                List<Tweet> tweets = SearchEngine.getSearchEngine().getTopTweets(text);
                return new JSONRPC2Response(tweets, request.getID());
            }

        }
        return new JSONRPC2Response(JSONRPC2Error.INVALID_PARAMS, request.getID());
    }
}
