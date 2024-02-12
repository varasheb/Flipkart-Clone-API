package com.flipkart.fms.exception;

@SuppressWarnings("serial")
public class UserNotLoggedInException extends RuntimeException {
	private String message;
	
	public UserNotLoggedInException(String message) {
		super();
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
