package com.msnet.model;

public class NDKey {
	private String prodName;
	private String productionDate;
	private String expirationDate;

	public NDKey(String prodName, String productionDate, String expirationDate) {
		this.prodName = prodName;
		this.productionDate = productionDate;
		this.expirationDate = expirationDate;
	}

	public String getProdName() {
		return prodName;
	}
	
	public String getProductionDate() {
		return productionDate;
	}
	
	public String getExpirationDate() {
		return expirationDate;
	}
	
	
	@Override
	public int hashCode() {
		String result = prodName + productionDate + expirationDate;
		return result.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		NDKey target = (NDKey) obj;

		// todo:
		if (prodName != null ? !prodName.equals(target.prodName) : target.prodName != null)
			return false;
		if (productionDate != null ? !productionDate.equals(target.productionDate) : target.productionDate != null)
			return false;		
		return expirationDate != null ? expirationDate.equals(target.expirationDate) : target.expirationDate == null;
	}

}
