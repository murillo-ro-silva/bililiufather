package com.java.ms.bililiu.commons.enums;

public enum Status {

	ORDER_RECEIVED(1,"Order Received"),
	ORDER_APROVED(2,"Order Created"),
	ORDER_CANCELLED(3,"Order Cancelled"),
	INVOICE(7,"Invoice generated"),
	INTEGRATED(8, "Order Integrated"),
	RETURN(107,"Return"),
	QUICK_SALE(118,"Quick Sale"),
	AWAITING_INVOICE(136,"Awaiting Invoice"),
	UPDATE_LEGACY(143,"Update Legacy");

	private int id;
	private String description;
	
	Status(int id, String description) {
		this.id = id;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}