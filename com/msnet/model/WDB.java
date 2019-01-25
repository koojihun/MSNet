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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.msnet.util.Settings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class WDB {

	private static ObservableList<Worker> workerList;
	private static ObservableList<WorkerInfo> workerInfoList;
	private static TableView<Worker> workerTableView;

	public WDB(TableView<Worker> workerTableView) {
		this.workerTableView = workerTableView;
		workerList = FXCollections.observableArrayList();
		workerInfoList = FXCollections.observableArrayList();
		fileReadWorkerInfo();
	}

	public static boolean searchExist(String id, String password, String name, String phone) {	
		
		boolean isExist = false;
		
		for (int i = 0; i < workerList.size(); i++) {
			isExist = workerList.get(i).getID().equals(id);
			if (isExist) {
				break;
			}
		}
		
		if(isExist) {
			// 동일 아이디가 존재할 때 false를 return
			return true;
		} else {
			// 동일 아이디가 존재하지 않을 때 true를 return하고 workerList와 파일에 쓴다
			Worker w = new Worker(id, name, false);
			WorkerInfo wInfo = new WorkerInfo(id, password, name, phone);
			workerList.add(w);
			workerInfoList.add(wInfo);
			
			fileWriteWorkerInfo(wInfo);

			showWorker(); // worker table 갱신
			
			return false;
		}
	}

	public static void fileReadWorkerInfo() {
		try {
			workerList = FXCollections.observableArrayList();
			workerInfoList = FXCollections.observableArrayList();
			
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

	public static void showWorker() {
		workerTableView.setItems(workerList);
	}
	
	public static void fileWriteWorkerInfo(WorkerInfo w) {
		try {
			File file = new File(
					"C:\\Users\\" + Settings.getSysUsrName() + "\\AppData\\Roaming\\Bitcoin\\workerInfo.dat");
			FileWriter fw = new FileWriter(file, true);
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("id", w.getID());
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

	public static void setLogin(String id, boolean isLogin) {
		Worker tmp;
		for (int i = 0; i < workerList.size(); i++) {
			tmp = workerList.get(i);
			if (tmp.getID().equals(id)) {
				tmp.setIsLogin(isLogin);
				break;
			}
		}		
		showWorker();
	}

	public static boolean confirmIDPW(String id, String password) {
		WorkerInfo tmp;
		for(int i = 0; i < workerInfoList.size(); i++) {
			tmp = WDB.workerInfoList.get(i);
			if (tmp.getID().equals(id) && tmp.getPassword().equals(password)) {
				return true;
			}
		}
		return false;
	}
}
