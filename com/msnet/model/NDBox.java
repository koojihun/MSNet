package com.msnet.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class NDBox {
	private StringProperty productName;
	private StringProperty productionDate;
	private StringProperty expirationDate;
	private IntegerProperty quantity;
	private IntegerProperty available;
	
	
	public NDBox(String prodName, String prodDate, String expDate, int amount) {
		this.productName = new SimpleStringProperty(prodName);
		this.productionDate = new SimpleStringProperty(prodDate);
		this.expirationDate = new SimpleStringProperty(expDate);
		this.quantity = new SimpleIntegerProperty(amount);
		this.available = new SimpleIntegerProperty(amount);
	}
	
	public String getProductName() {
		return productName.get();
	}
	
	public void setProductName(String name) {
		this.productName.set(name); 
	}
	
	public StringProperty productNameProperty() {
		return productName;
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
	
	public void setExpirationDate(String productionDate) {
		this.expirationDate.set(productionDate); 
	}
	
	public StringProperty expirationDateProperty() {
		return expirationDate;
	}
	
	public int getQuantity() {
		 return quantity.get();
	}
	
	public void setQuantity(int quantity) {
		this.quantity.set(quantity);
	}
	
	public IntegerProperty quantityProperty() {
		return quantity;
	}
	
	public int getAvailable() {
		 return available.get();
	}
	
	public void setAvailable(int available) {
		this.available.set(available);
	}
	
	public IntegerProperty availableProperty() {
		return available;
	}
}
