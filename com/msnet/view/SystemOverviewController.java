package com.msnet.view;

import java.util.List;
import java.util.Map;

import com.msnet.MainApp;
import com.msnet.model.Product;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

public class SystemOverviewController {
	@FXML
	private TextArea bitcoindTextArea;
	@FXML
	private TableView<Product> inventoryStatusTableView;
	@FXML
	private TableColumn<Product, String> productionDateColumn;
	@FXML
	private TableColumn<Product, String> expirationDateColumn;
	@FXML
	private TableColumn<Product, String> productNameColumn;
	@FXML
	private TableColumn<Product, Integer> quantityColumn;
	@FXML
	private Button inventoryStatusButton;
	
	private MainApp mainApp;

	ObservableList<Product> inventoryList = FXCollections.observableArrayList();

	public SystemOverviewController() {
		// this.bitcoindTextArea.setEditable(false);
		
	}

	public TextArea getBitcoindTextArea() {
		return bitcoindTextArea;
	}

	@FXML
	public void handleTest() {
		List<Map> maps = MainApp.bitcoinJSONRPClient.get_current_products();

		for (Map map : maps) {

			System.out.println(String.valueOf(map.get("PID")));
			System.out.println(String.valueOf(map.get("production date")));
			System.out.println(String.valueOf(map.get("expiration date")));

		}
	}

	@FXML
	private void initializeInventoryStatus() {
		productionDateColumn.setCellValueFactory(cellData -> cellData.getValue().productionDateProperty());
		expirationDateColumn.setCellValueFactory(cellData -> cellData.getValue().expirationDateProperty());
		productNameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
		//quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
	}

	@FXML
	
	private void showInventoryStatus() {
		List<Map> products = MainApp.bitcoinJSONRPClient.get_current_products();

		for (Map product : products) {
			
		}
	}

	@FXML
	public void handleInventoryStatus() {
		List<Map> products = MainApp.bitcoinJSONRPClient.get_current_products();
		for (Map product : products) {
			inventoryStatusTableView.getItems().add(new Product(String.valueOf(product.get("production date")), String.valueOf(product.get("expiration date")), "»õ¿ì±ø", 100));

		}
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

}
