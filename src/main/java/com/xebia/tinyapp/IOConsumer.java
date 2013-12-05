package com.xebia.tinyapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IOConsumer extends HttpServlet {

	
	AtomicInteger fileId = new AtomicInteger(0);
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		int kbytes = Integer.parseInt(req.getParameter("ko"));
		
		File file = new File("/tmp/tmp_"+fileId.getAndIncrement());
		FileOutputStream fos = new FileOutputStream(file);
		for (int i = 0; i < kbytes; i++) {
			fos.write(new byte[1024]);
			fos.flush();
		}
		fos.close();
		file.delete();
		
		resp.getWriter().println("Wrote "+kbytes+" ko");
	}
}
