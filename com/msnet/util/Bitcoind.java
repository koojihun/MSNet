package com.msnet.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Bitcoind extends Thread {
	
	private static String fileName = "bincoind.exe";
	private static Process process_bincoind;

	public Bitcoind() {}

	public void run() {
		try {
			if (isBitcoindRunning()) {
				killBitcoind();
			}
			String filePath = "C:\\Users\\" + System.getProperty("user.name")
					+ "\\AppData\\Roaming\\Bitcoin\\bincoind.exe";
			
			process_bincoind = Runtime.getRuntime().exec(filePath);
		} catch (IOException e) {
			System.err.println("Bitcoind execute error");
			System.exit(1);
		} catch(Exception e) {
			System.err.println("Already bitcoind process kill error");
			System.exit(1);
		}
		
		new BitcoindWriter().start();
	}

	public static boolean isBitcoindRunning() throws Exception {
		Process p = Runtime.getRuntime().exec("TASKLIST");
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;		
		while ((line = reader.readLine()) != null) {
			if (line.contains(fileName)) {
				return true;
			}
		}
		return false;
	}

	public static void killBitcoind() throws Exception {
		Process p = Runtime.getRuntime().exec("TASKKILL /F /IM bincoind.exe");
		p.waitFor();
		System.out.println("running bitcoind is killed!");
	}

	public class BitcoindWriter extends Thread {
		
		public BitcoindWriter() {}

		public void run() {
			String input;
			String bucket = "";
			BufferedReader br = new BufferedReader(new InputStreamReader(process_bincoind.getInputStream()));
			try {
				while ((input = br.readLine()) != null) {
					/*
					bucket += (input + "\n");
					if (bucket.length() > 2048) {
						bincoind_screen.appendText(bucket);
						bucket = "";
					}
					*/
				}
			} catch (IOException e) {
				System.err.println("redirection error!!");
				System.exit(1);
			}
		}
	}
}
