package com.java.ms.bililiu.son.sqs.message.exception;

/**
 * Used to throw exception when can not
 * read metrics from actuator endpoint
 * @author Murillo
 */
public class MetricsRedirectException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MetricsRedirectException() {
		super();
	}

	public MetricsRedirectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MetricsRedirectException(String message, Throwable cause) {
		super(message, cause);
	}

	public MetricsRedirectException(String message) {
		super(message);
	}

	public MetricsRedirectException(Throwable cause) {
		super(cause);
	}

}