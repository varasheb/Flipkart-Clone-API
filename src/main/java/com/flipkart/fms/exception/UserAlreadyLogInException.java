package com.flipkart.fms.exception;

public class UserAlreadyLogInException extends RuntimeException {
	private String message;
	
	public UserAlreadyLogInException(String message) {
		super();
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
