package com.msnet.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.msnet.util.Settings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PDB {
	private static ArrayList<Product> product_list;
	private static HashMap<String, NBox> nMap;
	private static HashMap<NDKey, NDBox> ndMap;
	
	private static ObservableList<NBox> nList;
	private static ObservableList<NDBox> ndList;
	private static ObservableList<Reservation> rList;
	
	public PDB() {
		nList = FXCollections.observableArrayList();
		ndList = FXCollections.observableArrayList();
		rList = FXCollections.observableArrayList();
		
		product_list = new ArrayList<>();
		nMap = new HashMap<String, NBox>();
		ndMap = new HashMap<NDKey, NDBox>();
		
		// read Reservation objects from disk into rMap & rList.
		fileReadReservation();
	}

	public static void refreshInventory(ArrayList<Product> products) {
		nList = FXCollections.observableArrayList();
		ndList = FXCollections.observableArrayList();
		
		product_list = new ArrayList<>();
		nMap = new HashMap<String, NBox>();
		ndMap = new HashMap<NDKey, NDBox>();
		
		for (Product p : products)
			addProduct(p);
		
		for (Reservation r : rList) {
			NBox nBox = nMap.get(r.getProductName());
			nBox.setAvailable(nBox.getAvailable() - r.getQuantity());
			
		
			NDBox ndBox = ndMap.get(new NDKey(r.getProductName(), r.getProductionDate(), r.getExpirationDate()));
			ndBox.setAvailable(ndBox.getAvailable() - r.getQuantity());
		}
	}
	
	public static void addProduct(Product p) {
		product_list.add(p);

		NBox nBox = nMap.get(p.getProductName());
		if (nBox == null) {
			NBox newBox = new NBox(p);
			nMap.put(p.getProductName(), newBox);
			nList.add(newBox);
		} else {
			nBox.addProduct(p);
		}

		NDKey key = new NDKey(p.getProductName(), p.getProductionDate(), p.getExpirationDate());
		NDBox ndBox = ndMap.get(key);
		if (ndBox == null) {
			NDBox newBox = new NDBox(p);
			ndMap.put(key, newBox);
			ndList.add(newBox);
		} else {
			ndBox.addProduct(p);
		}

	}
	
	public static void reserveProduct(String time, String address, String company, NDBox selectedNDBox, int count) {
		Reservation r = new Reservation(time, address, company, selectedNDBox, count);
		fileWriteReservation(r); 
		rList.add(r);
		
		NBox nBox = nMap.get(selectedNDBox.getProductName());
		nBox.setAvailable(nBox.getAvailable() - count);
		
		selectedNDBox.setAvailable(selectedNDBox.getAvailable() - count);
	}
	
	public static void reserveProduct(String time, String toAddress, String toCompany, String productName, String prodDate, String expDate,
			int quantity, int success, ArrayList<Product> productList) {
		
		Reservation r = new Reservation(time, toAddress, toCompany, productName, prodDate, expDate, quantity, success, productList);
		rList.add(r);
		
		NBox nBox = nMap.get(productName);
		if (nBox == null) {
			nBox = new NBox(productName);
			nBox.setAvailable(nBox.getAvailable() - quantity);
			nMap.put(productName, nBox);
		} else {
			nBox.setAvailable(nBox.getAvailable() - quantity);
		}
		
		NDBox ndBox = ndMap.get(new NDKey(productName, prodDate, expDate));
		if (ndBox == null) {
			ndBox = new NDBox(productName, prodDate, expDate);
			ndBox.setAvailable(ndBox.getAvailable() - quantity);
			ndMap.put(new NDKey(productName, prodDate, expDate), ndBox);
		} else {
			ndBox.setAvailable(ndBox.getAvailable() - quantity);
		}
	}
	
	public static void fileReadReservation() {
		try {
			File resDat = new File("C:\\Users\\" + Settings.getSysUsrName() + "\\AppData\\Roaming\\Bitcoin\\reservation.dat");
			if (resDat.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(resDat));
				JSONParser parser = new JSONParser();
				String input;
				while ((input = br.readLine()) != null) {
					JSONObject jsonObject = (JSONObject) parser.parse(input);
					String time = jsonObject.get("time").toString();
					String toAddress = jsonObject.get("toAddress").toString();
					String toCompany = jsonObject.get("toCompany").toString();
					String productName = jsonObject.get("productName").toString();
					String productionDate = jsonObject.get("productionDate").toString();
					String expirationDate = jsonObject.get("expirationDate").toString();
					int quantity = Integer.parseInt(jsonObject.get("quantity").toString());
					int success = Integer.parseInt(jsonObject.get("success").toString());
					ArrayList<Product> productList = (ArrayList<Product>) jsonObject.get("productList");
					
					PDB.reserveProduct(time, toAddress, toCompany, productName, productionDate, expirationDate, quantity, success, productList);
				}
				br.close();
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static void fileWriteReservation(Reservation r) {
		try {
			File file = new File("C:\\Users\\" + Settings.getSysUsrName() + "\\AppData\\Roaming\\Bitcoin\\reservation.dat");
			FileWriter fw = new FileWriter(file, true);
			JSONObject jsonObj = new JSONObject();
			
			jsonObj.put("time", r.getTime());
			jsonObj.put("toAddress", r.getToAddress());
			jsonObj.put("toCompany", r.getToCompany());
			jsonObj.put("productName", r.getProductName());
			jsonObj.put("productionDate", r.getProductionDate());
			jsonObj.put("expirationDate", r.getExpirationDate());
			jsonObj.put("quantity", r.getQuantity());
			jsonObj.put("success", r.getSuccess());
			JSONArray jsonArray = arrayProductToJSONArray(r.getProductList());
			jsonObj.put("productList", jsonArray);
			fw.write(jsonObj.toJSONString() + "\n");
			fw.close();
		} catch (IOException e) {
			System.err.println("File writer error");
			e.printStackTrace();
		}
	}
	
	public static JSONArray arrayProductToJSONArray(ArrayList<Product> productList) {
		JSONArray jsonArr = new JSONArray();
		for (Product p : productList) {
			JSONObject tmpObj = new JSONObject();
			tmpObj.put("productionDate", p.getProductionDate());
			tmpObj.put("expirationDate", p.getExpirationDate());
			tmpObj.put("productName", p.getProductName());
			tmpObj.put("pid", p.getPID());
			jsonArr.add(tmpObj);
		}
		return jsonArr;
	}
	
	public static ArrayList<Product> getProduct_list() {
		return product_list;
	}

	public static HashMap<String, NBox> getNMap() {
		return nMap;
	}

	public static HashMap<NDKey, NDBox> getNDMap() {
		return ndMap;
	}
	
	public static NDBox getNDBox(NDKey key) {
		return ndMap.get(key);
	}
	
	public static NBox getNBox(String prodName) {
		return nMap.get(prodName);
	}

	public static ObservableList<NBox> getNList() {
		return nList;
	}

	public static ObservableList<NDBox> getNDList() {
		return ndList;
	}

	public static ObservableList<Reservation> getRList() {
		return rList;
	}
	
}