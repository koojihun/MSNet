package com.msnet.model;

import java.util.ArrayList;
import java.util.Map;

import org.json.simple.JSONObject;

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
	private ArrayList<JSONObject> productList;

	// Reservation을 아예 새로 생성하는 경우.
	public Reservation(String time, String toAddress, String toCompany, NDBox selectedNDBox, int quantity) {
		this.time = new SimpleStringProperty(time);
		this.toAddress = new SimpleStringProperty(toAddress);
		this.toCompany = new SimpleStringProperty(toCompany);
		this.productName = new SimpleStringProperty(selectedNDBox.getProductName());
		this.productionDate = new SimpleStringProperty(selectedNDBox.getProductionDate());
		this.expirationDate = new SimpleStringProperty(selectedNDBox.getExpirationDate());
		this.quantity = new SimpleIntegerProperty(quantity);
		this.success = new SimpleIntegerProperty(0);
		this.productList = new ArrayList<>();
	}

	// Reservation을 파일에서 읽어와 ArrayList 형태로 추가하는 경우.
	public Reservation(String time, String toAddress, String toCompany, String productName, String prodDate, String expDate, int quantity, int success,
			ArrayList<JSONObject> productList) {
		this.time = new SimpleStringProperty(time);
		this.toAddress = new SimpleStringProperty(toAddress);
		this.toCompany = new SimpleStringProperty(toCompany);
		this.productName = new SimpleStringProperty(productName);
		this.productionDate = new SimpleStringProperty(prodDate);
		this.expirationDate = new SimpleStringProperty(expDate);
		this.quantity = new SimpleIntegerProperty(quantity);
		this.success = new SimpleIntegerProperty(success);
		this.productList = productList;
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

	public ArrayList<JSONObject> getProductList() {
		return this.productList;
	}

	public void setProductList(ArrayList<JSONObject> productList) {
		this.productList = productList;
	}
}
