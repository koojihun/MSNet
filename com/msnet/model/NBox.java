package com.msnet.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class NBox {
	private StringProperty productName;
	private IntegerProperty quantity;
	private IntegerProperty available;
	
	public NBox(String prodName, int amount) {
		productName = new SimpleStringProperty(prodName);
		quantity = new SimpleIntegerProperty(amount);
		available = new SimpleIntegerProperty(amount);
	}
	
	public void increaseQuantityAndAvailable(int plus) {
		quantity.setValue(quantity.getValue() + plus);
		available.setValue(available.getValue() + plus);
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
