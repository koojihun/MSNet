package com.msnet.view;

import java.io.IOException;

import com.msnet.MainApp;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProgressDialog {

	private static Stage progressStage;
	private static Stage primaryStage;
	
	public ProgressDialog() {}
	
	public static void show(Stage mainStage, boolean isHide) {
		primaryStage = mainStage;
		if (isHide)
			primaryStage.hide();
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/ProgressDialog.fxml"));
		AnchorPane progressPane = null;
		
		try {
			progressPane = (AnchorPane) loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Scene scene = new Scene(progressPane);
		scene.setFill(Color.TRANSPARENT);
		
		progressStage = new Stage();
		progressStage.setTitle("Loading");
		progressStage.initModality(Modality.WINDOW_MODAL);
		progressStage.initStyle(StageStyle.TRANSPARENT);
		progressStage.initOwner(primaryStage);
		
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		progressStage.setX((screenBounds.getWidth() - 200) / 2);
		progressStage.setY((screenBounds.getHeight() - 200) / 2);
				
		progressStage.setScene(scene);
		progressStage.toFront();
		progressStage.show();
	}
	
	public static void close() {
		progressStage.close();
		primaryStage.toFront();
		primaryStage.show();
	}
}
