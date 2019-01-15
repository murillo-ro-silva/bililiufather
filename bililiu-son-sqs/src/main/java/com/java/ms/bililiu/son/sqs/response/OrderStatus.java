package com.java.ms.bililiu.son.sqs.response;

public class OrderStatus {

	public Order order;
	public Status status;

	public OrderStatus() {
		super();
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "OrderStatus [order=" + order + ", status=" + status + "]";
	}

	static class Order {

		public Integer id;

		public Order() {
			super();
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		@Override
		public String toString() {
			return "Order [id=" + id + "]";
		}

	}
	
	static class Status {

		public Integer id;
		public String description;

		public Status() {
			super();
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		@Override
		public String toString() {
			return "Status [id=" + id + ", description=" + description + "]";
		}

	}


}