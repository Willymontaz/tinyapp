package com.xebia.tinyapp;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TinyServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		try {
			long sleep = (long) (20 + Math.random() * 180);
			Thread.sleep(sleep);
			resp.getWriter().println("Took "+sleep+" ms");
		} catch (InterruptedException e) {
			// Ignore
		}
	}

}
