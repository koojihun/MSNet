package com.msnet.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.sqlite.SQLiteConfig;

import com.msnet.model.Product;
import com.msnet.model.Reservation;
import com.msnet.model.Worker;

public class DB {
	private final String dbFileName = "C:\\Users\\" + Settings.getSysUsrName() + "\\AppData\\Roaming\\bitcoin\\msnetDB.db";
	
	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public DB() {}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	// DB Init Function. 
	// If there is no tables in database, make 4 tables.("completedReservation", "r_productList", "reservation", "workerInfo")
	@SuppressWarnings("resource")
	public void init() {
		Connection connection = null;
		PreparedStatement inquirePrep = null;
		PreparedStatement createPrep = null;
		ResultSet row = null;
		
		try {
			SQLiteConfig config = new SQLiteConfig();
			connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbFileName, config.toProperties());
			
			String prepQuery = "SELECT count(*) AS isExist FROM sqlite_master WHERE Name=?;";
			inquirePrep = connection.prepareStatement(prepQuery);
			
			// 1. completeReservation table creation.
			inquirePrep.setString(1, "completedReservation");
			row = inquirePrep.executeQuery();
			if (!row.getBoolean("isExist")) {
				String creationQeury = "CREATE TABLE `completedReservation` (" + 
						"`rid` INTEGER," + 
						"`time` TEXT," + 
						"`toAddress` TEXT," + 
						"`toCompany` TEXT," + 
						"`productName` TEXT," + 
						"`productionDate` TEXT," + 
						"`expirationDate` TEXT," + 
						"`quantity` INTEGER," + 
						"`success` INTEGER," + 
						"PRIMARY KEY(`rid`)" + 
						");";
				createPrep = connection.prepareStatement(creationQeury);
				createPrep.execute();
			}
			
			// 2. r_productList table creation.
			inquirePrep.setString(1, "r_productList");
			row = inquirePrep.executeQuery();
			if (!row.getBoolean("isExist")) {
				String creationQeury = "CREATE TABLE `r_productList` (" + 
						"`pid` TEXT," + 
						"`productName` TEXT," + 
						"`productionDate` TEXT," + 
						"`expirationDate` TEXT," + 
						"`rid` INTEGER," + 
						"`wid` TEXT," + 
						"PRIMARY KEY(`pid`)" + 
						");";
				createPrep = connection.prepareStatement(creationQeury);
				createPrep.execute();
			}
			
			// 3. reservation table creation.
			inquirePrep.setString(1, "reservation");
			row = inquirePrep.executeQuery();
			if (!row.getBoolean("isExist")) {
				String creationQeury = "CREATE TABLE `reservation` (" + 
						"`rid` INTEGER PRIMARY KEY AUTOINCREMENT," + 
						"`time`	TEXT," + 
						"`toAddress` TEXT," + 
						"`toCompany` TEXT," + 
						"`productName` TEXT," + 
						"`productionDate` TEXT," + 
						"`expirationDate` TEXT," + 
						"`quantity` INTEGER," + 
						"`success` INTEGER" + 
						");";
				createPrep = connection.prepareStatement(creationQeury);
				createPrep.execute();
			}
			
			// 4. workerInfo table creation.
			inquirePrep.setString(1, "workerInfo");
			row = inquirePrep.executeQuery();
			if (!row.getBoolean("isExist")) {
				String creationQeury = "CREATE TABLE `workerInfo` (" + 
						"`id` TEXT," + 
						"`password` TEXT," + 
						"`name`	TEXT," + 
						"`phone` TEXT," + 
						"PRIMARY KEY(`id`)" + 
						");";
				createPrep = connection.prepareStatement(creationQeury);
				createPrep.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inquirePrep != null) inquirePrep.close();
				if (createPrep != null) createPrep.close();
				if (row != null) row.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void readWorker() {
		Connection connection = null;
		PreparedStatement prep = null;
		ResultSet row = null;
		
		try {
			SQLiteConfig config = new SQLiteConfig();
			connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbFileName, config.toProperties());
			
			String query = "select * from workerinfo;";
			prep = connection.prepareStatement(query);
			row = prep.executeQuery();
			
			while (row.next())
				WDB.getWorkerList().add(new Worker(row.getString("id"), row.getString("name"), false));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (prep != null) prep.close();
				if (row != null) row.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void readReservation() {
		Connection connection = null;
		PreparedStatement prep = null;
		ResultSet row = null;
		
		try {
			SQLiteConfig config = new SQLiteConfig();
			connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbFileName, config.toProperties());
			
			String query = "select * from reservation;";
			prep = connection.prepareStatement(query);
			row = prep.executeQuery();
			
			while (row.next()) {
				ArrayList<Product> tmpList = readRProductList(row.getInt("rid"));
				Reservation r = new Reservation(row.getInt("rid"), row.getString("time"), row.getString("toAddress"), row.getString("toCompany"), row.getString("productName"),
						row.getString("productionDate"), row.getString("expirationDate"), row.getInt("quantity"), row.getInt("success"), tmpList);
				PDB.getRList().add(r);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (prep != null) prep.close();
				if (row != null) row.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// reservation table에 r을 추가하고, 새로 생성된 rid(autoincrement를 통해 자동생성)를 r.setRid()를 이용해 set함.
	public void writeReservation(Reservation r) {
		Connection connection = null;
		PreparedStatement prep = null;
		PreparedStatement prep2 = null;
		ResultSet row = null;
		
		try {
			SQLiteConfig config = new SQLiteConfig();
			connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbFileName, config.toProperties());
			String query = "insert into reservation (time, toAddress, toCompany, productName, productionDate, expirationDate, quantity, success) values (?, ?, ?, ?, ?, ?, ?, ?);";
			
			prep = connection.prepareStatement(query);
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
			prep2 = connection.prepareStatement(query);
			prep2.setString(1, r.getTime());
			row = prep2.executeQuery();
			
			r.setRid(row.getInt("rid"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (prep != null) prep.close();
				if (prep2 != null) prep2.close();
				if (row != null) row.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void readCompletedReservation() {
		Connection connection = null;
		PreparedStatement prep = null;
		ResultSet row = null;
		try {
			SQLiteConfig config = new SQLiteConfig();
			connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbFileName, config.toProperties());
			String query = "select * from completedReservation;";
			prep = connection.prepareStatement(query);
			row = prep.executeQuery();
			
			while(row.next()) {
				ArrayList<Product> tmpList = readRProductList(row.getInt("rid"));
				Reservation r = new Reservation(row.getInt("rid"), row.getString("time"), row.getString("toAddress"), row.getString("toCompany"), row.getString("productName"),
						row.getString("productionDate"), row.getString("expirationDate"), row.getInt("quantity"), row.getInt("success"), tmpList);
				PDB.getComplitedRList().add(r);			
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (prep != null) prep.close();
				if (row != null) row.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// reservation을 completedReservation table에 추가하고 
	public void writeCompletedReservation(Reservation r) {
		Connection connection = null;
		PreparedStatement prep = null;
		ResultSet row = null;
		try {
			SQLiteConfig config = new SQLiteConfig();
			connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbFileName, config.toProperties());
			
			String query = "insert into completedReservation (rid, time, toAddress, toCompany, productName, productionDate, expirationDate, quantity, success) values (?, ?, ?, ?, ?, ?, ?, ?, ?);";
			prep = connection.prepareStatement(query);
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
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (prep != null) prep.close();
				if (row != null) row.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void deleteReservation(Reservation r) {
		Connection connection = null;
		PreparedStatement prep = null;
		ResultSet row = null;
		try {
			SQLiteConfig config = new SQLiteConfig();
			connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbFileName, config.toProperties());
			
			String query = "delete from reservation where rid=?;";
			prep = connection.prepareStatement(query);
			int rid = r.getRid();
			prep.setString(1, String.valueOf(rid));
			prep.executeUpdate();
			prep.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (prep != null) prep.close();
				if (row != null) row.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<Product> readRProductList(int rid) {
		Connection connection = null;
		PreparedStatement prep = null;
		ResultSet row = null;
		
		ArrayList<Product> pList = new ArrayList<Product>();
		
		try {
			SQLiteConfig config = new SQLiteConfig();
			connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbFileName, config.toProperties());
			
			String query = "select * from r_productList where rid=?;";
			prep = connection.prepareStatement(query);
			prep.setInt(1, rid);
			row = prep.executeQuery();
			
			while (row.next()) {
				Product p = new Product(row.getString("productionDate"), row.getString("expirationDate"), row.getString("productName"), 
						row.getString("pid"), row.getString("wid"));
				pList.add(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (prep != null) prep.close();
				if (row != null) row.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return pList;
	}

	// DB에 같은 id가 존재하면 false 반환, 존재하지 않으면 true 반환하고 DB에 가입 정보 저장
	@SuppressWarnings("resource")
	public boolean signUp(String id, String password, String name, String phone) {
		Connection connection = null;
		PreparedStatement prep = null;
		ResultSet row = null;
		
		boolean result = true;
		
		try {
			SQLiteConfig config = new SQLiteConfig();
			connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbFileName, config.toProperties());
			
			String query = "select * from workerInfo where id = ?;";
			prep = connection.prepareStatement(query);
			prep.setString(1, id);
			row = prep.executeQuery();

			if (row.next()) {
				result = false; // 같은 id가 존재하면 false 반환
			}

			// id가 존재하지 않을 때는 worker의 정보를 workerinfo에 insert
			if (result) {
				query = "insert into workerInfo (id, password, name, phone) values (?, ?, ?, ?);";
				prep = connection.prepareStatement(query);
				prep.setString(1, id);
				prep.setString(2, password);
				prep.setString(3, name);
				prep.setString(4, phone);
				prep.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (prep != null) prep.close();
				if (row != null) row.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}

	public boolean signIn(String id, String password) {
		Connection connection = null;
		PreparedStatement prep = null;
		ResultSet row = null;
		
		boolean result = false;
		
		try {
			SQLiteConfig config = new SQLiteConfig();
			connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbFileName, config.toProperties());
			
			String query = "select id, password from workerInfo where id=? and password=?";
			prep = connection.prepareStatement(query);
			prep.setString(1, id);
			prep.setString(2, password);
			row = prep.executeQuery();
			if (row.next())
				result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (prep != null) prep.close();
				if (row != null) row.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}

	public void setSuccessPlusOne(int rid) {
		Connection connection = null;
		PreparedStatement prep = null;
		ResultSet row = null;
		
		try {
			SQLiteConfig config = new SQLiteConfig();
			connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbFileName, config.toProperties());
			
			String query = "update reservation set success = success + 1 where rid =?;";
			prep = connection.prepareStatement(query);
			prep.setInt(1, rid);
			prep.executeUpdate();	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (prep != null) prep.close();
				if (row != null) row.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void writeProductList(String pid, String productName, String productionDate, String expirationDate, int rid, String wid) {
		Connection connection = null;
		PreparedStatement prep = null;
		ResultSet row = null;
		
		try {
			SQLiteConfig config = new SQLiteConfig();
			connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbFileName, config.toProperties());
			
			String query = "insert into r_productList (pid, productName, productionDate, expirationDate, rid, wid) values (?, ?, ?, ?, ?, ?);";
			prep = connection.prepareStatement(query);
			prep.setString(1, pid);
			prep.setString(2, productName);
			prep.setString(3, productionDate);
			prep.setString(4, expirationDate);
			prep.setInt(5, rid);
			prep.setString(6, wid);
			prep.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (prep != null) prep.close();
				if (row != null) row.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void deleteWorker(Worker w) {
		Connection connection = null;
		PreparedStatement prep = null;
		ResultSet row = null;
		
		try {
			SQLiteConfig config = new SQLiteConfig();
			connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbFileName, config.toProperties());
			
			String query = "delete from workerInfo where id=?;";
			prep = connection.prepareStatement(query);
			String id = w.getID();
			prep.setString(1, id);
			prep.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (prep != null) prep.close();
				if (row != null) row.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
