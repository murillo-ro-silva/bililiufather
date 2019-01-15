package com.java.ms.bililiu.commons.enums;

public enum Applications {
	
	APP_P52(1,"P52App"),
	APP_BUZZORDER(15,"buzzorder-workerApp"),
	APP_TDC(16,"TDCCancellationEventsApp"),
	APP_INTEGRA_COMMERCE(32, "IntegracommerceApp"),
	APP_NINO(8, "MaestroWorkerNinoApp"),
	APP_SLASH(28, "MaestroWorkerSlashApp"),
	APP_SULLIVAN(9, "MaestroWorkerSullivanApp");
	
	private int id;
	private String descriptionApp;
	
	Applications(int id, String description) {
		this.id = id;
		this.descriptionApp = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return descriptionApp;
	}

	public void setDescription(String description) {
		this.descriptionApp = description;
	}
}