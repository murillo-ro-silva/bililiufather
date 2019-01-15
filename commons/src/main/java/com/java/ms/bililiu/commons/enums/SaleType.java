package com.java.ms.bililiu.commons.enums;

public enum SaleType {

	CAPTURED("captured"), PROCESSED("processed");

	private String type;

	SaleType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
