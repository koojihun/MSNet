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
	
	public NBox(String productName, ArrayList<Product> productList, int quantity) {
		this.productName = new SimpleStringProperty(productName);
		this.productList =productList;
		this.quantity = new SimpleIntegerProperty(quantity);
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
	
}
