package com.java.ms.bililiu.son.sqs.response;

public class Meta {

	public String server;
	public Integer limit;
	public Integer offset;
	public Integer recordCount;

	public Meta() {
		super();
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(Integer recordCount) {
		this.recordCount = recordCount;
	}

	@Override
	public String toString() {
		return "Meta [server=" + server + ", limit=" + limit + ", offset=" + offset + ", recordCount=" + recordCount
				+ "]";
	}

}