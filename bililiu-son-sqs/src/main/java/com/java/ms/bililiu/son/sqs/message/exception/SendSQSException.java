package com.java.ms.bililiu.son.sqs.message.exception;

public class SendSQSException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public SendSQSException (String message) {
		super(message);
	}
}