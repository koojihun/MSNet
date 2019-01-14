package com.msnet;


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import com.msnet.util.Bitcoind;
import com.msnet.util.HTTP;
import com.msnet.util.Settings;
import com.msnet.view.LoginViewController;
import com.msnet.view.SystemOverviewController;
import com.bitcoinClient.javabitcoindrpcclient.BitcoinJSONRPCClient;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class MainApp extends Application {

	private Stage primaryStage;
	static public BitcoinJSONRPCClient bitcoinJSONRPClient;
	
	/*
	public void initRootLayout() {
		
		try {
			// fmxl 파일에서 상위 레이아웃을 가져온다.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane)loader.load();
			
			// 상위 레이아웃을 포함하는 scene을 보여준다.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
	}
	
	public void showSystemOverview() {
		
		AnchorPane systemOverview;
		
		try {
			// systemOverview를 fmxl 파일에서 가져온다.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/SystemOverview.fxml"));
			systemOverview = (AnchorPane) loader.load();
			
			// systemOverview를 상위 레이아웃의 가운데로 설정한다.
			rootLayout.setCenter(systemOverview);
			
			// 메인 애플리케이션이 컨트롤러를 이용할 수 있게 한다.
			SystemOverviewController controller = loader.getController();
			controller.setMainApp(this);
			
			// Bitcoin daemon 출력
			new Bitcoind(controller.getBitcoindTextArea()).start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
	}
	*/
	public static void main(String[] args) throws Exception {
		/*
		try {			
			bitcoinJSONRPClient = new BitcoinJSONRPCClient(Settings.getRpcUser(), Settings.getRpcPassword());
		} catch (MalformedURLException e) {
			System.err.println("BitcoinJSONRPCClient Constructor Error!!");
		}
		*/
		launch(args);
		ArrayList<String> keys = new ArrayList<>();
		ArrayList<String> vals = new ArrayList<>();
		keys.add("My Name Is");
		vals.add("Koo Ji Hoon");
		HTTP.send("http://localhost:8090/NewSystem_web/test_get.do", "get", keys, vals);
	}
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("MSNet");
		showLoginView();
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public void showLoginView() {
		AnchorPane loginPane;		
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/LoginView.fxml"));
			loginPane = (AnchorPane) loader.load();
			FadeTransition ft = new FadeTransition(Duration.millis(2500), loginPane);
			ft.setFromValue(0);
			ft.setToValue(1);
			ft.play();
			Scene loginScene = new Scene(loginPane);
			
			primaryStage.setScene(loginScene);
			primaryStage.initStyle(StageStyle.TRANSPARENT);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
