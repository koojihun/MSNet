package com.msnet.view;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.json.simple.JSONObject;

import com.msnet.MainApp;
import com.msnet.util.HTTP;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginViewController implements Initializable {

	private double x = 0;
	private double y = 0;
	private String serverURL = "";

	@FXML
	private Button signUpButton;
	@FXML
	private TextField idTextField;
	@FXML
	private TextField passwordTextField;
	@FXML
	private Button enterButton;
	
	private MainApp mainApp;
	
	public LoginViewController() {
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// 참고사항 = 멤버 변수 객체들 생성 및 뷰와 연결후 initialize 함수 실행.
		String original = signUpButton.getStyle();
		String pressed = original + "-fx-text-fill: gray;";
		signUpButton.setOnMousePressed(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				signUpButton.setStyle(pressed);
			}
		});

		signUpButton.setOnMouseReleased(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				signUpButton.setStyle(original);
			}
		});

		signUpButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					Desktop.getDesktop().browse(new URI("http://166.104.126.42:8090/NewSystem/register.html"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@FXML
	void dragged(MouseEvent event) {
		Node node = (Node) event.getSource();
		Stage stage = (Stage) node.getScene().getWindow();

		stage.setX(event.getScreenX() - x);
		stage.setY(event.getScreenY() - y);
	}

	@FXML
	void pressed(MouseEvent event) {
		x = event.getSceneX();
		y = event.getSceneY();
	}

	@FXML
	private void handleEnter() {

		String id = idTextField.getText();
		String password = passwordTextField.getText();
		
		boolean result = false;
		
		ArrayList<String> key = new ArrayList<String>();
		ArrayList<String> val = new ArrayList<String>();

		key.add("id");
		val.add(id);
		key.add("password");
		val.add(password);
		key.add("device");
		val.add("m");
		
		try {
			JSONObject jsonResult = HTTP.send("http://166.104.126.42:8090/NewSystem/signin.do", "post", key, val);
			result = (boolean)jsonResult.get("result");
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(result) {
			mainApp.initRootLayout();
			mainApp.showSystemOverview();
		}
	}

	public void setMain(MainApp mainApp) {
		this.mainApp = mainApp;
		
	}
}
