package ir.servlets;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.common.base.Charsets;
import com.google.common.collect.Range;
import com.google.common.primitives.Ints;
import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;


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
     * Send server reply back to client
     * @param response
     * @param replyBytes
     * @throws IOException
     */
    protected void sendResponse(HttpServletResponse response, byte[] replyBytes) throws IOException {
        response.setHeader("Connection", "close");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentLength(replyBytes.length);
        response.getOutputStream().write(replyBytes);
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }
}
