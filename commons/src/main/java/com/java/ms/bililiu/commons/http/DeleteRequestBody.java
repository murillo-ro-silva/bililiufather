package com.java.ms.bililiu.commons.http;

import com.google.gson.Gson;

public class DeleteRequestBody {

	private String createdAt;

	public DeleteRequestBody(String createdAt) {
		super();
		this.createdAt = createdAt;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	
	public String marshall() {
		return new Gson().toJson(this);
	}

}
