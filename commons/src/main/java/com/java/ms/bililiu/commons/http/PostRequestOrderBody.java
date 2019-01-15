package com.java.ms.bililiu.commons.http;

import com.google.gson.Gson;

/**
 * @author Murillo Note: body of payload in the request api post Apolo-Gateway
 *         and transform to json.
 */
public class PostRequestOrderBody {

	private PostRequestOrder order;
	private String createdAt;

	public PostRequestOrderBody(PostRequestOrder order, String createdAt) {
		super();
		this.order = order;
		this.createdAt = createdAt;
	}

	public PostRequestOrder getOrder() {
		return order;
	}

	public void setOrder(PostRequestOrder order) {
		this.order = order;
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