package com.msnet.view;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.json.simple.JSONObject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.msnet.MainApp;
import com.msnet.util.Alert;
import com.msnet.util.HTTP;
import com.msnet.util.Settings;
import com.msnet.util.ThreadGroup;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class LoginViewController implements Initializable{
	private final String serverURL = "http://166.104.126.42:8090/NewSystem/";
	@FXML
	private JFXTextField idField;
	@FXML
	private JFXPasswordField passwordField;
	@FXML
	private JFXButton signUpButton;
	@FXML
	private JFXButton enterButton;
	
	private AnchorPane loginPane;
	private MainApp mainApp;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		passwordField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER) {
					handleEnter();
				}
			}			
		});
	}
	
	public void setPane(Parent p) {
		loginPane = (AnchorPane) p;
	}
	
	public void setMain(MainApp mainApp) {
		this.mainApp = mainApp;		
	}
	
	@FXML
	public void handleSignUp() {
		try {
			Desktop.getDesktop().browse(new URI("http://166.104.126.42:8090/NewSystem/register.html"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	@FXML
	public void handleEnter() {
		String id = idField.getText();
		String password = passwordField.getText();
		
		boolean result = false;
		
		ArrayList<String> key = new ArrayList<String>();
		ArrayList<String> val = new ArrayList<String>();
		
		key.add("id"); key.add("password"); key.add("device");
		val.add(id);   val.add(password);   val.add("p");
		
		try {
			JSONObject jsonResult = HTTP.send(serverURL + "signin.do", "post", key, val);
			result = (boolean) jsonResult.get("result");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(result) {
			ProgressDialog.show(mainApp.getPrimaryStage(), true);
			Thread t = new Thread() {
				public void run() {
					new Settings(id, password);
					Platform.runLater(() -> {
						ProgressDialog.close();
					});
				}
			}; 
			ThreadGroup.addThread(t);
			mainApp.showSystemOverview();
		} else {
			String head = "Login ERROR";
			String body = "Check Your ID & Password again.";
			new Alert(loginPane, head, body);
		}
	}
}
