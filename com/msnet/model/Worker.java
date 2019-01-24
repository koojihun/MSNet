package com.msnet.model;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class Worker {
	private StringProperty id;
	private StringProperty employeeNumber;
	private BooleanProperty isLogin;
	
	public Worker(String id, String employeeNumber, boolean isLogin) {
		this.id = new SimpleStringProperty(id);
		this.employeeNumber = new SimpleStringProperty(employeeNumber);
		this.isLogin = new SimpleBooleanProperty(isLogin);
	}
	
	public String getID() {
		return id.get();
	}
	
	public void setID(String id) {
		this.id.set(id); 
	}
	
	public StringProperty idProperty() {
		return id;
	}
	
	public String getEmployeeNumber() {
		return employeeNumber.get();
	}
	
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber.set(employeeNumber); 
	}
	
	public StringProperty employeeNumberProperty() {
		return employeeNumber;
	}
	
	public boolean getIsLogin() {
		return isLogin.get();
	}
	
	public void setIsLogin(boolean isLogin) {
		this.isLogin.set(isLogin); 
	}
	
	public BooleanProperty isLoginProperty() {
		return isLogin;
	}
	
}
