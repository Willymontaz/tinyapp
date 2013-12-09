package com.xebia.tinyapp;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MemoryConsumer extends HttpServlet{

	ConcurrentMap<Integer, byte[]> cache = new ConcurrentHashMap<Integer, byte[]>();
	byte[] oneMbArray = new byte[1024*1024];
	int mbPerRequest = 1;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		if((Runtime.getRuntime().freeMemory()/(1024*1024)) > 10){
			for (int i = 0; i < mbPerRequest; i++) {
				cache.put(cache.size(), (byte[]) oneMbArray.clone());
			}
			
		}
		
		/*
		 * Use CPU, hopefully will get worse with GC
		 */
		PiDigits.computePi(5000);
		
		resp.getWriter().println("Cache size is "+cache.size()+" MB ");
		
	}
	
}
