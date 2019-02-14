package com.msnet.view;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.msnet.MainApp;
import com.msnet.model.Product;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProductInfoDialogController implements Initializable {
	@FXML
	private AnchorPane pane;
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
	@FXML
	private TableColumn<Product, String> workerIDColumn;
	@FXML
	private JFXTextField searchTextField;
	@FXML
	private JFXButton searchButton;
	
	private Stage dialogStage;
	private ObservableList<Product> pList;
	private MainApp mainApp;
	public double xOffset = 0;
	public double yOffset = 0;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		productInfoTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductName()));
		idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPid()));
		productionDateColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getProductionDate()));
		expirationDateColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getExpirationDate()));
		workerIDColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getWid()));
		
		searchTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					handleSearch();
				}
			}
		});
		
		pane.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = event.getSceneX();
	            yOffset = event.getSceneY();					
			}
		});
		
		pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
	        	dialogStage.setX(event.getScreenX() - xOffset);
	        	dialogStage.setY(event.getScreenY() - yOffset);
	        }
	    });
	}

	@FXML
	public void handleSearch() {
		String search_str = searchTextField.getText();
		productInfoTableView.getItems().stream().filter(item -> (item.getPid()).contains(search_str))
				.findAny().ifPresent(item -> {
					productInfoTableView.getSelectionModel().select(item);
					productInfoTableView.scrollTo(item);
				});
	}

	public void setProduct(List<Product> product_list) {
		pList = FXCollections.observableArrayList(product_list);
		productInfoTableView.setItems(pList);
	}
	
	public ObservableList<Product> getPList(){
		return pList;
	}
	
	@FXML
	public void handleClose() {
		dialogStage.close();
	}

	public void setMain(MainApp mainApp) {
		this.mainApp = mainApp;
		
	}
	
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
		dialogStage.setResizable(false);
		this.dialogStage.initStyle(StageStyle.UNDECORATED); // 제목표시줄 안보이게 하기 위한 작업
	}
}
