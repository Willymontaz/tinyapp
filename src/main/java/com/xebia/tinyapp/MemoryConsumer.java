package com.xebia.tinyapp;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xebia.tinyapp.mbean.CacheInfos;
import com.xebia.tinyapp.util.PiDigits;
import com.xebia.tinyapp.util.TinyCache;


public class MemoryConsumer extends HttpServlet{

	TinyCache cache = TinyCache.getInstance();
	byte[] oneMbArray = new byte[1024*1024];
	int mbPerRequest = 1;
	
	@Override
	public void init() throws ServletException {
		/*
		 * Register CacheInfos MBean
		 */
		try{
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
	        ObjectName name = new ObjectName("com.xebia.tinyapp:type=CacheInfosMBean"); 
	        CacheInfos cacheInfos = new CacheInfos(); 
	        mbs.registerMBean(cacheInfos, name); 
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		if((Runtime.getRuntime().freeMemory()/(1024*1024)) > 10){
			for (int i = 0; i < mbPerRequest; i++) {
				cache.put((byte[]) oneMbArray.clone());
			}
			
		}
		
		/*
		 * Use CPU, hopefully will get worse with GC
		 * Use random to prevent code optimization
		 */
		int nDigits = (int) (1000 + Math.random() * 500);
		BigDecimal pi = PiDigits.computePi(nDigits);
		
		resp.getWriter().println("Cache size is "+cache.sizeKo()+" Ko \n\n\n"+pi.toPlainString());
		
	}
	
}
