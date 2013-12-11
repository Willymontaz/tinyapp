package com.xebia.tinyapp;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SlowFast extends HttpServlet {

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
			long slowSleep = 0;
			String speed = req.getParameter("speed");
			if ("slow".equals(speed)) {
				slowSleep = (long) (MIN + Math.random() * (MAX - MIN));
				Thread.sleep(slowSleep);
			}
			long regularSpleep = (long) (20 + Math.random() * 40);
			Thread.sleep(regularSpleep);

			resp.getWriter().println("<number>\n\t<type>PI</type>\n\t<digits>"+(regularSpleep+slowSleep)+"</digits>\n</number>");
			
			
		} catch (InterruptedException e) {
			// Ignore
		}
	}

}
