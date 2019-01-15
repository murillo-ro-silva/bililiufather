package com.java.ms.bililiu.son.sqs.message.exception;

public class InvalidHttpResponseException extends RuntimeException {
	
	private String body;
	
	private static final long serialVersionUID = 1L;

	public InvalidHttpResponseException() {
		
	}

	public InvalidHttpResponseException(String message) {
		super(message);
	}
	
	public InvalidHttpResponseException(String message, String body) {
		super(message);
		this.body = body;
	}

	public InvalidHttpResponseException(Throwable cause) {
		super(cause);
	}

	public InvalidHttpResponseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
}