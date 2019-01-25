package com.msnet;

import java.io.IOException;

import com.bitcoinClient.javabitcoindrpcclient.BitcoinJSONRPCClient;
import com.msnet.util.AES;
import com.msnet.util.Bitcoind;
import com.msnet.util.HTTP;
import com.msnet.view.LoginViewController;
import com.msnet.view.SystemOverviewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

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
		HTTP.bitcoinServer.closeSocket();
		HTTP.bitcoinServer.close();
	}

	public void showLoginView() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/LoginView.fxml"));
			AnchorPane loginPane = (AnchorPane) loader.load();

			Scene loginScene = new Scene(loginPane);
			LoginViewController controller = loader.getController();
			controller.setPane(loginPane);
			controller.setMain(this);

			primaryStage.setScene(loginScene);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showSystemOverview() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/SystemOverview.fxml"));
			AnchorPane systemOverview = (AnchorPane) loader.load();

			Scene scene = new Scene(systemOverview);
			centerStage(primaryStage, WIDTH, HEIGHT);
			primaryStage.setScene(scene);

			SystemOverviewController controller = loader.getController();
			controller.setPane(systemOverview);
			controller.setMainApp(this);						
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	private void centerStage(Stage stage, double width, double height) {
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		stage.setX((screenBounds.getWidth() - width) / 2);
		stage.setY((screenBounds.getHeight() - height) / 2);
	}
}
