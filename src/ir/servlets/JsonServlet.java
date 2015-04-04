package ir.servlets;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.common.base.Charsets;
import com.google.common.collect.Range;
import com.google.common.primitives.Ints;
import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import net.minidev.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class JsonServlet extends HttpServlet {

    private static final long serialVersionUID = 1285424896184570261L;

    private boolean requestValidator(String contentType, Integer contentLength) {
        //Maximum length that able to handle
        Range<Integer> lengthRange = Range.closedOpen(0, 10000);
        if (contentLength == null) {
            contentLength = 0;
        }
        return contentType.equals("application/json") && lengthRange.contains(contentLength);
    }

    /**
     * Parse HttpServletRequest to JSON-RPC request
     * Assumed the request content is encoded with UTF-8
     * @param request HttpServletRequest
     * @return JSONRPC2Request
     * @throws IOException
     */
    protected JSONRPC2Request parseJsonRequest(HttpServletRequest request) throws IOException, JSONRPC2ParseException {
        String contentType = request.getHeader("Content-Type");
        Integer contentLength = Ints.tryParse(request.getHeader("Content-Length"));
        Preconditions.checkArgument(requestValidator(contentType, contentLength), "Invalid JSON request");
        BufferedReader reader = request.getReader();
        StringBuffer message = new StringBuffer();
        char[] buffer = new char[contentLength];
        int numRead = 0;
        while (numRead < contentLength) {
            int read = reader.read(buffer, 0, buffer.length);
            String s = new String(buffer, 0, read);
            numRead += s.getBytes(Charsets.UTF_8).length;
            message.append(s);
        }
        return JSONRPC2Request.parse(message.toString(), true, true, true);
    }

    /**
     * Render JSON response
     * @param requestId RPC ID
     * @param message Server message
     * @param result int indicate whether or not the request is successful
     * @param returnParams list of return results
     * @return Server response in JSON
     */
    protected JSONObject renderResponse(String requestId, String message, int result, HashMap<String, String> returnParams) {
        JSONObject reply = new JSONObject();
        reply.put("jsonrpc", "2.0");
        reply.put("result", result);
        reply.put("message", message);
        reply.put("id", requestId);
        if (result == 1 && returnParams != null && returnParams.size()>0) {
            JSONObject params = new JSONObject();
            for (Map.Entry<String, String> entry : returnParams.entrySet()) {
                params.put(entry.getKey(), entry.getValue());
            }
            reply.put("params", params);
        }
        return reply;
    }
}
