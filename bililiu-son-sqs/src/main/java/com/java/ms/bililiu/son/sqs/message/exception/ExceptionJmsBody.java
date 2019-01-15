package com.java.ms.bililiu.son.sqs.message.exception;

import com.google.gson.Gson;

/**
 * @author Murillo
 * Note: Body of payload of exception errors and transformation for json
 */
public class ExceptionJmsBody {

	public ExceptionJms body;
	
	public ExceptionJmsBody(ExceptionJms body) {
		super();
		this.body = body;		
	}

	public ExceptionJms getStatus() {
		return body;
	}

	public void setStatus(ExceptionJms body) {
		this.body = body;
	}
	
	public String marshall() {
		return new Gson().toJson(this);
	}

	@Override
	public String toString() {
		return "ExceptionJmsBody [status=" + body + "]";
	}

}