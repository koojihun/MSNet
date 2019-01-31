package com.msnet.model;
/**
 * Worker DB
 * @author triz
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.msnet.util.Settings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class WDB {

	private static ObservableList<Worker> workerList;
	private static ArrayList<WorkerInfo> workerInfoList;
	private static TableView<Worker> workerTableView;

	public WDB(TableView<Worker> inputTableView) {
		workerTableView = inputTableView;
		workerList = FXCollections.observableArrayList();
		workerInfoList = new ArrayList<>();
		fileReadWorkerInfo();
	}

	public static boolean searchExist(String id) {	
		for (int i = 0; i < workerList.size(); i++) {
			if(workerList.get(i).getID().equals(id))
				return true;
		}
		return false;
	}
	
	public static void insert(String id, String password, String name, String phone) {
		Worker w = new Worker(id, name, false);
		WorkerInfo wInfo = new WorkerInfo(id, password, name, phone);
		workerList.add(w);
		workerInfoList.add(wInfo);
		fileWriteWorkerInfo(wInfo);
	}

	public static void fileReadWorkerInfo() {
		try {			
			File resDat = new File(
					"C:\\Users\\" + Settings.getSysUsrName() + "\\AppData\\Roaming\\Bitcoin\\workerInfo.dat");
			if (resDat.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(resDat));
				JSONParser parser = new JSONParser();
				String input;
				Worker w;
				WorkerInfo wi;
				boolean isLogin = false;
				while ((input = br.readLine()) != null) {
					JSONObject jsonObject = (JSONObject) parser.parse(input);
					String id = jsonObject.get("id").toString();
					String password = jsonObject.get("password").toString();
					String name = jsonObject.get("name").toString();
					String phone = jsonObject.get("phone").toString();
					
					w = new Worker(id, name, false);
					wi = new WorkerInfo(id, password, name, phone);
					
					workerList.add(w);
					workerInfoList.add(wi);
				}
				br.close();
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static void fileWriteWorkerInfo(WorkerInfo w) {
		try {
			File file = new File(
					"C:\\Users\\" + Settings.getSysUsrName() + "\\AppData\\Roaming\\Bitcoin\\workerInfo.dat");
			FileWriter fw = new FileWriter(file, true);
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("id", w.getId());
			jsonObj.put("password", w.getPassword());
			jsonObj.put("name", w.getName());
			jsonObj.put("phone", w.getPhone());
			fw.write(jsonObj.toJSONString() + "\n");
			fw.close();
		} catch (IOException e) {
			System.err.println("File writer error");
			e.printStackTrace();
		}
	}

	public static ObservableList<Worker> getWorkerList() {
		return workerList;
	}

	public static void setIsLogin(String id, boolean isLogin) {
		Worker tmp;
		for (int i = 0; i < workerList.size(); i++) {
			tmp = workerList.get(i);
			if (tmp.getID().equals(id)) {
				tmp.setIsLogin(isLogin);
				break;
			}
		}
	}
	
	public static boolean isLogin(String id) {
		for (Worker w : workerList) {
			if (w.getID().equals(id)) {
				return w.getIsLogin();
			}
		}
		return false;
	}

	public static boolean confirmIDPW(String id, String password) {
		WorkerInfo tmp;
		for(int i = 0; i < workerInfoList.size(); i++) {
			tmp = WDB.workerInfoList.get(i);
			if (tmp.getId().equals(id) && tmp.getPassword().equals(password)) {
				return true;
			}
		}
		return false;
	}
}
