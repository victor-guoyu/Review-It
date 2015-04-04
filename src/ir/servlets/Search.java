package ir.servlets;

import com.google.common.base.Charsets;
import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.Dispatcher;
import ir.handler.SearchTextHandler;
import ir.handler.SearchTweetHandler;
import ir.handler.SearchVideoHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Search extends JsonServlet implements AppServlet{
    /**
     * Search APIs implementation following JSON-RPC 2.0
     */
    private static final long serialVersionUID = 8841047725685113712L;
    private Logger logger;
    private Dispatcher searchDispatcher;

    @Override
    public void init() {
        logger = LogManager.getLogger(Search.class);
        searchDispatcher = new Dispatcher();
        searchDispatcher.register(new SearchTweetHandler());
        searchDispatcher.register(new SearchVideoHandler());
        searchDispatcher.register(new SearchTextHandler());
    }

    @Override
    protected  void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            JSONRPC2Request jsonRequest = parseJsonRequest(request);
            JSONRPC2Response jsonResponse = searchDispatcher.process(jsonRequest, null);
            byte[] replyBytes = jsonResponse.toString().getBytes(Charsets.UTF_8);
            sendResponse(response, replyBytes);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Unable to send JSON response to user");
        } catch (JSONRPC2ParseException e) {
            e.printStackTrace();
            logger.error("Unable to parse user JSON request");
        }
    }
}
