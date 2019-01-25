package com.msnet.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Worker {
	private StringProperty id;
	private StringProperty name;
	private BooleanProperty isLogin;
	
	public Worker(String id, String name, boolean isLogin) {
		this.id = new SimpleStringProperty(id);
		this.name = new SimpleStringProperty(name);
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
	
	public String getName() {
		return name.get();
	}
	
	public void setName(String name) {
		this.name.set(name); 
	}
	
	public StringProperty nameProperty() {
		return name;
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
