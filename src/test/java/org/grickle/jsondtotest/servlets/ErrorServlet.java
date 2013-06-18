package org.grickle.jsondtotest.servlets;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grickle.jsondtotest.util.Util;

/**
 * Servlet that just returns an error
 */
public class ErrorServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.setContentType("application/json-rpc");
        ServletOutputStream out = resp.getOutputStream();
        String id = req.getParameter("id");
        String params = req.getParameter("params");
        Matcher m = Pattern.compile("\\[\"(.*)\"\\]").matcher(params);
        if ( m.matches() )
            out.print("{\"id\":" + id + ",\"error\":\""+m.group(1)+"\"}");
        else
            out.print("FAIL THIS TEST");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {


        resp.setContentType("application/json-rpc");
        ServletOutputStream out = resp.getOutputStream();
        String id = req.getParameter("id");
        String params = Util.ReadEntireBuffer(req.getReader());
        Matcher m = Pattern.compile("\\[\"(.*)\"\\]").matcher(params);
        if ( m.matches() )
            out.print("{\"id\":" + id + ",\"error\":\""+m.group(1)+"\"}");
        else
            out.print("FAIL THIS TEST");
    }
}
