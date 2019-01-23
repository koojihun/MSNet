package com.msnet.view;

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
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
	@FXML
	private TextField searchTextField;
	@FXML
	private Button searchButton;

	private Stage dialogStage;
	private NBox nBox;
	private NDBox ndBox;
	private ArrayList<Product> prodList;
	private MainApp mainApp;

	private ObservableList<Product> pList;

	@FXML
	public void handleSearch() {
		String search_str = searchTextField.getText();
		productInfoTableView.getItems().stream().filter(item -> item.getPID().equals(search_str)).findAny()
				.ifPresent(item -> {
					productInfoTableView.getSelectionModel().select(item);
					productInfoTableView.scrollTo(item);
				});
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
		dialogStage.setResizable(false);
	}

	public void setProduct(NBox nBox) {
		this.nBox = nBox;
		pList = FXCollections.observableArrayList();

		for (Product product : nBox.getProductList()) {

			pList.add(product);
		}
		productInfoTableView.setItems(pList);
	}

	public void setProduct(NDBox ndBox) {
		this.ndBox = ndBox;
		pList = FXCollections.observableArrayList();

		for (Product product : ndBox.getProductList()) {
			pList.add(product);
		}
		productInfoTableView.setItems(pList);
	}

	public void setProduct(ArrayList<Product> prodList) {
		this.prodList = prodList;
		pList = FXCollections.observableArrayList();

		for (Product product : prodList) {
			pList.add(product);
		}
		productInfoTableView.setItems(pList);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		productInfoTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
		idColumn.setCellValueFactory(cellData -> cellData.getValue().pidProperty());
		productionDateColumn.setCellValueFactory(cellData -> cellData.getValue().productionDateProperty());
		expirationDateColumn.setCellValueFactory(cellDate -> cellDate.getValue().expirationDateProperty());
		productInfoTableView.setItems(pList);
	}
}
