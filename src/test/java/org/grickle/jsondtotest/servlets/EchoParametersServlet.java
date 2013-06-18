package org.grickle.jsondtotest.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grickle.jsondtotest.util.Util;

/**
 *
 */
public class EchoParametersServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.setContentType("application/json-rpc");
        ServletOutputStream out = resp.getOutputStream();
        String id = req.getParameter("id");
        String params = req.getParameter("params");
        params = params.replace("\"", "\\\"");
        out.print("{"
                +"\"error\":null,"
                +"\"id\":" + id + ","
                +"\"result\":\"" + params + "\""
                +"}");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json-rpc");
        ServletOutputStream out = resp.getOutputStream();
        String id = req.getParameter("id");
        String params = Util.ReadEntireBuffer(req.getReader());
        params = params.replace("\"", "\\\"");
        out.print("{"
                +"\"error\":null,"
                +"\"id\":" + id + ","
                +"\"result\":\"" + params + "\""
                +"}");
    }
}
