package com.msnet.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.jfoenix.controls.JFXTextField;
import com.msnet.model.Company;
import com.msnet.util.HTTP;
import com.msnet.util.Settings;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AddressBookDialogController implements Initializable {
	@FXML
	private AnchorPane pane;
	@FXML
	private JFXTextField searchTextField;
	@FXML
	private TableView<Company> companyInfoTableView;
	@FXML
	private TableColumn<Company, String> nameColumn;
	@FXML
	private TableColumn<Company, String> addressColumn;
	@FXML
	private TableColumn<Company, String> bitcoinAddressColumn;
	private TextField companyTextField;
	private TextField addressTextField;
	private Stage dialogStage;
	private ObservableList<Company> companyList;
	public double xOffset = 0;
	public double yOffset = 0;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		ArrayList<String> key = new ArrayList<String>();
		ArrayList<String> val = new ArrayList<String>();

		key.add("id");
		key.add("password");
		val.add(Settings.getId());
		val.add(Settings.getPassword());

		try {
			JSONObject response = HTTP.send("http://166.104.126.42:8090/NewSystem/getAddressBook.do", "post", key, val);
			JSONArray arr = (JSONArray) response.get("result");
			companyList = FXCollections.observableArrayList();

			for (int i = 0; i < arr.size(); i++) {
				JSONObject tmp = (JSONObject) arr.get(i);
				String company_name = (String) tmp.get("company_name");
				String company_address = (String) tmp.get("company_address");
				String bitcoin_address = (String) tmp.get("bitcoin_address");
				Company tmpCompany = new Company(company_name, company_address, bitcoin_address);
				companyList.add(tmpCompany);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
		bitcoinAddressColumn.setCellValueFactory(cellData -> cellData.getValue().bitcoinAddressProperty());
		companyInfoTableView.setItems(companyList);
		
		searchTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER) {
					handleSearch();
				}
			}			
		});
		
		companyInfoTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() >= 2) {
					Company comp = companyInfoTableView.getSelectionModel().getSelectedItem();
					companyTextField.setText(comp.getName());
					addressTextField.setText(comp.getBitcoinAddress());
					dialogStage.close();
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
		companyInfoTableView
				.getItems().stream().filter(item -> ((item.getName().contains(search_str))
						| (item.getAddress().contains(search_str)) | (item.getBitcoinAddress().contains(search_str))))
				.findAny().ifPresent(item -> {
					companyInfoTableView.getSelectionModel().select(item);
					companyInfoTableView.scrollTo(item);
				});
	}
	
	@FXML
	public void handleClose() {
		dialogStage.close();
	}
	
	public void setCompanyTextField(TextField companyTextField) {
		this.companyTextField = companyTextField;
	}

	public void setAddressTextField(TextField addressTextField) {
		this.addressTextField = addressTextField;
	}
	
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
		dialogStage.setResizable(false);
		this.dialogStage.initStyle(StageStyle.UNDECORATED); // 제목표시줄 안보이게 하기 위한 작업
	}
}
