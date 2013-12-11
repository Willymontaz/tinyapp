package com.xebia.tinyapp.mbean;

import com.xebia.tinyapp.util.TinyCache;

public class CacheInfos implements CacheInfosMBean {
	TinyCache cache = TinyCache.getInstance();
	
	@Override
	public String getName() {
		return "CacheInfos";
	}

	@Override
	public int getCacheSizeKo() {
		return cache.sizeKo();
	}
	
}
