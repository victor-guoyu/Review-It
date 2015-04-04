package ir.handler;

import com.google.common.base.Strings;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.MessageContext;
import com.thetransactioncompany.jsonrpc2.server.RequestHandler;
import ir.index.ParsedComment;
import ir.index.SearchEngine;

import java.util.List;
import java.util.Map;

import static ir.handler.HandlerConstant.*;

public class SearchTextHandler implements RequestHandler{

    @Override
    public String[] handledRequests() {
        return new String[]{COMMENT_SEARCH};
    }

    @Override
    public JSONRPC2Response process(JSONRPC2Request request, MessageContext messageContext) {
        if (request.getMethod().equalsIgnoreCase(COMMENT_SEARCH)) {
            return handleCommentSearch(request);
        } else {
            return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND, request.getID());
        }
    }

    private JSONRPC2Response handleCommentSearch(JSONRPC2Request request) {
        Map<String, Object> param = request.getNamedParams();
        if (param != null) {
            String text = String.valueOf(param.get(SEARCH_TEXT));
            if (!Strings.isNullOrEmpty(text)) {
                List<ParsedComment> comments = SearchEngine.getSearchEngine().searchComments(text);
                return new JSONRPC2Response(comments, request.getID());
            }
        }
        return new JSONRPC2Response(JSONRPC2Error.INVALID_PARAMS, request.getID());
    }
}
