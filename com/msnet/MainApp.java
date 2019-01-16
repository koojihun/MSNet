package com.msnet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import com.bitcoinClient.javabitcoindrpcclient.BitcoinJSONRPCClient;
import com.msnet.model.NBox;
import com.msnet.model.NDBox;
import com.msnet.util.Bitcoind;
import com.msnet.util.HTTP;
import com.msnet.util.Settings;
import com.msnet.view.LoginViewController;
import com.msnet.view.ProductInfoDialogController;
import com.msnet.view.SystemOverviewController;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
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

		showLoginView();
		// initRootLayout();
		// showSystemOverview();
	}

	public void initRootLayout() {
		try {
			// fmxl 파일에서 상위 레이아웃을 가져온다.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// 상위 레이아웃을 포함하는 scene을 보여준다.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showSystemOverview() {
		try {
			// systemOverview를 fmxl 파일에서 가져온다.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/SystemOverview.fxml"));
			AnchorPane systemOverview = (AnchorPane) loader.load();

			// systemOverview를 상위 레이아웃의 가운데로 설정한다.
			rootLayout.setCenter(systemOverview);

			// 메인 애플리케이션이 컨트롤러를 이용할 수 있게 한다.
			SystemOverviewController controller = loader.getController();

			controller.setMainApp(this);

			// NDBox를 더블 클릭하면 Product 목록이 나옴
			controller.getInventoryStatusTableView().setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (event.getClickCount() >= 2) {
						NDBox selectedNDBox = controller.getInventoryStatusTableView().getSelectionModel()
								.getSelectedItem();
						showProductInfoDialog(selectedNDBox);
					}
				}
			});

			// NBox를 더블 클릭하면 Product 목록이 나옴
			controller.getTotal_InventoryStatusTableView().setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (event.getClickCount() >= 2) {
						NBox selectedNBox = controller.getTotal_InventoryStatusTableView().getSelectionModel().getSelectedItem();
						showProductInfoDialog(selectedNBox);
					}
				}
			});

			// Bitcoin daemon 출력
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
			LoginViewController controller = loader.getController();
			controller.setMain(this);

			primaryStage.setScene(loginScene);
			// primaryStage.initStyle(StageStyle.TRANSPARENT);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showProductInfoDialog(NDBox ndBox) {

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/ProductInfoDialog.fxml"));
			AnchorPane productInfoPane = (AnchorPane) loader.load();

			// 다이얼로그 스테이지를 만든다.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Product Info");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(MainApp.primaryStage);
			Scene scene = new Scene(productInfoPane);
			dialogStage.setScene(scene);

			// product를 컨트롤러에 설정한다.
			ProductInfoDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setProduct(ndBox);

			// 다이얼로그를 보여주고 사용자가 닫을 때까지 기다린다.
			dialogStage.showAndWait();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void showProductInfoDialog(NBox nBox) {

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/ProductInfoDialog.fxml"));
			AnchorPane productInfoPane = (AnchorPane) loader.load();

			// 다이얼로그 스테이지를 만든다.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Product Info");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(MainApp.primaryStage);
			Scene scene = new Scene(productInfoPane);
			dialogStage.setScene(scene);
			// product를 컨트롤러에 설정한다.
			ProductInfoDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setProduct(nBox);
			// 다이얼로그를 보여주고 사용자가 닫을 때까지 기다린다.
			dialogStage.showAndWait();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
