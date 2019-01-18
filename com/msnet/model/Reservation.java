package com.msnet.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Reservation {
	private StringProperty time;
	private StringProperty toAddress;
	private StringProperty toCompany;
	private StringProperty productName;
	private StringProperty productionDate;
	private StringProperty expirationDate;
	private IntegerProperty quantity;
	private IntegerProperty success;
	
	
	public Reservation(String time, String toAddress, String toCompany, String productName, String productionDate, String expirationDate, int quantity, int success) {
		this.time = new SimpleStringProperty(time);
		this.toAddress = new SimpleStringProperty(toAddress);
		this.toCompany = new SimpleStringProperty(toCompany);
		this.productName = new SimpleStringProperty(productName);
		this.productionDate = new SimpleStringProperty(productionDate);
		this.expirationDate = new SimpleStringProperty(expirationDate);		
		this.quantity = new SimpleIntegerProperty(quantity);
		this.success = new SimpleIntegerProperty(success);
	}
	
	public Reservation(String time, String toAddress, String toCompany, String productName, int quantity, int success) {
		this.time = new SimpleStringProperty(time);
		this.toAddress = new SimpleStringProperty(toAddress);
		this.toCompany = new SimpleStringProperty(toCompany);
		this.productName = new SimpleStringProperty(productName);	
		this.quantity = new SimpleIntegerProperty(quantity);
		this.success = new SimpleIntegerProperty(success);
	}
	
	public String getTime() {
		return time.get();
	}
	
	public void setTime(String time) {
		this.time.set(time); 
	}
	
	public StringProperty timeProperty() {
		return time;
	}
	
	public String getToAddress() {
		return toAddress.get();
	}
	
	public void setToAddress(String toAddress) {
		this.toAddress.set(toAddress); 
	}
	
	public StringProperty toAddressProperty() {
		return toAddress;
	}
	
	public String getToCompany() {
		return toCompany.get();
	}
	
	public void setToCompany(String toCompany) {
		this.toCompany.set(toCompany); 
	}
	
	public StringProperty toCompanyProperty() {
		return toCompany;
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
	
	public int getQuantity() {
		return this.quantity.get();
	}
	
	public void setQuantity(int quantity) {
		this.quantity.set(quantity); 
	}
	
	public IntegerProperty quantityProperty() {
		return quantity;
	}	
	
	public int getSuccess() {
		return this.success.get();
	}
	
	public void setSuccess(int success) {
		this.success.set(success); 
	}
	
	public IntegerProperty successProperty() {
		return success;
	}
}
