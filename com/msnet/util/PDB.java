package com.msnet.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.msnet.MainApp;
import com.msnet.model.NBox;
import com.msnet.model.NDBox;
import com.msnet.model.NDKey;
import com.msnet.model.Reservation;
import com.msnet.view.SystemOverviewController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class PDB {
	private static HashMap<String, NBox> nMap;
	private static HashMap<NDKey, NDBox> ndMap;
	private static TableView<Reservation> reservationStatusTableView;

	private static ObservableList<NBox> nList;
	private static ObservableList<NDBox> ndList;
	private static ObservableList<Reservation> rList;

	public PDB(TableView<Reservation> reservationStatusTableView) {
		this.reservationStatusTableView = reservationStatusTableView;

		nList = FXCollections.observableArrayList();
		ndList = FXCollections.observableArrayList();
		rList = FXCollections.observableArrayList();

		nMap = new HashMap<String, NBox>();
		ndMap = new HashMap<NDKey, NDBox>();

		// read Reservation objects from disk into rList.
		fileReadReservation();
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

			System.out.println(prodName);
			
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
		fileWriteOneReservation(r);

		NBox nBox = nMap.get(selectedNDBox.getProductName());
		nBox.setAvailable(nBox.getAvailable() - count);

		selectedNDBox.setAvailable(selectedNDBox.getAvailable() - count);
	}

	public static void reserveProduct(String time, String toAddress, String toCompany, String productName,
			String prodDate, String expDate, int quantity, int success, ArrayList<JSONObject> productList) {

		Reservation r = new Reservation(time, toAddress, toCompany, productName, prodDate, expDate, quantity, success,
				productList);
		rList.add(r);
	}

	public static void fileReadReservation() {
		try {
			File resDat = new File(
					"C:\\Users\\" + Settings.getSysUsrName() + "\\AppData\\Roaming\\Bitcoin\\reservation.dat");
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
					ArrayList<JSONObject> productList = (ArrayList<JSONObject>) jsonObject.get("productList");

					PDB.reserveProduct(time, toAddress, toCompany, productName, productionDate, expirationDate,
							quantity, success, productList);
				}
				br.close();
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	public static void fileWriteAllReservation() {
		try {
			File file = new File(
					"C:\\Users\\" + Settings.getSysUsrName() + "\\AppData\\Roaming\\Bitcoin\\reservation.dat");
			FileWriter fw = new FileWriter(file, false);

			Reservation r;
			JSONObject jsonObj = new JSONObject();
			for (int i = 0; i < rList.size(); i++) {
				r = rList.get(i);
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
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void fileWriteOneReservation(Reservation r) {
		try {
			File file = new File(
					"C:\\Users\\" + Settings.getSysUsrName() + "\\AppData\\Roaming\\Bitcoin\\reservation.dat");
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

	public static JSONArray arrayProductToJSONArray(ArrayList<JSONObject> productList) {
		JSONArray jsonArr = new JSONArray();
		for (JSONObject p : productList) {
			jsonArr.add(p);
		}
		return jsonArr;
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

	public static Reservation getSendRList(String prodName, String productionDate, String expirationDate,
			String bitcoin_address) {
		ArrayList<Reservation> reservationList = new ArrayList<Reservation>();
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

	public static void sendProduct(String bitcoin_address, String pid, String prodName, String productionDate,
			String expirationDate) {
		Reservation sendReservation = PDB.getSendRList(prodName, productionDate, expirationDate, bitcoin_address);
		if (sendReservation == null) {
			// Reservation list에 조건에 부합하는 reservation이 없을 때 경고창 띄움
			String head = "Not exists the reservation";
			String body = "Please send a correct product";
			new Alert(SystemOverviewController.getSystemOverview(), head, body);
		} else {
			// Reservation list에 조건에 부합하는 reservation이 있을 때 send_to_address 실행
			PDB.fileWriteAllReservation();
		}

		JSONObject p = new JSONObject();
		p.put("production date", productionDate);
		p.put("expiration date", productionDate);
		p.put("prodName", productionDate);
		p.put("PID", pid);

		sendReservation.getProductList().add(p);
		sendReservation.setSuccess(sendReservation.getSuccess() + 1);

		MainApp.bitcoinJSONRPClient.send_to_address(bitcoin_address, pid);
	}
}
