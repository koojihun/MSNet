package com.msnet.model;

import java.util.ArrayList;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class NBox {
	private StringProperty productName;
	private ArrayList<Product> productList;
	private IntegerProperty quantity;
	private IntegerProperty available;
	
	public NBox(String productName, ArrayList<Product> productList, int quantity, int available) {
		this.productName = new SimpleStringProperty(productName);
		this.productList =productList;
		this.quantity = new SimpleIntegerProperty(quantity);
		this.available = new SimpleIntegerProperty(available);
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
	
	public ArrayList<Product> getProductList(){
		return productList;
	}
	
	public void setProductList(ArrayList<Product> productList) {
		this.productList = productList;
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
