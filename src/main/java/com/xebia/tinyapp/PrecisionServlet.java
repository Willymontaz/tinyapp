package com.xebia.tinyapp;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PrecisionServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
			int precision = (int)(5000 + Math.random() * 15000);
			resp.getWriter().println("<precision>\n\t<digits>"+(precision)+"</digits>\n</precision>");
			
	}

}
