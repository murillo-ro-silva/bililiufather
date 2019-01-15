package com.java.ms.bililiu.son.sqs.message.exception;

import com.google.gson.Gson;
import com.java.ms.bililiu.commons.enums.SaleType;

/**
 * @author Murillo
 * Note: Exception and transform error payload contents for json
 */
public class ExceptionJms {

	private String status;

	private String application;

	private SaleType type;

	private String message;
	
	private String order;

	public ExceptionJms(String status, String application, SaleType type, String message, String order) {
		super();
		this.status = status;
		this.application = application;
		this.type = type;
		this.message = message;
		this.order = order;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public SaleType getType() {
		return type;
	}

	public void setType(SaleType type) {
		this.type = type;
	}

	public String getMessagem() {
		return message;
	}

	public void setMessagem(String message) {
		this.message = message;
	}
	
	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String marshall() {
		return new Gson().toJson(this);

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((application == null) ? 0 : application.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((order == null) ? 0 : order.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExceptionJms other = (ExceptionJms) obj;
		if (application == null) {
			if (other.application != null)
				return false;
		} else if (!application.equals(other.application))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (order == null) {
			if (other.order != null)
				return false;
		} else if (!order.equals(other.order))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExceptionJms [status=" + status + ", application=" + application + ", type=" + type + ", message="
				+ message + ", order=" + order + "]";
	}

}