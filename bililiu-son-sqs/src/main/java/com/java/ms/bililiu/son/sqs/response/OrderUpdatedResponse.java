package com.java.ms.bililiu.son.sqs.response;

import java.util.List;

public class OrderUpdatedResponse {

	public Meta meta;
	public List<OrderStatus.Status> records;

	public OrderUpdatedResponse() {
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

	public List<OrderStatus.Status> getRecords() {
		return records;
	}

	public void setRecords(List<OrderStatus.Status> records) {
		this.records = records;
	}

}
