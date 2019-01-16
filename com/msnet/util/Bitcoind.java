package com.msnet.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class Bitcoind extends Thread {
	
	private static String fileName = "bincoind.exe";
	private static Process process_bincoind;
	private TextArea bincoind_screen;

	public Bitcoind(TextArea bincoind_screen) {
		this.bincoind_screen = bincoind_screen;
	}

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
		
		BitcoindWriter writer = new BitcoindWriter(new BufferedReader(new InputStreamReader(process_bincoind.getInputStream())));
		writer.start();
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
		private BufferedReader bufferedReader;

		public BitcoindWriter(BufferedReader bufferedReader) {
			this.bufferedReader = bufferedReader;
		}

		public void run() {
			String input;
			try {
				while ((input = bufferedReader.readLine()) != null) {
					bincoind_screen.appendText(input + "\n");
				}
			} catch (IOException e) {
				System.err.println("redirection error!!");
				System.exit(1);
			}
		}

	}
}
