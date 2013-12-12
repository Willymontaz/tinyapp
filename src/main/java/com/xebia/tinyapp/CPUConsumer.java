package com.xebia.tinyapp;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xebia.tinyapp.util.PiDigits;

public class CPUConsumer extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String stringDigits = req.getParameter("digits");
		int nDigits = 10000;
		if(stringDigits != null){
			nDigits = Integer.parseInt(stringDigits);
		}
		BigDecimal pi = PiDigits.computePi(nDigits);
		resp.getWriter().println(pi.toPlainString());
		
	}
}
