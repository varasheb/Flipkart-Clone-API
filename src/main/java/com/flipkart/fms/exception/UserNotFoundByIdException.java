package com.flipkart.fms.exception;

public class UserNotFoundByIdException extends RuntimeException {
	private String message;
	public UserNotFoundByIdException(String message) {
		super();
		this.message = message;
	}
	@Override
	public String getMessage() {
		return message;
	}
}
