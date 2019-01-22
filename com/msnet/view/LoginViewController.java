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
import com.msnet.util.HTTP;
import com.msnet.util.Settings;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
			result = (boolean)jsonResult.get("result");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(result) {
			new Settings(id, password);
			try {
				mainApp.showSystemOverview();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Login Error");
			alert.setHeaderText("Login Error");
			alert.setContentText("ID, PASSWORD CHECK AGAIN PLEASE.");
			alert.showAndWait();
		}
	}
}
