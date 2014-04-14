package com.xebia.tinyapp;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PeopleServlet extends HttpServlet {

	private int MIN;
	private int MAX;

	@Override
	public void init(ServletConfig config) throws ServletException {
		MIN = Integer.parseInt(config.getInitParameter("MIN"));
		MAX = Integer.parseInt(config.getInitParameter("MAX"));
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		if(req.getParameter("forename") == null || req.getParameter("lastname") == null) {
            resp.setStatus(500);
            return;
        }

        String forename = req.getParameter("forename");
        String lastname = req.getParameter("lastname");
		
    	resp.getWriter().println("<person>\n\t<forename>"+forename+"</forename>\n\t<lastname>"+lastname+"</lastname>\n</person>\n");

	}

}
