package com.msnet.util;

import java.io.*;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.bitcoinClient.javabitcoindrpcclient.BitcoinJSONRPCClient;
import com.msnet.MainApp;

public class Settings {

	private static String id;
	private static String password;
	private static String bitcoinAddress;
	private static String defaultKey;
	private static String sysUsrName;

	public Settings(String id, String password) {
		Settings.id = id;
		Settings.password = password;
		Settings.sysUsrName = System.getProperty("user.name");
		////////////////////////////////////////////////////////////////
		// Bitcoin daemon.
		if (!isThereBitcoind())
			copyBitcoind();
		////////////////////////////////////////////////////////////////
		// Bitcoin conf file.
		if (!isThereConfFile())
			makeConfFile();
		////////////////////////////////////////////////////////////////
		// License.txt file.
		if (!isThereLicense())
			copyLicense();
		////////////////////////////////////////////////////////////////
		// Check bitcoin address.
		readBitcoinConfFile();
		////////////////////////////////////////////////////////////////
		// Set key for encrypt.
		AES.setKey();
		////////////////////////////////////////////////////////////////
		HTTP.startHttpServer();
		////////////////////////////////////////////////////////////////
		Thread t = new Bitcoind();
		ThreadGroup.addThread(t);
		////////////////////////////////////////////////////////////////
		try {
			MainApp.bitcoinJSONRPClient = new BitcoinJSONRPCClient(id, password);
			sendMyBitcoinAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		////////////////////////////////////////////////////////////////
		// DB init.
		DB db = new DB();
		db.init();
	}

	private boolean isThereConfFile() {
		File confFile = new File("C:\\Users\\" + sysUsrName + "\\AppData\\Roaming\\Bitcoin\\bitcoin.conf");
		if (confFile.exists())
			return true;
		return false;
	}

	private boolean isThereBitcoind() {
		///////////////////////////////////////////////////////////
		File bitcoin_directory = new File("C:\\Users\\" + sysUsrName + "\\AppData\\Roaming\\Bitcoin");
		if (!bitcoin_directory.exists()) {
			bitcoin_directory.mkdir();
		}

		File bincoind_exe = new File("C:\\Users\\" + sysUsrName + "\\AppData\\Roaming\\Bitcoin\\bincoind.exe");
		if (bincoind_exe.exists())
			return true;
		else
			return false;
	}

	private boolean isThereLicense() {
		File license_txt = new File("C:\\Users\\" + sysUsrName + "\\AppData\\Roaming\\Bitcoin\\license.txt");
		if (license_txt.exists())
			return true;
		else
			return false;
	}

	private void copyLicense() {
		try {
			File bincoind = new File("res/license.txt");
			FileInputStream is = new FileInputStream(bincoind);

			// sets the output stream to a system folder
			OutputStream os = new FileOutputStream(
					"C:\\Users\\" + sysUsrName + "\\AppData\\Roaming\\Bitcoin\\license.txt");
			// 2048 here is just my preference
			byte[] b = new byte[4096];
			int length;
			while ((length = is.read(b)) != -1) {
				os.write(b, 0, length);
			}
			is.close();
			os.close();
		} catch (Exception e) {
			System.err.println("Error : Copy license.txt into System");
			System.exit(1);
		}
	}

	private void copyBitcoind() {
		try {
			File bincoind = new File("res/bincoind.exe");
			FileInputStream is = new FileInputStream(bincoind);

			// sets the output stream to a system folder
			OutputStream os = new FileOutputStream(
					"C:\\Users\\" + sysUsrName + "\\AppData\\Roaming\\Bitcoin\\bincoind.exe");

			byte[] b = new byte[4096];
			int length;
			while ((length = is.read(b)) != -1) {
				os.write(b, 0, length);
			}
			is.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error : Copy Bitcoind.exe into System");
			System.exit(1);
		}
	}

	private void makeConfFile() {
		String fileName = "C:\\Users\\" + sysUsrName + "\\AppData\\Roaming\\Bitcoin\\bitcoin.conf";
		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, false));

			fw.write("rpcuser=" + id);
			fw.newLine();
			fw.write("rpcpassword=" + password);
			fw.newLine();
			fw.write("server=1");
			fw.newLine();
			fw.write("msnet=1");
			fw.newLine();
			fw.write("printtoconsole=1");
			fw.newLine();
			fw.write("addnode=166.104.126.42");
			fw.newLine();
			
			fw.flush();
			fw.close();
		} catch (Exception e) {
			System.err.println("Error : Making bitcoin.conf file error.");
		}
	}

	public void readBitcoinConfFile() {
		try {
			////////////////////////////////////////////////////////////////
			// bitcoin.conf file //
			String bitcoin_conf = "C:\\Users\\" + sysUsrName + "\\AppData\\Roaming\\Bitcoin\\bitcoin.conf";
			BufferedReader in = new BufferedReader(new FileReader(bitcoin_conf));
			String s;
			while ((s = in.readLine()) != null) {
				if (s.contains("bitcoinAddress")) {
					int equalIndex = s.indexOf('=');
					Settings.bitcoinAddress = s.substring(equalIndex + 1);
				} else if (s.contains("defaultKey")) {
					int equalIndex = s.indexOf('=');
					Settings.defaultKey = s.substring(equalIndex + 1);
				}
			}
			in.close();
		} catch (IOException e) {
			System.err.println("Error : From reading bitcoinAddress from bitcoin.conf file.");
			System.exit(1);
		}
	}

	public static void sendMyBitcoinAddress() throws Exception {
		if (defaultKey == null || bitcoinAddress == null) {
			while (defaultKey == null ||bitcoinAddress == null) {
				try {
					JSONObject result = MainApp.bitcoinJSONRPClient.dump_default_key();
					bitcoinAddress = (String) result.get("bitcoinAddress");
					defaultKey = (String) result.get("defaultKey");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			String filePath = "C:\\Users\\" + Settings.getSysUsrName() + "\\AppData\\Roaming\\Bitcoin\\bitcoin.conf";
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true));
				bw.write("bitcoinAddress=" + Settings.getBitcoinAddress());
				bw.newLine();
				bw.write("defaultKey=" + Settings.getDefaultKey());
				bw.newLine();
				bw.close();
				/////////////////////////////////////////////////////
				ArrayList<String> keys = new ArrayList<>();
				ArrayList<String> vals = new ArrayList<>();
				keys.add("id");
				keys.add("bitcoinAddress");
				keys.add("defaultKey");
				vals.add(Settings.getId());
				vals.add(Settings.getBitcoinAddress());
				vals.add(Settings.getDefaultKey());
				HTTP.send("http://www.godqr.com/reportAddress.do.pc", "GET", keys, vals);
				/////////////////////////////////////////////////////
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String getId() {
		return id;
	}

	public static String getPassword() {
		return password;
	}

	public static String getSysUsrName() {
		return sysUsrName;
	}

	public static String getBitcoinAddress() {
		return bitcoinAddress;
	}
	
	public static String getDefaultKey() {
		return defaultKey;
	}
}