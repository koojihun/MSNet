package com.msnet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import com.bitcoinClient.javabitcoindrpcclient.BitcoinJSONRPCClient;
import com.msnet.model.NBox;
import com.msnet.util.Bitcoind;
import com.msnet.util.HTTP;
import com.msnet.util.Settings;
import com.msnet.view.LoginViewController;
import com.msnet.view.ProductInfoDialogController;
import com.msnet.view.SystemOverviewController;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class MainApp extends Application {

	static public Stage primaryStage;
	static private BorderPane rootLayout;
	static public BitcoinJSONRPCClient bitcoinJSONRPClient;



	public static void main(String[] args) throws Exception {
		try {
			bitcoinJSONRPClient = new BitcoinJSONRPCClient("a", "12");
		} catch (MalformedURLException e) {
			System.err.println("BitcoinJSONRPCClient Constructor Error!!");
		}
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("MSNet");
		
		// showLoginView();
		initRootLayout();
		showSystemOverview();
	}

	public static void initRootLayout() {
		try {
			// fmxl ���Ͽ��� ���� ���̾ƿ��� �����´�.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// ���� ���̾ƿ��� �����ϴ� scene�� �����ش�.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void showSystemOverview() {
		try {
			// systemOverview�� fmxl ���Ͽ��� �����´�.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/SystemOverview.fxml"));
			AnchorPane systemOverview = (AnchorPane) loader.load();

			// systemOverview�� ���� ���̾ƿ��� ����� �����Ѵ�.
			rootLayout.setCenter(systemOverview);

			// ���� ���ø����̼��� ��Ʈ�ѷ��� �̿��� �� �ְ� �Ѵ�.
			SystemOverviewController controller = loader.getController();
			
			// Bitcoin daemon ���
			new Bitcoind(controller.getBitcoindTextArea()).start();

		} catch (IOException e) {
			e.printStackTrace();
		}
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

			primaryStage.setScene(loginScene);
			//primaryStage.initStyle(StageStyle.TRANSPARENT);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
