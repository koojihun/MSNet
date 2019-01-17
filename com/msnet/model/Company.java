package com.msnet.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Company {
	private StringProperty company_name;
	private StringProperty company_address;
	private StringProperty bitcoin_address;
	
	public Company(String company_name, String company_address, String bitcoin_address) {
		this.company_name = new SimpleStringProperty(company_name);
		this.company_address = new SimpleStringProperty(company_address);
		this.bitcoin_address = new SimpleStringProperty(bitcoin_address);
	}
	
	public String getName() {
		return company_name.get();
	}
	
	public void setName(String company_name) {
		this.company_name.set(company_name); 
	}
	
	public StringProperty nameProperty() {
		return company_name;
	}
	
	public String getAddress() {
		return company_address.get();
	}
	
	public void setAddress(String company_address) {
		this.company_address.set(company_address); 
	}
	
	public StringProperty addressProperty() {
		return company_address;
	}
	
	public String getBitcoinAddress() {
		return bitcoin_address.get();
	}
	
	public void setBitcoinAddress(String bitcoin_address) {
		this.bitcoin_address.set(bitcoin_address); 
	}
	
	public StringProperty bitcoinAddressProperty() {
		return bitcoin_address;
	}
}
