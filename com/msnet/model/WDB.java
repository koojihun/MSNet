package com.msnet.model;
/**
 * Worker DB
 * @author triz
 *
 */

import java.sql.SQLException;

import com.msnet.util.DataReader;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class WDB {

	private static ObservableList<Worker> workerList;
	private static TableView<Worker> workerTableView;

	public WDB(TableView<Worker> inputTableView) {
		workerTableView = inputTableView;
		workerList = FXCollections.observableArrayList();

		DataReader dr = new DataReader("C:\\Users\\triz\\AppData\\Roaming\\msnetDB.db");
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

	public static boolean isLogin(String id) {
		for (Worker w : workerList) {
			if (w.getID().equals(id)) {
				return w.getIsLogin();
			}
		}
		return false;
	}
}
