package com.flipkart.fms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.flipkart.fms")
public class FlipkartCloneApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlipkartCloneApiApplication.class, args);
	}

}
