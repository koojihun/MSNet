package com.msnet.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.msnet.model.WDB;
import com.sun.net.httpserver.*;

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
		
		public static void closeSocket() {
			httpServer.stop(0);
			System.out.println("Bitcoin Server is closed!");
		}
		
		public class Handler implements HttpHandler {
			@Override
			public void handle(HttpExchange httpExchange) throws IOException {
				Map<String, String> query = queryToMap(httpExchange.getRequestURI().getQuery());
				String method = query.get("method");
				
				if (method.equals("send")) {
					String prodName = query.get("prodName");
					String productionDate = query.get("productionDate");
					String expirationDate = query.get("expirationDate");
					String pid = AES.decrypt(query.get("pid"));
					String bitcoinAddress = query.get("bitcoinAddress");
					String wid = query.get("wid");
					sendReturnValue(httpExchange, "result", PDB.sendProduct(bitcoinAddress, pid, prodName, productionDate, expirationDate, wid));
					System.out.println(prodName+ "/" + productionDate + "/" + pid + "/" + wid);
					if (!WDB.isLogin(wid))
						WDB.setIsLogin(wid, true);
				} else if (method.equals("workerSignOut")) {
					String id = query.get("id");
					WDB.setIsLogin(id, false);
					sendReturnValue(httpExchange, "result", "true");
				} else if (method.equals("workerSignIn")) {
					String id = query.get("id");
					String password = query.get("password");
					boolean result;					
					DataReader dr = new DataReader("C:\\Users\\triz\\AppData\\Roaming\\msnetDB.db");
					dr.open();					
					try {
						result = dr.signIn(id, password);
						sendReturnValue(httpExchange, "result", String.valueOf(result));
						// id, password에 해당하는 workerinfo가 있을 때는 WDB.workerList의 isLogin을 true로 설정
						if(result) {
							WDB.setIsLogin(id, true);
						}						
					} catch (SQLException e) {
						System.out.println("로그인 과정에서 문제 발생.");
						e.printStackTrace();
					}
					dr.close();
				} else if (method.equals("workerSignUp")) {
					String id = query.get("id");
					String password = query.get("password");
					String name = query.get("name");
					String phone = query.get("phone");
					boolean result;					
					DataReader dr = new DataReader("C:\\Users\\triz\\AppData\\Roaming\\msnetDB.db");
					dr.open();					
					try {
						result = dr.signUp(id, password, name, phone);
						if (result) {
							WDB.insert(id, password, name, phone);
						}
						sendReturnValue(httpExchange, "result", String.valueOf(result));
					} catch (SQLException e) {
						System.out.println("회원가입 과정에서 문제 발생.");
						e.printStackTrace();
					}
					dr.close();				
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
