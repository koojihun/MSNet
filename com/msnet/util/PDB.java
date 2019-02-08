package com.msnet.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
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
	private static ObservableList<Reservation> completedRList;

	public PDB(TableView<Reservation> reservationStatusTableView) {
		this.reservationStatusTableView = reservationStatusTableView;

		nList = FXCollections.observableArrayList();
		ndList = FXCollections.observableArrayList();
		rList = FXCollections.observableArrayList();
		completedRList = FXCollections.observableArrayList();

		nMap = new HashMap<String, NBox>();
		ndMap = new HashMap<NDKey, NDBox>();

		// read Reservation objects from disk into rList.
		// fileReadReservation();
		// fileReadCompletedReservation();

		DataReader dr = new DataReader("C:\\Users\\triz\\AppData\\Roaming\\msnetDB.db");
		dr.open();
		try {
			dr.readReservation();
			dr.readCompletedReservation();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dr.close();

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
		//fileWriteOneReservation(r);
		DataReader dr = new DataReader("C:\\Users\\triz\\AppData\\Roaming\\msnetDB.db");
		dr.open();
		try {
			dr.writeReservation(r);
		} catch (SQLException e) {
			System.err.println("reserveProduct할 때 문제 생김");
			e.printStackTrace();
		}
		dr.close();
		
		NBox nBox = nMap.get(selectedNDBox.getProductName());
		nBox.setAvailable(nBox.getAvailable() - count);

		selectedNDBox.setAvailable(selectedNDBox.getAvailable() - count);
	}
/*
	public static void reserveProduct(String time, String toAddress, String toCompany, String productName,
			String prodDate, String expDate, int quantity, int success, ArrayList<JSONObject> productList) {

		Reservation r = new Reservation(time, toAddress, toCompany, productName, prodDate, expDate, quantity, success,
				productList);
		rList.add(r);
	}
*/
	/*
	public static void addToCompletedRList(String time, String toAddress, String toCompany, String productName,
			String prodDate, String expDate, int quantity, int success, ArrayList<JSONObject> productList) {

		Reservation r = new Reservation(time, toAddress, toCompany, productName, prodDate, expDate, quantity, success,
				productList);
		completedRList.add(r);
	}
	*/

	/*
	 * public static void fileReadReservation() { try { File resDat = new File(
	 * "C:\\Users\\" + Settings.getSysUsrName() + "\\AppData\\Roaming\\Bitcoin\\
	 * reservation.dat"); if (resDat.exists()) { BufferedReader br = new
	 * BufferedReader(new FileReader(resDat)); JSONParser parser = new JSONParser();
	 * String input; while ((input = br.readLine()) != null) { JSONObject jsonObject
	 * = (JSONObject) parser.parse(input); String time =
	 * jsonObject.get("time").toString(); String toAddress =
	 * jsonObject.get("toAddress").toString(); String toCompany =
	 * jsonObject.get("toCompany").toString(); String productName =
	 * jsonObject.get("productName").toString(); String productionDate =
	 * jsonObject.get("productionDate").toString(); String expirationDate =
	 * jsonObject.get("expirationDate").toString(); int quantity =
	 * Integer.parseInt(jsonObject.get("quantity").toString()); int success =
	 * Integer.parseInt(jsonObject.get("success").toString()); ArrayList<JSONObject>
	 * productList = (ArrayList<JSONObject>) jsonObject.get("productList");
	 * 
	 * PDB.reserveProduct(time, toAddress, toCompany, productName, productionDate,
	 * expirationDate, quantity, success, productList); } br.close(); } } catch
	 * (IOException | ParseException e) { e.printStackTrace(); } }
	 */
	/*
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

	private static void fileDeleteReservation(Reservation r) throws IOException {
		File inputFile = new File(
				"C:\\Users\\" + Settings.getSysUsrName() + "\\AppData\\Roaming\\Bitcoin\\reservation.dat");
		File tempFile = new File("myTempFile.dat");

		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
		JSONObject jsonObj = new JSONObject();

		jsonObj.put("time", r.getTime());
		jsonObj.put("toAddress", r.getToAddress());
		jsonObj.put("toCompany", r.getToCompany());
		jsonObj.put("productName", r.getProductName());
		jsonObj.put("productionDate", r.getProductionDate());
		jsonObj.put("expirationDate", r.getExpirationDate());
		jsonObj.put("quantity", r.getQuantity());
		String lineToRemove = jsonObj.toJSONString();

		String currentLine;

		while ((currentLine = reader.readLine()) != null) {
			// trim newline when comparing with lineToRemove
			String trimmedLine = currentLine.trim();
			if (trimmedLine.contains(lineToRemove))
				continue;
			writer.write(currentLine + System.getProperty("line.separator"));
		}
		writer.close();
		reader.close();
		inputFile.delete();
		boolean successful = tempFile.renameTo(inputFile);
	}
*/
	/*
	 * 
	 * // 완료된 Reservation 정보를 파일에 읽기 public static void
	 * fileReadCompletedReservation() { try { File resDat = new File(
	 * "C:\\Users\\" + Settings.getSysUsrName() + "\\AppData\\Roaming\\Bitcoin\\
	 * salesData.dat"); if (resDat.exists()) { BufferedReader br = new
	 * BufferedReader(new FileReader(resDat)); JSONParser parser = new JSONParser();
	 * String input; while ((input = br.readLine()) != null) { JSONObject jsonObject
	 * = (JSONObject) parser.parse(input); String time =
	 * jsonObject.get("time").toString(); String toAddress =
	 * jsonObject.get("toAddress").toString(); String toCompany =
	 * jsonObject.get("toCompany").toString(); String productName =
	 * jsonObject.get("productName").toString(); String productionDate =
	 * jsonObject.get("productionDate").toString(); String expirationDate =
	 * jsonObject.get("expirationDate").toString(); int quantity =
	 * Integer.parseInt(jsonObject.get("quantity").toString()); int success =
	 * Integer.parseInt(jsonObject.get("success").toString()); ArrayList<JSONObject>
	 * productList = (ArrayList<JSONObject>) jsonObject.get("productList");
	 * 
	 * System.out.println(time + " " + toCompany + " " + productName + " " +
	 * success); addToCompletedRList(time, toAddress, toCompany, productName,
	 * productionDate, expirationDate, quantity, success, productList); }
	 * br.close(); } } catch (IOException | ParseException e) { e.printStackTrace();
	 * } }
	 */
	// 완료된 Reservation 정보를 파일에 쓰기
	private static void fileWriteCompletedReservation(Reservation r) {
		try {
			File file = new File(
					"C:\\Users\\" + Settings.getSysUsrName() + "\\AppData\\Roaming\\Bitcoin\\salesData.dat");
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
			sortCompletedReservation();
		} catch (IOException e) {
			System.err.println("File writer error");
			e.printStackTrace();
		}
	}

	// 완료된 Reservation 정보 정렬하기
	public static void sortCompletedReservation() {
		ArrayList<String> tmpList = new ArrayList<String>();
		try {
			File resDat = new File(
					"C:\\Users\\" + Settings.getSysUsrName() + "\\AppData\\Roaming\\Bitcoin\\salesData.dat");

			BufferedReader br = new BufferedReader(new FileReader(resDat));
			String input;
			while ((input = br.readLine()) != null) {
				tmpList.add(input);
			}

			tmpList = AscendingOrder(tmpList);

			BufferedWriter bw = new BufferedWriter(new FileWriter(resDat));

			for (int i = 0; i < tmpList.size(); i++) {
				System.out.println("tmpList.get(" + i + "): " + tmpList.get(i));
				bw.write(tmpList.get(i));
				bw.newLine();
			}
			br.close();
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList AscendingOrder(ArrayList<String> arrList) {
		String tmp;
		ArrayList<String> newArrList = new ArrayList<String>();
		for (int i = 0; i < arrList.size() - 1; i++) {
			for (int j = i + 1; j < arrList.size(); j++) {
				if (arrList.get(i).compareTo(arrList.get(j)) > 0) {
					tmp = arrList.get(i);
					arrList.set(i, arrList.get(j));
					arrList.set(j, tmp);
				}
			}
		}
		newArrList = arrList;
		return newArrList;
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

	public static ObservableList<Reservation> getComplitedRList() {
		return completedRList;
	}

	public static Reservation findReservation(String prodName, String productionDate, String expirationDate,
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

	public static boolean isExistPID_in_Reservation(Reservation r, String pid) {
		ArrayList<JSONObject> tmpProductList = r.getProductList();
		String tmpPID;
		for (JSONObject p : tmpProductList) {
			tmpPID = (String) p.get("PID");
			if (pid.equals(tmpPID)) {
				return true;
			}
		}
		return false;
	}

	public static String sendProduct(String bitcoin_address, String pid, String prodName, String productionDate,
			String expirationDate, String wid) {
		// sendReservation: reservation that matches the information to be sent in the
		// reservation list
		Reservation sendReservation = PDB.findReservation(prodName, productionDate, expirationDate, bitcoin_address);

		if (sendReservation == null) {
			// When there is no reservation matching the condition in the reservation list,
			// an alert window
			System.out.println("======== 존재하지 않는 reservation =========");
			return "notExist";
		} else if (isExistPID_in_Reservation(sendReservation, pid)) {
			System.out.println("========== 이미 보낸 제품 ===========");
			return "sentProduct";
		} else if (sendReservation.getQuantity() > sendReservation.getSuccess()) {
			// Execute send_to_address when there is a reservation matching the condition in
			// the reservation list
			System.out.println("=================================");
			System.out.println(prodName);
			System.out.println(pid);
			System.out.println(productionDate);
			System.out.println(expirationDate);
			JSONObject p = new JSONObject();
			p.put("production date", productionDate);
			p.put("expiration date", expirationDate);
			p.put("prodName", prodName);
			p.put("PID", pid);

			DataReader dr = new DataReader("C:\\Users\\triz\\AppData\\Roaming\\msnetDB.db");

			if (sendReservation.getQuantity() - 1 == sendReservation.getSuccess()) {
				System.out.println("========== 마지막 상품 ==========");	
				try {
					dr.open();			
					sendReservation.getProductList().add(p); // productList에 p 추가
					dr.writeProductList(pid, prodName, productionDate, expirationDate, sendReservation.getRid(), wid); // db에서도 p 추가
					sendReservation.setSuccess(sendReservation.getSuccess() + 1); // success + 1		
					dr.setSuccessPlusOne(sendReservation.getRid()); // db에서도 success + 1
					MainApp.bitcoinJSONRPClient.send_to_address(bitcoin_address, pid);
					completedRList.add(sendReservation);
					dr.writeCompletedReservation(sendReservation);
					rList.remove(sendReservation);	
					dr.deleteReservation(sendReservation);				
					dr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}	
				// PDB.fileWriteCompletedReservation(sendReservation);
				// PDB.fileDeleteReservation(sendReservation);		
			} else {		
				try {
					dr.open();
					sendReservation.getProductList().add(p);
					dr.writeProductList(pid, prodName, productionDate, expirationDate, sendReservation.getRid(), wid);
					sendReservation.setSuccess(sendReservation.getSuccess() + 1); // success
					dr.setSuccessPlusOne(sendReservation.getRid());					
					MainApp.bitcoinJSONRPClient.send_to_address(bitcoin_address, pid);
					dr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}		
			}
			//PDB.fileWriteAllReservation();
			return "success";
		} else {
			System.out.println("===== 할당량 끝 =====");
			return "overflow";
		}
	}
}
