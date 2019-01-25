package com.msnet.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.msnet.MainApp;
import com.msnet.model.NDKey;
import com.msnet.model.PDB;
import com.msnet.model.Reservation;
import com.msnet.model.WDB;
import com.msnet.model.WorkerInfo;
import com.msnet.view.SystemOverviewController;
import com.sun.net.httpserver.*;

import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class HTTP {

	public static String makeStrMap(ArrayList<String> keys, ArrayList<String> vals) {
		String ret = "";
		for (int cnt = 0; cnt < keys.size(); cnt++) {
			String key = keys.get(cnt);
			String val = vals.get(cnt);
			ret += "&" + key + "=" + val;
		}
		return ret;
	}

	public static JSONObject getResponseBody(InputStream is) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));

		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null)
			response.append(inputLine);
		in.close();

		JSONParser jsonParser = new JSONParser();
		return (JSONObject) jsonParser.parse(response.toString());
	}

	public static JSONObject send(String strUrl, String method, ArrayList<String> keys, ArrayList<String> vals)
			throws Exception {
		JSONObject ret = null;
		if (method == "GET" || method == "get") {
			URL url = new URL(strUrl + "?" + makeStrMap(keys, vals));
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
			ret = getResponseBody(con.getInputStream());
			con.disconnect();
		} else if (method == "POST" || method == "post") {
			URL obj = new URL(strUrl);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			String dataToSend = makeStrMap(keys, vals);
			con.getOutputStream().write(dataToSend.getBytes());
			ret = getResponseBody(con.getInputStream());
			con.disconnect();
		}
		return ret;
	}

	public static void startHttpServer() {
		new bitcoinServer().start();
	}

	public static class bitcoinServer extends Thread {
		private static HttpServer httpServer;
		private static InetSocketAddress socket;

		public void run() {
			try {
				socket = new InetSocketAddress(9999);
				httpServer = HttpServer.create(socket, 0);
				httpServer.createContext("/", new Handler());
				httpServer.setExecutor(null);
				httpServer.start();
			} catch (Exception e) {
				System.err.println(e.toString());
				System.exit(1);
			}
		}

		public class Handler implements HttpHandler {
			@Override
			public void handle(HttpExchange httpExchange) throws IOException {
				System.out.println(httpExchange.getRequestURI());
				Map<String, String> query = queryToMap(httpExchange.getRequestURI().getQuery());
				String method = query.get("method");
				if (method.equals("send")) {
					String prodName = query.get("prodName");
					String productionDate = query.get("productionDate");
					String expirationDate = query.get("expirationDate");
					String pid = AES.decrypt(query.get("pid"));
					String bitcoinAddress = query.get("bitcoinAddress");
					
					System.out.println("prodName: " + prodName);
					System.out.println("productionDate: " + productionDate);
					System.out.println("expirationDate: " + expirationDate);
					System.out.println("pid: " + pid);
					System.out.println("bitcoinAddress: " + bitcoinAddress);
					
					Reservation sendReservation = PDB.getSendRList(prodName, productionDate, expirationDate,
							bitcoinAddress);
					
					if(sendReservation == null) {
						// Reservation list에 조건에 부합하는 reservation이 없을 때 경고창 띄움 
						JFXAlert alert = new JFXAlert((Stage) SystemOverviewController.getSystemOverview().getScene().getWindow());
						alert.initModality(Modality.APPLICATION_MODAL);
			            alert.setOverlayClose(true);
			            JFXDialogLayout layout = new JFXDialogLayout();
			            layout.setHeading(new Label("Not exists the reservation"));
			            layout.setBody(new Label("Please send a correct product"));
			            JFXButton closeButton = new JFXButton("ACCEPT");
			            closeButton.getStyleClass().add("dialog-accept");
			            closeButton.setOnAction(event -> alert.hideWithAnimation());
			            layout.setActions(closeButton);
			            alert.setContent(layout);
					} else {
						//Reservation list에 조건에 부합하는 reservation이 있을 때 send_to_address 실행
						PDB.sendProduct(sendReservation, bitcoinAddress, pid, prodName, productionDate, expirationDate);
					}
					
				} else if (method.equals("workerSignOut")) {
					String id = query.get("id");
					System.out.println("[" + id + "] Sign Out!!!!!!!!!!!!!!!!");
					WDB.setLogin(id, false);
				} else if (method.equals("workerSignIn")) {
					String id = query.get("id");
					String password = query.get("password");
					boolean isConfirm = WDB.confirmIDPW(id, password);
					String result;

					if (isConfirm) {
						WDB.setLogin(id, true);
						result = "true";
					} else {
						result = "false";
					}
					sendReturnValue(httpExchange, "result", result);
				} else if (method.equals("workerSignUp")) {
					String id = query.get("id");
					String password = query.get("password");
					String name = query.get("name");
					String phone = query.get("phone");

					boolean isExist = WDB.searchExist(id, password, name, phone);
					String result;
					if (isExist) {
						// 같은 아이디가 존재하면 false를 전달
						result = "false";
					} else {
						// 같은 아이디가 존재하지 않으면 true 전달
						result = "true";
					}
					// for return value.
					sendReturnValue(httpExchange, "result", result);
				}

			}
		}

		public void sendReturnValue(HttpExchange httpExchange, String key, String value) {
			JSONObject response = new JSONObject();
			response.put(key, value);
			try {
				httpExchange.sendResponseHeaders(200, response.toJSONString().getBytes().length);
				OutputStream os = httpExchange.getResponseBody();
				os.write(response.toJSONString().getBytes());
				os.close();
				httpExchange.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		// url 뒤 parameter들을 파싱 하기 위한 함수들. (ex. ip주소:port/?method=1&address=~~~&pid=~~)
		public Map<String, String> queryToMap(String query) {
			Map<String, String> result = new HashMap<String, String>();
			for (String param : query.split("&")) {
				String pair[] = param.split("=");
				if (pair.length > 1)
					result.put(pair[0], pair[1]);
				else
					result.put(pair[0], "");
			}
			return result;
		}

		public static void close() {
			httpServer.stop(0);
		}
	}
}
