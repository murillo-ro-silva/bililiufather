package com.java.ms.bililiu.son.sqs.response;

import java.util.List;

public class OrderCreatedResponse {

	public Meta meta;
	public List<OrderStatus> records;

	public OrderCreatedResponse() {
		super();
	}

	@Override
	public String toString() {
		return "Response [meta=" + meta + "]";
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public List<OrderStatus> getRecords() {
		return records;
	}

	public void setRecords(List<OrderStatus> records) {
		this.records = records;
	}

}
