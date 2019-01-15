package com.java.ms.bililiu.commons.http.bililiu.endpoint;

import java.util.Date;

import org.springframework.http.HttpMethod;

import com.google.gson.Gson;
import com.java.ms.bililiu.commons.enums.SaleType;

public class FallbackMessage {

	private String orderId;
	private SaleType saleType;
	private HttpMethod method;
	private Integer eventId;
	private Date createdAt;

	public FallbackMessage() {
		super();
	}

	public FallbackMessage(String orderId, SaleType saleType, HttpMethod method, Integer eventId) {
		super();
		this.orderId = orderId;
		this.saleType = saleType;
		this.method = method;
		this.eventId = eventId;
		this.createdAt = new Date();
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public SaleType getSaleType() {
		return saleType;
	}

	public void setSaleType(SaleType saleType) {
		this.saleType = saleType;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public Integer getEventId() {
		return eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String toJson() {
		return new Gson().toJson(this);
	}

}
