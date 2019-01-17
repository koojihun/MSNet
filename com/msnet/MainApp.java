package com.msnet;

import java.io.IOException;

import com.bitcoinClient.javabitcoindrpcclient.BitcoinJSONRPCClient;
import com.msnet.util.Bitcoind;
import com.msnet.util.Settings;
import com.msnet.view.LoginViewController;
import com.msnet.view.SystemOverviewController;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainApp extends Application {

	public static final double WIDTH = 1300;
	public static final double HEIGHT = 900;
	public Stage primaryStage;
	public static BitcoinJSONRPCClient bitcoinJSONRPClient;

	public static void main(String[] args) throws Exception {		
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("MSNet");
		showLoginView();
	}

	@Override
	public void stop() throws Exception {
		Bitcoind.killBitcoind();
	}

	public void showLoginView() {

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/LoginView.fxml"));
			AnchorPane loginPane = (AnchorPane) loader.load();

			FadeTransition ft = new FadeTransition(Duration.millis(2500), loginPane);
			ft.setFromValue(0);
			ft.setToValue(1);
			ft.play();

			Scene loginScene = new Scene(loginPane);
			LoginViewController controller = loader.getController();
			controller.setMain(this);

			primaryStage.setScene(loginScene);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showSystemOverview() throws Exception {
		try {
			// systemOverview를 fmxl 파일에서 가져온다.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/SystemOverview.fxml"));
			AnchorPane systemOverview = (AnchorPane) loader.load();

			// systemOverview를 상위 레이아웃의 가운데로 설정한다.
			// 상위 레이아웃을 포함하는 scene을 보여준다.
			Scene scene = new Scene(systemOverview);
			primaryStage.setScene(scene);
			primaryStage.show();;

			// 메인 애플리케이션이 컨트롤러를 이용할 수 있게 한다.
			SystemOverviewController controller = loader.getController();
			controller.setMainApp(this);
		
			bitcoinJSONRPClient = new BitcoinJSONRPCClient(Settings.getId(), Settings.getPassword());
			Settings.makeAndSendBitcoinAddress();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
}
