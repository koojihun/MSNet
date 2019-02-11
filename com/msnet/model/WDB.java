package com.msnet.model;

import java.sql.SQLException;

import com.msnet.util.DB;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class WDB {

	private static ObservableList<Worker> workerList;

	public WDB() {
		workerList = FXCollections.observableArrayList();
		
		DB dr = new DB();
		dr.open();
		try {
			dr.readWorker();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dr.close();
	}
	
	public static void insert(String id, String password, String name, String phone) {
		Worker w = new Worker(id, name, false);
		workerList.add(w);
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
}
