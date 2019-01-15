package com.java.ms.bililiu.son.sqs.model;

import com.google.gson.Gson;

public class MessageSqs {

	private String verb;
	
	private String createdAt;
	
	private OrderSqs order;
	
	public OrderSqs getOrder() {
		return order;
	}
	public void setOrder(OrderSqs order) {
		this.order = order;
	}
	public String getVerb() {
		return verb;
	}
	public void setVerb(String verb) {
		this.verb = verb;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String toJson() {
		return new Gson().toJson(this);
	}
	
}
