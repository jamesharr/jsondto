package org.grickle.jsondto.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * A couple reasons for this:
 *  - remote.php can require authentication
 *  - remote.php is always a POST (JSON nastiness).
 */
public class ProxyServlet extends HttpServlet
{
    private static final long serialVersionUID = 9L;

    private static String targetServer;
    private static String stripPrefix;

    @Override
    public void init() throws ServletException
    {
        targetServer = getInitParameter("proxyTo");
        stripPrefix = getInitParameter("stripPrefix");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
            {
        handleRequest(req, resp, false);
            }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
            {
        handleRequest(req, resp, true);
            }

    protected void handleRequest(HttpServletRequest req, HttpServletResponse resp, boolean isPost)
            throws ServletException, IOException
            {

        HttpClient httpclient = new DefaultHttpClient();

        StringBuffer sb = new StringBuffer();

        sb.append(targetServer);
        String uri = req.getRequestURI();
        if ( uri.startsWith(stripPrefix) )
            uri = uri.substring(stripPrefix.length());
        sb.append(uri);

        if (req.getQueryString() != null)
        {
            sb.append("?" + req.getQueryString());
        }

        HttpRequestBase targetRequest = null;

        if (isPost)
        {
            HttpPost post = new HttpPost(sb.toString());

            BufferedReader reader = req.getReader();
            // Our JSON objects are only one line. HAX!
            post.setEntity(new StringEntity(reader.readLine()));
            targetRequest = post;

        } else
        {
            HttpGet get = new HttpGet(sb.toString());
            targetRequest = get;
        }

        // This copies the headers. There are a few that will mess it up (encoding, etc).
        // So I only copy ones that are generally needed.
        targetRequest.setHeader("Authorization", req.getHeader("Authorization"));
        targetRequest.setHeader("Cookie", req.getHeader("Cookie"));

        HttpResponse targetResponse = httpclient.execute(targetRequest);
        HttpEntity entity = targetResponse.getEntity();

        // Send the Response
        InputStream input = entity.getContent();
        OutputStream output = resp.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
        String line = reader.readLine();

        // Set status line
        resp.setStatus(	targetResponse.getStatusLine().getStatusCode() );

        // Set headers
        for(Header h: targetResponse.getAllHeaders())
            resp.setHeader(h.getName(), h.getValue());

                while (line != null)
                {
                    writer.write(line + "\n");
                    line = reader.readLine();
                }

                reader.close();
                writer.close();
                httpclient.getConnectionManager().shutdown();
            }

}
