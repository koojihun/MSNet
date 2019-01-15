package com.msnet.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.msnet.MainApp;
import com.msnet.model.NBox;
import com.msnet.model.NDBox;
import com.msnet.model.Product;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProductInfoDialogController implements Initializable {
	@FXML
	private TableView<Product> productInfoTableView;
	@FXML
	private TableColumn<Product, String> nameColumn;
	@FXML
	private TableColumn<Product, String> idColumn;
	@FXML
	private TableColumn<Product, String> productionDateColumn;
	@FXML
	private TableColumn<Product, String> expirationDateColumn;
	
	
	private Stage dialogStage;
	private NBox nBox;
	private NDBox ndBox;
	private boolean okClicked = false;
	private MainApp mainApp;
	
	ObservableList<Product> pList;
	
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	public void setProduct(NBox nBox) {
		this.nBox = nBox;
		pList = FXCollections.observableArrayList();
		
		for(Product product : nBox.getProductList()) {
			pList.add(product);
		}
		productInfoTableView.setItems(pList);
	}
	
	public void setProduct(NDBox ndBox) {
		this.ndBox = ndBox;
		pList = FXCollections.observableArrayList();
		
		for(Product product : ndBox.getProductList()) {
			pList.add(product);
		}
		productInfoTableView.setItems(pList);		
	}
	
	public static void showProductInfoDialog(NBox nBox) {
		
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
			
			//다이얼로그를 보여주고 사용자가 닫을 때까지 기다린다.
			dialogStage.showAndWait();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void showProductInfoDialog(NDBox ndBox) {
		
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
			
			//다이얼로그를 보여주고 사용자가 닫을 때까지 기다린다.
			dialogStage.showAndWait();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
		idColumn.setCellValueFactory(cellData -> cellData.getValue().pidProperty());
		productionDateColumn.setCellValueFactory(cellData -> cellData.getValue().productionDateProperty());
		expirationDateColumn.setCellValueFactory(cellDate -> cellDate.getValue().expirationDateProperty());
		productInfoTableView.setItems(pList);
		
	}
	
	
}
