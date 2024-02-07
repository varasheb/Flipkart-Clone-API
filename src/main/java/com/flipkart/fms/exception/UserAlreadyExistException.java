package com.flipkart.fms.exception;

public class UserAlreadyExistException extends RuntimeException{
	private String message;
	public UserAlreadyExistException(String message) {
		super();
		this.message = message;
	}
	@Override
	public String getMessage() {
		return message;
	}
}
