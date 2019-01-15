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
@JsonPropertyOrder({ "id", "origin" })
public class Status {

	@JsonProperty("id")
	private int id;
	
	@JsonProperty("origin")
	private int idOrigin;
	
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("id")
	public int getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(int id) {
		this.id = id;
	}

	@JsonProperty("origin")
	public int getOrigin() {
		return idOrigin;
	}

	@JsonProperty("origin")
	public void setOrigin(int origin) {
		this.idOrigin = origin;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}