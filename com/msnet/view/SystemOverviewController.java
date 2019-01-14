package com.msnet.view;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.msnet.MainApp;
import com.msnet.model.Product;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

public class SystemOverviewController implements Initializable {
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

	ObservableList<Product> inventoryList = FXCollections
			.observableArrayList(new Product("20181123T1212", "20201123T1212", "¸Àµ¿»ê", 100));

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
			System.out.println(String.valueOf(map.get("prodName")));
			System.out.println(String.valueOf(map.get("PID")));
			System.out.println(String.valueOf(map.get("production date")));
			System.out.println(String.valueOf(map.get("expiration date")));

		}
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
		
		
		
		inventoryList = FXCollections.observableArrayList();
		for (Map product : products) {
			inventoryList.add(new Product(String.valueOf(product.get("production date")),
					String.valueOf(product.get("expiration date")), String.valueOf(product.get("prodName")), 100));
		}
		inventoryStatusTableView.setItems(inventoryList);
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		productionDateColumn.setCellValueFactory(cellData -> cellData.getValue().productionDateProperty());
		expirationDateColumn.setCellValueFactory(cellData -> cellData.getValue().expirationDateProperty());
		productNameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
		quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
		inventoryStatusTableView.setItems(inventoryList);
	}

}
