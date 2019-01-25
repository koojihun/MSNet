package com.msnet.view;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.json.simple.JSONObject;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.msnet.MainApp;
import com.msnet.util.HTTP;
import com.msnet.util.Settings;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
			result = (boolean)jsonResult.get("result");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(result) {
			ProgressDialog.show(mainApp.getPrimaryStage(), true);
			new Thread() {
				public void run() {
					new Settings(id, password);
					// After Setting is finished, Progress Dialog should be closed and then primary stage should be shown.
					Platform.runLater(() -> {
						ProgressDialog.close();
					});
				}
			}.start();
			
			mainApp.showSystemOverview();
		} else {
			JFXAlert alert = new JFXAlert((Stage) loginPane.getScene().getWindow());
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setOverlayClose(true);
            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setHeading(new Label("Login ERROR"));
            layout.setBody(new Label("Check Your ID & Password again."));
            JFXButton closeButton = new JFXButton("ACCEPT");
            closeButton.getStyleClass().add("dialog-accept");
            closeButton.setOnAction(event -> alert.close());
            layout.setActions(closeButton);
            alert.setContent(layout);
            alert.show();
		}
	}
}
