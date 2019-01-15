package com.java.ms.bililiu.son.sqs.model;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "subOrder", "salesChannel" })
public class Order {

	@JsonProperty("id")
	private String id;

	@JsonProperty("subOrder")
	private SubOrder subOrder;

	@JsonProperty("salesChannel")
	private SalesChannel salesChannel;

	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("subOrder")
	public SubOrder getSubOrder() {
		return subOrder;
	}

	@JsonProperty("subOrder")
	public void setSubOrder(SubOrder subOrder) {
		this.subOrder = subOrder;
	}

	@JsonProperty("salesChannel")
	public SalesChannel getSalesChannel() {
		return salesChannel;
	}

	@JsonProperty("salesChannel")
	public void setSalesChannel(SalesChannel salesChannel) {
		this.salesChannel = salesChannel;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", subOrder=" + subOrder + ", salesChannel=" + salesChannel
				+ ", additionalProperties=" + additionalProperties + "]";
	}
	
}