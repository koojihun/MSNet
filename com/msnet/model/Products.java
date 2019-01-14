package com.msnet.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Products {
	List<Map> productList;

	public Products(List<Map> productList) {
		this.productList = productList;
	}

	public Map<String, ArrayList<Product>> makeProducts() {

		Map<String, ArrayList<Product>> products = new HashMap<String, ArrayList<Product>>();
		ArrayList<String> prodName = new ArrayList<String>();
		for (Map product : productList) {
			String name = (String) product.get("prodName");
			if(prodName.contains(name)) {
				//products.put(name, );
			}
		}

		return products;
	}
}
