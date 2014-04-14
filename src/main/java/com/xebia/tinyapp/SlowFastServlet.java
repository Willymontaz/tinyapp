package com.xebia.tinyapp;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SlowFastServlet extends HttpServlet {

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
		
		try {
			String speed = req.getParameter("speed");
			if ("slow".equals(speed)) {
				long slowSleep = (long) (MIN + Math.random() * (MAX - MIN));
				Thread.sleep(slowSleep);
			}
			long regularSleep = (long) (20 + Math.random() * 40);
			Thread.sleep(regularSleep);

			resp.getWriter().println(speed);

		} catch (InterruptedException e) {
			// Ignore
		}
	}

}
