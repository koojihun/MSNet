package com.msnet.model;

import java.util.ArrayList;
import java.util.List;

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

	public NDBox(String productName, String productionDate, String expirationDate, ArrayList<Product> productList, int quantity, int available) {
		this.productName = new SimpleStringProperty(productName);
		this.productionDate = new SimpleStringProperty(productionDate);
		this.expirationDate = new SimpleStringProperty(expirationDate);
		this.productList = productList;
		this.quantity = new SimpleIntegerProperty(quantity);
		this.available = new SimpleIntegerProperty(available);
	}
	
	public List<String> getPid(){
		List<String> pidList = new ArrayList<String>();
		for(Product product : productList) {
			pidList.add(product.getPID()); 
		}
		return pidList;
	}
	
	public void addProduct(Product input) {
		productList.add(input);
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
