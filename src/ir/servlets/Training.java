package ir.servlets;

import com.google.common.base.Charsets;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.Dispatcher;
import ir.handler.TrainingHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Training extends JsonServlet implements AppServlet{
    /**
     * Training APIs implementation following JSON-RPC 2.0
     */
    private static final long serialVersionUID = 8878497194596555938L;
    private Logger logger;
    private Dispatcher trainingDispatcher;

    @Override
    public void init() {
        logger = LogManager.getLogger(Training.class);
        trainingDispatcher = new Dispatcher();
        trainingDispatcher.register(new TrainingHandler());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            JSONRPC2Request jsonRequest = parseJsonRequest(request);
            JSONRPC2Response jsonResponse = trainingDispatcher.process(jsonRequest, null);
            byte[] replyBytes = jsonResponse.toString().getBytes(Charsets.UTF_8);
            response.setHeader("Connection", "close");
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentLength(replyBytes.length);
            response.getOutputStream().write(replyBytes);
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Unable to parse the user request");
        }
    }

}
