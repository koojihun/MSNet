package com.msnet.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;

import com.msnet.MainApp;
import com.msnet.model.NBox;
import com.msnet.model.NDBox;
import com.msnet.model.NDKey;
import com.msnet.model.Product;
import com.msnet.model.Reservation;
import com.msnet.view.SystemOverviewController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PDB {
	private static HashMap<String, NBox> nMap;
	private static HashMap<NDKey, NDBox> ndMap;

	private static ObservableList<NBox> nList;
	private static ObservableList<NDBox> ndList;
	private static ObservableList<Reservation> rList;
	private static ObservableList<Reservation> completedRList;

	public PDB() {
		nList = FXCollections.observableArrayList();
		ndList = FXCollections.observableArrayList();
		rList = FXCollections.observableArrayList();
		completedRList = FXCollections.observableArrayList();

		nMap = new HashMap<String, NBox>();
		ndMap = new HashMap<NDKey, NDBox>();

		DB db = new DB();
		db.readReservation();
		db.readCompletedReservation();
	}

	public static void refreshInventory(List<JSONObject> nddBoxes) {
		nList = FXCollections.observableArrayList();
		ndList = FXCollections.observableArrayList();

		nMap = new HashMap<String, NBox>();
		ndMap = new HashMap<NDKey, NDBox>();

		if (nddBoxes == null)
			return;

		for (JSONObject obj : nddBoxes) {
			String result = (String) obj.get("result");
			int length = result.length();
			String expiration_date = result.substring(length - 15);
			String production_date = result.substring(length - 30, length - 15);
			String prodName = result.substring(0, length - 30);

			int quantity = Integer.parseInt((String) obj.get("quantity"));

			NBox nBox = nMap.get(prodName);
			if (nBox == null) {
				nBox = new NBox(prodName, quantity);
				nMap.put(prodName, nBox);
				nList.add(nBox);
			} else {
				nBox.increaseQuantityAndAvailable(quantity);
			}

			NDBox newNDBox = new NDBox(prodName, production_date, expiration_date, quantity);
			ndMap.put(new NDKey(prodName, production_date, expiration_date), newNDBox);
			ndList.add(newNDBox);
		}

		for (Reservation r : rList) {
			NBox nBox = nMap.get(r.getProductName());
			nBox.setAvailable(nBox.getQuantity() - (r.getQuantity() - r.getSuccess()));

			NDBox ndBox = ndMap.get(new NDKey(r.getProductName(), r.getProductionDate(), r.getExpirationDate()));
			ndBox.setAvailable(ndBox.getQuantity() - (r.getQuantity() - r.getSuccess()));
		}
	}

	public static void reserveProduct(String time, String address, String company, NDBox selectedNDBox, int count) {
		Reservation r = new Reservation(time, address, company, selectedNDBox, count);
		rList.add(r);

		DB db = new DB();
		db.writeReservation(r);

		NBox nBox = nMap.get(selectedNDBox.getProductName());
		nBox.setAvailable(nBox.getAvailable() - count);

		selectedNDBox.setAvailable(selectedNDBox.getAvailable() - count);
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

	public static ObservableList<Reservation> getComplitedRList() {
		return completedRList;
	}

	public static Reservation findReservation(String prodName, String productionDate, String expirationDate,
			String bitcoin_address) {
		Reservation tmpR;

		for (int i = 0; i < rList.size(); i++) {
			tmpR = rList.get(i);
			if (prodName.equals(tmpR.getProductName()) && productionDate.equals(tmpR.getProductionDate())
					&& expirationDate.equals(tmpR.getExpirationDate()) && bitcoin_address.equals(tmpR.getToAddress())
					&& (tmpR.getQuantity() >= tmpR.getSuccess())) {
				// Reservation 중 보낼 물건의 정보와 일치하고 [Quantity > Success]인 tmpR만 리스트에 추가
				return tmpR;
			}
		}
		return null;
	}

	/*
	 * public static boolean isExistPID_in_Reservation(Reservation r, String pid) {
	 * ArrayList<Product> tmpProductList = r.getProductList(); String tmpPID; for
	 * (Product p : tmpProductList) { tmpPID = p.getPid(); if (pid.equals(tmpPID)) {
	 * return true; } } return false; }
	 */
	public static String sendProduct(String bitcoin_address, String pid, String prodName, String productionDate,
			String expirationDate, String wid) {
		// sendReservation: reservation that matches the information to be sent in the
		// reservation list
		Reservation sendReservation = PDB.findReservation(prodName, productionDate, expirationDate, bitcoin_address);
		DB db = new DB();
		String result = null;
		if (sendReservation == null) {
			// When there is no reservation matching the condition in the reservation list,
			// an alert window
			System.out.println("======== 존재하지 않는 reservation =========");
			return "notExist";
		} else if (sendReservation.getQuantity() > sendReservation.getSuccess()) {
			// Execute send_to_address when there is a reservation matching the condition in
			// the reservation list
			Product p = new Product(productionDate, expirationDate, prodName, pid, wid);
			result = MainApp.bitcoinJSONRPClient.send_to_address(bitcoin_address, pid);
			
			if ( !result.equals("No Product.")) {
				// result가 "No Product."가 아니므로 정상적인 Transaction 발생.
				MainApp.bitcoinJSONRPClient.set_generate();
				sendReservation.getProductList().add(p); // productList에 p 추가
				db.writeProductList(pid, prodName, productionDate, expirationDate, sendReservation.getRid(), wid); // db에서도  p 추가
				sendReservation.setSuccess(sendReservation.getSuccess() + 1); // success + 1
				db.setSuccessPlusOne(sendReservation.getRid()); // db에서도 success + 1
				
				if (sendReservation.getQuantity() == sendReservation.getSuccess()) {
					// reservation에서 마지막 tx 상황일 때
					completedRList.add(sendReservation);
					db.writeCompletedReservation(sendReservation);
					rList.remove(sendReservation);
					db.deleteReservation(sendReservation);
				} 
				
				return "success";
			} else {
				// result가 "No Product."이므로 이미 보낸 제품
				System.out.println("========== 이미 보낸 제품 ===========");
				return "sentProduct";
			}
		} else {
			System.out.println("===== 할당량 끝 =====");
			return "overflow";
		}
	}
}
