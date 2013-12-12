package com.xebia.tinyapp.util;

import java.util.HashMap;
import java.util.Map;

public class TinyCache {

	Map<Integer, byte[]> cache = new HashMap<Integer, byte[]>();
	private int sizeKo;
	
	private static class TinyCacheHolder {
		private static final TinyCache instance = new TinyCache();
	}
	
	private TinyCache(){}
	
	public static TinyCache getInstance(){
		return TinyCacheHolder.instance;
	}
	
	public int sizeKo() {
		return sizeKo;
	}
	
	public synchronized void put(byte[] b){
		cache.put(sizeKo, b);
		sizeKo += b.length;
	}

}
