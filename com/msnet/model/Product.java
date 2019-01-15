package com.msnet.model;

import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Product {
	private StringProperty productionDate;
	private StringProperty expirationDate;
	private StringProperty productName;
	private StringProperty pid;
	
	public Product(Map product) {
		this.productionDate = new SimpleStringProperty(String.valueOf(product.get("production date")));
		this.expirationDate = new SimpleStringProperty(String.valueOf(product.get("expiration date")));
		this.productName = new SimpleStringProperty(String.valueOf(product.get("productName")));
		this.pid = new SimpleStringProperty(String.valueOf(product.get("PID")));
	} 
	
	public Product(String productionDate, String expirationDate, String productName, String pid) {
		this.productionDate = new SimpleStringProperty(productionDate);
		this.expirationDate = new SimpleStringProperty(expirationDate);
		this.productName = new SimpleStringProperty(productName);
		this.pid = new SimpleStringProperty(pid);
	}
	

	public String getProductionDate() {
		return productionDate.get();
	}
	
	public void setProductionDate(String productionDate) {
		this.productionDate.set(productionDate); 
	}
	
	public StringProperty productionDateProperty() {
		return productionDate;
	}
		
	
	public String getExpirationDate() {
		return expirationDate.get();
	}
	
	public void setExpirationDate(String expirationDate) {
		this.expirationDate.set(expirationDate);
	}
	
	public StringProperty expirationDateProperty() {
		return expirationDate;
	}
	
	public String getProductName() {
		return productName.get();
	}
	
	public void setProductName(String productName) {
		this.productName.set(productName);
	}
	
	public StringProperty productNameProperty() {
		return productName;
	}
	
	public String getPID() {
		return productName.get();
	}
	
	public void setPID(String pid) {
		this.pid.set(pid);
	}
	
	public StringProperty pidProperty() {
		return pid;
	}
}
