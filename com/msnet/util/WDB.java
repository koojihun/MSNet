package com.msnet.util;

import com.msnet.model.Worker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class WDB {

	private static ObservableList<Worker> workerList;

	public WDB() {
		workerList = FXCollections.observableArrayList();
		DB db = new DB();
		db.readWorker();
	}
	
	public static boolean signIn(String id, String password) {
		DB db = new DB();
		boolean result = db.signIn(id, password);
		if (result)
			setIsLogin(id, true);
		return result;
	}
	
	public static boolean signUp(String id, String password, String name, String phone) {
		DB db = new DB();
		boolean result = db.signUp(id, password, name, phone);
		
		if (result)
			insert(id, password, name, phone);
		
		return result;
	}
	
	public static void insert(String id, String password, String name, String phone) {
		Worker w = new Worker(id, name, false);
		workerList.add(w);
	}

	public static void delete(Worker w) {
		workerList.remove(w);
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
}
