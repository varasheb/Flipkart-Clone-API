package com.flipkart.fms.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.flipkart.fms.service.AuthService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ScheduleJobs {
	private AuthService authService;
	
	@Scheduled(fixedDelay = 5000l*60)
	public void deleteUser() {
	     authService.permantDeleteUser();
        System.err.println("User DELETED");
	}
	@Scheduled(fixedDelay = 5000l*60)
	public void deleteToken() {
		authService.permentDeleteToken();
        System.err.println("Token DELETED");
	}
}
