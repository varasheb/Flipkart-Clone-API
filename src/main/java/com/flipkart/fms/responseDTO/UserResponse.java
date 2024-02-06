package com.flipkart.fms.responseDTO;

import com.flipkart.fms.enums.UserRole;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@Builder
public class UserResponse {
	private int userId;
	private String username;
	private String email;
	private UserRole userRole;
}
