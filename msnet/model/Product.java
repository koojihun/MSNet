package com.msnet.model;

import java.util.Map;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Product {
	private StringProperty productId;
	private StringProperty productionDate;
	private StringProperty expirationDate;
	private StringProperty productName;
	private IntegerProperty quantity;
	
	public Product(Map product) {
		this.productionDate = new SimpleStringProperty(String.valueOf(product.get("production date")));
		this.expirationDate = new SimpleStringProperty(String.valueOf(product.get("expiration date")));
		this.productName = new SimpleStringProperty(String.valueOf(product.get("productName")));
		this.quantity = new SimpleIntegerProperty(Integer.valueOf((String) product.get("quantity")));
	}
	
	public Product(String productionDate, String expirationDate, String productName, int quantity) {
		this.productionDate = new SimpleStringProperty(productionDate);
		this.expirationDate = new SimpleStringProperty(expirationDate);
		this.productName = new SimpleStringProperty(productName);
		this.quantity = new SimpleIntegerProperty(quantity);
	}
	
	public String getProductId() {
		return productId.get();
	}
	
	public void setProductId(String productId) {
		this.productId.set(productId);
	}
	
	public StringProperty productIdProperty() {
		return productId;
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
	
	public int getQuantity() {
		return quantity.get();
	}
	
	public void setQuantity(int quantity) {
		this.quantity.set(quantity);
	}	
	
	public IntegerProperty quantityProperty() {
		return quantity;
	}
}
