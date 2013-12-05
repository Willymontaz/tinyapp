package com.xebia.tinyapp;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AutoFailer extends HttpServlet {
	
	private double FAIL_PROBA = 0.5;

	@Override
	public void init(ServletConfig config) throws ServletException {
		FAIL_PROBA = Double.parseDouble(config.getInitParameter("FAIL_PROBA"));
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if(Math.random() < FAIL_PROBA){
			resp.setStatus(500);
			resp.getWriter().println("500 Server Error");
		} else {
			resp.getWriter().println("200 Ok");
		}
		
		
		
	}
}
