package com.msnet.view;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.json.simple.JSONObject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

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

public class ProductInfoDialogController implements Initializable {
	@FXML
	private TableView<JSONObject> productInfoTableView;
	@FXML
	private TableColumn<JSONObject, String> nameColumn;
	@FXML
	private TableColumn<JSONObject, String> idColumn;
	@FXML
	private TableColumn<JSONObject, String> productionDateColumn;
	@FXML
	private TableColumn<JSONObject, String> expirationDateColumn;
	@FXML
	private JFXTextField searchTextField;
	@FXML
	private JFXButton searchButton;

	private ObservableList<JSONObject> pList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		productInfoTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		nameColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty((String) cellData.getValue().get("prodName")));
		idColumn.setCellValueFactory(cellData -> new SimpleStringProperty((String) cellData.getValue().get("PID")));
		productionDateColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty((String) cellData.getValue().get("production date")));
		expirationDateColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty((String) cellData.getValue().get("expiration date")));

		searchTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					handleSearch();
				}
			}
		});
	}

	@FXML
	public void handleSearch() {
		String search_str = searchTextField.getText();
		productInfoTableView.getItems().stream().filter(item -> ((String) item.get("PID")).contains(search_str))
				.findAny().ifPresent(item -> {
					productInfoTableView.getSelectionModel().select(item);
					productInfoTableView.scrollTo(item);
				});
	}

	public void setProduct(List<JSONObject> product_list) {
		pList = FXCollections.observableArrayList(product_list);
		productInfoTableView.setItems(pList);
	}
}
