package com.msnet.model;

import java.util.ArrayList;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class NDBox {
	private StringProperty productName;
	private StringProperty productionDate;
	private StringProperty expirationDate;
	private ArrayList<Product> productList;
	private IntegerProperty quantity;
	private IntegerProperty available;

	public NDBox(Product p) {
		this.productName = new SimpleStringProperty(p.getProductName());
		this.productionDate = new SimpleStringProperty(p.getProductionDate());
		this.expirationDate = new SimpleStringProperty(p.getExpirationDate());
		this.productList = new ArrayList<Product>();
		productList.add(p);
		this.quantity = new SimpleIntegerProperty(1);
		this.available = new SimpleIntegerProperty(1);
	}
	
	public NDBox(String prodName, String prodDate, String expDate) {
		this.productName = new SimpleStringProperty(prodName);
		this.productionDate = new SimpleStringProperty(prodDate);
		this.expirationDate = new SimpleStringProperty(expDate);
		this.productList = new ArrayList<Product>();
		this.quantity = new SimpleIntegerProperty(0);
		this.available = new SimpleIntegerProperty(0);
	}
	
	public void addProduct(Product p) {
		productList.add(p);
		quantity.set(quantity.get() + 1);
		available.set(available.get() + 1);
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
