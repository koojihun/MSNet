package com.msnet;

import java.io.IOException;

import com.bitcoinClient.javabitcoindrpcclient.BitcoinJSONRPCClient;
import com.msnet.util.AES;
import com.msnet.util.Bitcoind;
import com.msnet.util.HTTP;
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
		HTTP.bitcoinServer.close();
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
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/SystemOverview.fxml"));
			AnchorPane systemOverview = (AnchorPane) loader.load();

			Scene scene = new Scene(systemOverview);
			primaryStage.setScene(scene);
			primaryStage.show();;

			SystemOverviewController controller = loader.getController();
			controller.setMainApp(this);
		
			bitcoinJSONRPClient = new BitcoinJSONRPCClient(Settings.getId(), Settings.getPassword());
			Settings.makeAndSendBitcoinAddress();
			
			HTTP.startHttpServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
}
