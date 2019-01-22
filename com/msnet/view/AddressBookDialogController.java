package com.msnet.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.msnet.model.Company;
import com.msnet.util.HTTP;
import com.msnet.util.Settings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddressBookDialogController implements Initializable {

	@FXML
	private TextField searchTextField;
	@FXML
	private TableView<Company> companyInfoTableView;
	@FXML
	private TableColumn<Company, String> nameColumn;
	@FXML
	private TableColumn<Company, String> addressColumn;
	@FXML
	private TableColumn<Company, String> bitcoinAddressColumn;

	private Stage dialogStage;
	private ObservableList<Company> companyList;
	private TextField companyTextField;
	private TextField addressTextField;

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
		dialogStage.setResizable(false);
	}

	public void setCompanyTextField(TextField companyTextField) {
		this.companyTextField = companyTextField;
	}

	public void setAddressTextField(TextField addressTextField) {
		this.addressTextField = addressTextField;
	}

	@FXML
	public void handleSearch() {
		String search_str = searchTextField.getText();
		System.out.println(search_str);
		companyInfoTableView
				.getItems().stream().filter(item -> ((item.getName().equals(search_str))
						| (item.getAddress().equals(search_str)) | (item.getBitcoinAddress().equals(search_str))))
				.findAny().ifPresent(item -> {
					companyInfoTableView.getSelectionModel().select(item);
					companyInfoTableView.scrollTo(item);
				});
	}

	@FXML
	public void handleOk() {
		Company comp = companyInfoTableView.getSelectionModel().getSelectedItem();
		companyTextField.setText(comp.getName());
		addressTextField.setText(comp.getBitcoinAddress());
		this.dialogStage.close();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		ArrayList<String> key = new ArrayList<String>();
		ArrayList<String> val = new ArrayList<String>();

		key.add("id");
		key.add("password");
		val.add(Settings.getId());
		val.add(Settings.getPassword());

		try {
			JSONObject response = HTTP.send("http://166.104.126.42:8090/NewSystem/listCompany.do", "post", key, val);
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

	}

}
