package com.flipkart.fms.cache;


import java.time.Duration;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CacheStore<T>{
	private Cache<String,T> cache;

	public CacheStore(Duration expire) {
		super();
		this.cache =CacheBuilder.newBuilder()
				    .expireAfterAccess(expire)
				    .concurrencyLevel(Runtime.getRuntime().availableProcessors())
				    .build();
	}
	public void add(String key,T OTP) {
		cache.put(key, OTP);
	}
	public T get(String key) {
		return cache.getIfPresent(key);
	}
	public void remove(String key) {
		cache.invalidate(key);
	}

}
