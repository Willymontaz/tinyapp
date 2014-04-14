package com.xebia.tinyapp.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TinyCache {

	Set<byte[]> cache = new HashSet<byte[]>();
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
		cache.add(b);
		sizeKo += b.length;
	}

    public synchronized void remove(byte[] b){
        cache.remove(b);
        sizeKo -= b.length;
    }

}
