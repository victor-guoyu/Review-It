package ir.handler;

import com.google.common.base.Strings;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.MessageContext;
import com.thetransactioncompany.jsonrpc2.server.RequestHandler;
import ir.crawler.youtube.Video;
import ir.index.SearchEngine;

import java.util.Map;

public class SearchVideoHandler implements RequestHandler{
    protected static final String VIDEO_SEARCH = "videoSearch";
    protected static final String SEARCH_TEXT = "text";

    @Override
    public String[] handledRequests() {
        return new String[]{VIDEO_SEARCH};
    }

    @Override
    public JSONRPC2Response process(JSONRPC2Request request, MessageContext messageContext) {
        if (request.getMethod().equalsIgnoreCase(VIDEO_SEARCH)) {
            return handleVideoSearch(request);
        } else {
            return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND, request.getID());
        }
    }

    /**
     * YouTube search handler
     *
     * @param request
     * @return Top rated YouTube video
     */
    private JSONRPC2Response handleVideoSearch(JSONRPC2Request request) {
        Map<String, Object> param = request.getNamedParams();
        if (param != null) {
            String text = String.valueOf(param.get(SEARCH_TEXT));
            if (!Strings.isNullOrEmpty(text)) {
                Video topVideo = SearchEngine.getSearchEngine().getTopRatedVideo(text);
                return new JSONRPC2Response(topVideo, request.getID());
            }
        }
        return new JSONRPC2Response(JSONRPC2Error.INVALID_PARAMS, request.getID());
    }
}
