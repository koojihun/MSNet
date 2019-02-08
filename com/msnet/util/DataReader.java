package com.msnet.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.sqlite.SQLiteConfig;

import com.msnet.model.Reservation;
import com.msnet.model.WDB;
import com.msnet.model.Worker;
import com.msnet.model.Product;;

public class DataReader {
	private Connection connection;
	private String dbFileName;
	private boolean isOpened = false;
	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.err.println("org.sqlite.JDB를 찾지 못했습니다...ㅠㅠ");
			e.printStackTrace();
		}
	}

	public DataReader(String databaseFileName) {
		this.dbFileName = databaseFileName;
	}

	public boolean open() {
		try {
			SQLiteConfig config = new SQLiteConfig();
			this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbFileName, config.toProperties());
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		isOpened = true;
		return true;
	}

	public boolean close() {
		if (this.isOpened == false) {
			return true;
		}
		try {
			this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void readWorker() throws SQLException {
		if (this.isOpened == false) {
			return;
		}
		String query = "select * from workerinfo;";
		PreparedStatement prep = this.connection.prepareStatement(query);
		ResultSet row = prep.executeQuery();
		Worker w;
		while (row.next()) {
			w = new Worker(row.getString("id"), row.getString("name"), false);
			WDB.getWorkerList().add(w);
		}
		row.close();
		prep.close();
	}

	public void readReservation() throws SQLException {
		if (this.isOpened == false) {
			return;
		}
		String query = "select * from reservation;";
		PreparedStatement prep = this.connection.prepareStatement(query);
		ResultSet row = prep.executeQuery();
		ArrayList<JSONObject> tmpList = new ArrayList<JSONObject>();
		Reservation r;
		
		while (row.next()) {
			tmpList = readRProductList(row.getInt("rid"));
			r = new Reservation(row.getInt("rid"), row.getString("time"), row.getString("toAddress"), row.getString("toCompany"), row.getString("productName"),
					row.getString("productionDate"), row.getString("expirationDate"), row.getInt("quantity"), row.getInt("success"), tmpList);
			PDB.getRList().add(r);
		}
		row.close();
		prep.close();
	}
	
	// reservation table에 r을 추가하고, 새로 생성된 rid(autoincrement를 통해 자동생성)를 r.setRid()를 이용해 set함.
	public void writeReservation(Reservation r) throws SQLException {
		if(this.isOpened == false) {
			return;
		}
		String query = "insert into reservation (time, toAddress, toCompany, productName, productionDate, expirationDate, quantity, success) values (?, ?, ?, ?, ?, ?, ?, ?);";
		PreparedStatement prep = this.connection.prepareStatement(query);
		prep.setString(1, r.getTime());
		prep.setString(2, r.getToAddress());
		prep.setString(3, r.getToCompany());
		prep.setString(4, r.getProductName());
		prep.setString(5, r.getProductionDate());
		prep.setString(6, r.getExpirationDate());
		prep.setInt(7, r.getQuantity());
		prep.setInt(8, r.getSuccess());	
		prep.executeUpdate();
		
		query = "select rid from reservation where time=?;"; // time이 겹치지 않는 키라고 생각하고 찾아줌.
		PreparedStatement prep2 = this.connection.prepareStatement(query);
		prep2.setString(1, r.getTime());
		ResultSet row = prep2.executeQuery();
		
		r.setRid(row.getInt("rid"));
		
		row.close();
		prep.close();
		prep2.close();
	}
	
	public void readCompletedReservation() throws SQLException {
		if(this.isOpened == false) {
			return;
		}
		String query = "select * from completedReservation;";
		PreparedStatement prep = this.connection.prepareStatement(query);
		ResultSet row = prep.executeQuery();
		ArrayList<JSONObject> tmpList = new ArrayList<JSONObject>();
		Reservation r;
		
		while(row.next()) {
			tmpList = readRProductList(row.getInt("rid"));
			r = new Reservation(row.getInt("rid"), row.getString("time"), row.getString("toAddress"), row.getString("toCompany"), row.getString("productName"),
					row.getString("productionDate"), row.getString("expirationDate"), row.getInt("quantity"), row.getInt("success"), tmpList);
			PDB.getComplitedRList().add(r);			
		}
		row.close();
		prep.close();
	}
	
	// reservation을 completedReservation table에 추가하고 
	public void writeCompletedReservation(Reservation r) throws SQLException {
		if(this.isOpened == false) {
			return;
		}
		String query = "insert into completedReservation (rid, time, toAddress, toCompany, productName, productionDate, expirationDate, quantity, success) values (?, ?, ?, ?, ?, ?, ?, ?, ?);";
		PreparedStatement prep = this.connection.prepareStatement(query);
		prep.setInt(1, r.getRid());
		prep.setString(2, r.getTime());
		prep.setString(3, r.getToAddress());
		prep.setString(4, r.getToCompany());
		prep.setString(5, r.getProductName());
		prep.setString(6, r.getProductionDate());
		prep.setString(7, r.getExpirationDate());
		prep.setInt(8, r.getQuantity());
		prep.setInt(9, r.getSuccess());
		prep.executeUpdate();
		
		prep.close();
	}
	
	public void deleteReservation(Reservation r) throws SQLException {
		if(this.isOpened == false) {
			return;
		}		
		String query = "delete from reservation where rid=?;";
		PreparedStatement prep = this.connection.prepareStatement(query);
		int rid = r.getRid();
		prep.setString(1, String.valueOf(rid));
		prep.executeUpdate();
		prep.close();
	}
	
	public ArrayList<JSONObject> readRProductList(int rid) {
		if (this.isOpened == false) {
			return null;
		}
		ArrayList<JSONObject> pList = new ArrayList<JSONObject>();
		JSONObject tmp = new JSONObject();

		try {
			String query = "select * from r_productList where rid=?;";
			PreparedStatement prep = this.connection.prepareStatement(query);
			prep.setInt(1, rid);
			ResultSet row = prep.executeQuery();
			while (row.next()) {
				tmp.put("production date", row.getString("productionDate"));
				tmp.put("expiration date", row.getString("expirationDate"));
				tmp.put("prodName", row.getString("productName"));
				tmp.put("PID", row.getString("pid"));
				pList.add(tmp);
			}
			return pList;
		} catch (SQLException e) {
			System.out.println("readRProductList에서 문제 있음");
			e.printStackTrace();
			return null;
		}
	}

	// DB에 같은 id가 존재하면 false 반환, 존재하지 않으면 true 반환하고 DB에 가입 정보 저장
	public boolean signUp(String id, String password, String name, String phone) throws SQLException {
		if (this.isOpened == false) {
			return false;
		}

		boolean result = true;
		String query = "select * from workerInfo where id = ?;";
		PreparedStatement prep = this.connection.prepareStatement(query);
		prep.setString(1, id);
		ResultSet row = prep.executeQuery();

		if (row.next()) {
			result = false; // 같은 id가 존재하면 false 반환
		}

		// id가 존재하지 않을 때는 worker의 정보를 workerinfo에 insert
		if (result) {
			query = "insert into workerInfo (id, password, name, phone) values (?, ?, ?, ?);";
			prep = this.connection.prepareStatement(query);
			prep.setString(1, id);
			prep.setString(2, password);
			prep.setString(3, name);
			prep.setString(4, phone);
			prep.executeUpdate();

		}
		row.close();
		prep.close();
		return result;
	}

	public boolean signIn(String id, String password) throws SQLException {
		if (this.isOpened == false) {
			return false;
		}

		boolean result = false;
		String query = "select id password from workerInfo where id=? and password=?";
		PreparedStatement prep = this.connection.prepareStatement(query);
		prep.setString(1, id);
		prep.setString(2, password);
		ResultSet row = prep.executeQuery();
		if (row.next()) {
			result = true;
		}
		return result;
	}

	public void setSuccessPlusOne(int rid) throws SQLException {
		if(this.isOpened == false) {
			return;
		}
		String query = "update reservation set success = success + 1 where rid =?;";
		PreparedStatement prep = this.connection.prepareStatement(query);
		prep.setInt(1, rid);
		prep.executeUpdate();
		prep.close();		
	}


	public void writeProductList(String pid, String productName, String productionDate, String expirationDate, int rid, String wid) throws SQLException {
		if(this.isOpened == false) {
			return;
		}
		String query = "insert into r_productList (pid, productName, productionDate, expirationDate, rid, wid) values (?, ?, ?, ?, ?, ?);";
		PreparedStatement prep = this.connection.prepareStatement(query);
		prep.setString(1, pid);
		prep.setString(2, productName);
		prep.setString(3, productionDate);
		prep.setString(4, expirationDate);
		prep.setInt(5, rid);
		prep.setString(6, wid);
		prep.executeUpdate();
		prep.close();	
	}
}
