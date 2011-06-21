package org.grickle.jsondtotest.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 */
public class EchoParametersServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException
    {
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.setContentType("application/json-rpc");
        ServletOutputStream out = resp.getOutputStream();


        String method = req.getParameter("method");
        String id = req.getParameter("id");
        String params = req.getParameter("params");
        System.err.println("GET");
        System.err.println(" method=" + method);
        System.err.println(" id=" + id);
        System.err.println(" params=" + params);
        params = params.replace("\"", "\\\"");
        String output = "{"
            +"\"error\":null,"
            +"\"id\":" + id + ","
            +"\"result\":\"" + params + "\""
            +"}";
        System.err.println("RETURN data");
        System.err.println(" " + output);
        out.print(output);
    }
}
