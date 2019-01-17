package com.msnet.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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
		return (JSONObject)jsonParser.parse(response.toString());
	}
	
	public static JSONObject send(String strUrl, String method, ArrayList<String> keys, ArrayList<String> vals) throws Exception {
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
}
