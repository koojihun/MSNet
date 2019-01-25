package com.msnet.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class WorkerInfo {
	private StringProperty id;
	private StringProperty password;
	private StringProperty name;
	private StringProperty phone;
	
	public WorkerInfo(String id, String password, String name, String phone) {
		this.id = new SimpleStringProperty(id);
		this.password = new SimpleStringProperty(password);
		this.name = new SimpleStringProperty(name);
		this.phone = new SimpleStringProperty(phone);
	}
	
	public String getID() {
		return id.get();
	}
	
	public void setID(String id) {
		this.id.set(id); 
	}
	
	public StringProperty PropertyID() {
		return id;
	}
	
	public String getPassword() {
		return password.get();
	}
	
	public void setPassword(String password) {
		this.password.set(password); 
	}
	
	public StringProperty passwordProperty() {
		return password;
	}
	
	public String getName() {
		return name.get();
	}
	
	public void setName(String name) {
		this.name.set(name); 
	}
	
	public StringProperty nameProperty() {
		return name;
	}
	
	public String getPhone() {
		return phone.get();
	}
	
	public void setPhone(String phone) {
		this.phone.set(phone); 
	}
	
	public StringProperty phoneProperty() {
		return phone;
	}
}
