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
	
	public NBox(String prodName) {
		productName = new SimpleStringProperty(prodName);
		productList = new ArrayList<Product>();
		quantity = new SimpleIntegerProperty(0);
		available = new SimpleIntegerProperty(0);
	}
	
	public NBox(Product p) {
		productName = new SimpleStringProperty(p.getProductName());
		productList = new ArrayList<Product>(); 
		productList.add(p);
		quantity = new SimpleIntegerProperty(1);
		available = new SimpleIntegerProperty(1);
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
