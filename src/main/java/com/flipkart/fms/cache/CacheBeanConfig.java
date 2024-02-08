package com.flipkart.fms.cache;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.flipkart.fms.entity.User;

@Configuration
public class CacheBeanConfig {
	@Bean
	public CacheStore<User> userCacheStore(){
		return new CacheStore<User>(Duration.ofMinutes(5));
	}
	@Bean
	public CacheStore<String> otpCacheStore(){
		return new CacheStore<String>(Duration.ofSeconds(130));
	}
}
