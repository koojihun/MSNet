package com.msnet.model;

import org.json.simple.JSONObject;

public class Product {
	private String productionDate;
	private String expirationDate;
	private String productName;
	private String pid;
	private String wid;
	
	public Product(JSONObject obj) {
		this.productionDate = String.valueOf(obj.get("production date"));
		this.expirationDate = String.valueOf(obj.get("expiration date"));
		this.productName = String.valueOf(obj.get("prodName"));
		this.pid = String.valueOf(obj.get("PID"));
	}
	
	public Product(String productionDate, String expirationDate, String productName, String pid) {
		this.productionDate = productionDate;
		this.expirationDate = expirationDate;
		this.productName = productName;
		this.pid = pid;
	}
	
	public Product(String productionDate, String expirationDate, String productName, String pid, String wid) {
		this.productionDate = productionDate;
		this.expirationDate = expirationDate;
		this.productName = productName;
		this.pid = pid;
		this.wid = wid;
	}

	public String getProductionDate() {
		return productionDate;
	}

	public void setProductionDate(String productionDate) {
		this.productionDate = productionDate;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}
	
	public void setWid(String wid) {
		this.wid = wid;
	}
	
	public String getWid() {
		return wid;
	}

}