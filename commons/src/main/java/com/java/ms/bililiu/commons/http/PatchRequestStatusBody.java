package com.java.ms.bililiu.commons.http;

import com.google.gson.Gson;

/**
 * @author Murillo Note: body of payload in the request api patch Apolo-Gateway
 *         and transform to json.
 */
public class PatchRequestStatusBody {

	private PatchRequestStatus status;
	private String createdAt;

	public PatchRequestStatusBody(PatchRequestStatus status, String createdAt) {
		super();
		this.status = status;
		this.createdAt = createdAt;
	}

	public PatchRequestStatus getStatus() {
		return status;
	}

	public void setStatus(PatchRequestStatus status) {
		this.status = status;
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

	@Override
	public String toString() {
		return "PatchRequestStatusBody [status=" + status + "]";
	}

}

