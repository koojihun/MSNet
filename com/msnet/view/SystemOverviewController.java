package com.msnet.view;

import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.msnet.MainApp;
import com.msnet.model.Product;
import com.msnet.util.Bitcoind;
import com.msnet.model.NBox;
import com.msnet.model.NDBox;
import com.msnet.model.NDKey;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SystemOverviewController implements Initializable {

	//////////////////////////////////////////////////
	// Accordion
	@FXML
	private Button inventoryStatusButton;
	@FXML
	private ComboBox<String> selectBox;
	@FXML
	private TextField product_prodNameTextField;
	@FXML
	private TextField product_quantityTextField;
	@FXML
	private TextField product_productionDateTextField;
	@FXML
	private TextField product_expirationDateTextField;

	ObservableList<String> list;
	//////////////////////////////////////////////////
	// Tab
	@FXML
	private TextArea bitcoindTextArea;
	@FXML
	private TableView<NDBox> inventoryStatusTableView;
	@FXML
	private TableColumn<NDBox, String> productionDateColumn;
	@FXML
	private TableColumn<NDBox, String> expirationDateColumn;
	@FXML
	private TableColumn<NDBox, String> productNameColumn;
	@FXML
	private TableColumn<NDBox, Integer> quantityColumn;
	//////////////////////////////////////////////////
	// Total Quantity
	@FXML
	private TableView<NBox> total_inventoryStatusTableView;
	@FXML
	private TableColumn<NBox, String> total_productNameColumn;
	@FXML
	private TableColumn<NBox, Integer> total_quantityColumn;
	//////////////////////////////////////////////////
	// Product transfer
	@FXML
	private Button addressBookButton;
	@FXML
	private TextField companyTextField;
	@FXML
	private TextField addressTextField;
	@FXML
	private TextField productNameTextField;
	@FXML
	private TextField quantityTextField;
	@FXML
	private TextField productionDateTextField;
	@FXML
	private TextField expirationDateTextField;
	//////////////////////////////////////////////////
	private MainApp mainApp;
	private ObservableList<NBox> nList;
	private ObservableList<NDBox> ndList;
	//////////////////////////////////////////////////

	public SystemOverviewController() {
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		list = FXCollections.observableArrayList("Total", "Details");
		selectBox.setItems(list);

		productionDateColumn.setCellValueFactory(cellData -> cellData.getValue().productionDateProperty());
		expirationDateColumn.setCellValueFactory(cellData -> cellData.getValue().expirationDateProperty());
		productNameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
		quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
		inventoryStatusTableView.setItems(ndList);

		total_productNameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
		total_quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
		total_inventoryStatusTableView.setItems(nList);

		inventoryStatusTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() >= 2) {
					NDBox selectedNDBox = inventoryStatusTableView.getSelectionModel().getSelectedItem();
					showProductInfoDialog(selectedNDBox);
				}
			}
		});

		total_inventoryStatusTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() >= 2) {
					NBox selectedNBox = total_inventoryStatusTableView.getSelectionModel().getSelectedItem();
					showProductInfoDialog(selectedNBox);
				}
			}
		});

		bitcoindTextArea.setEditable(false);
		new Bitcoind(bitcoindTextArea).start();
	}

	@FXML
	public void handleAddressBook() {
		showAddressBookDialog();
	}

	@FXML
	public void handleSendToAddress() {

		String address = addressTextField.getText();
		String prodName = productNameTextField.getText();
		String str_quantity = quantityTextField.getText();
		String productionDate = productionDateTextField.getText();
		String expirationDate = expirationDateTextField.getText();

		if (address.equals("") || prodName.equals("") || prodName.equals("")) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Caution");
			alert.setHeaderText("Enter the information");
			alert.setContentText("Please enter the required information(Address, Product name, Quantity)");
			alert.showAndWait();
		} else {
			int quantity = Integer.getInteger(str_quantity);
			List<Map> productList = MainApp.bitcoinJSONRPClient.get_current_products();
			Map<NDKey, NDBox> productND_Map = makeNDBox(productList);

			if (productionDateTextField.getText().equals("") && expirationDateTextField.getText().equals("")) {
				Map<String, NBox> productN_Map = makeNBox(productND_Map);

			} else if (!productionDateTextField.getText().equals("") && !expirationDateTextField.getText().equals("")) {
				NDKey key = new NDKey(prodName, productionDate, expirationDate);
				NDBox ndBox = productND_Map.get(key);
				MainApp.bitcoinJSONRPClient.send_many(address, quantity, ndBox.getPid());
			} else {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Caution");
				alert.setHeaderText("Enter the date information");
				alert.setContentText("Please enter the exact date information");
				alert.showAndWait();
			}
		}
	}

	@FXML
	public void handleSelectProduct() {
		String selected = selectBox.getSelectionModel().getSelectedItem();
		if (selected.equals("Total")) {
			NBox selectedBox = total_inventoryStatusTableView.getSelectionModel().getSelectedItem();
			productNameTextField.setText(selectedBox.getProductName());
		} else if (selected.equals("Details")) {
			NDBox selectedBox = inventoryStatusTableView.getSelectionModel().getSelectedItem();
			productNameTextField.setText(selectedBox.getProductName());
			productionDateTextField.setText(selectedBox.getProductionDate());
			expirationDateTextField.setText(selectedBox.getExpirationDate());
		} else {
			System.out.println("Select!!!!");
		}
	}

	@FXML
	public void handleInventoryStatus() {
		List<Map> productList = MainApp.bitcoinJSONRPClient.get_current_products();
		//////////////////////////////////////////////////////////////
		Map<NDKey, NDBox> result_NDBox = makeNDBox(productList);

		ndList = FXCollections.observableArrayList();

		for (NDKey key : result_NDBox.keySet()) {
			ndList.add(result_NDBox.get(key));
		}
		inventoryStatusTableView.setItems(ndList);
		//////////////////////////////////////////////////////////////
		Map<String, NBox> result_NBox = makeNBox(result_NDBox);

		nList = FXCollections.observableArrayList();

		for (String key : result_NBox.keySet()) {
			nList.add(result_NBox.get(key));
		}
		total_inventoryStatusTableView.setItems(nList);
		//////////////////////////////////////////////////////////////
	}

	@FXML
	public void handleMakeProducts() {
		String name = product_prodNameTextField.getText();
		String str_quantity = product_quantityTextField.getText();
		String productionDate = product_productionDateTextField.getText();
		String expirationDate = product_expirationDateTextField.getText();
		
		if (name.equals("") || str_quantity.equals("") || productionDate.equals("") || expirationDate.equals("")) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Caution");
			alert.setHeaderText("Enter the information");
			alert.setContentText("Please enter the required information(Name, Quantity, Production date, Expiration date)");
			alert.showAndWait();
		} else {
			int quantity = Integer.parseInt(str_quantity);
			List<String> pids = MainApp.bitcoinJSONRPClient.gen_new_product(name, productionDate, expirationDate, quantity);
			if (pids == null)
				System.out.println("실패");
			else
				System.out.println("성공");
		}
	}
	
	public Map<NDKey, NDBox> makeNDBox(List<Map> productList) {

		Map<NDKey, NDBox> result = new HashMap<NDKey, NDBox>();

		for (Map product : productList) {
			NDKey key = new NDKey(String.valueOf(product.get("prodName")),
					String.valueOf(product.get("production date")), String.valueOf(product.get("expiration date")));
			if (result.get(key) == null) {
				Product tmpProduct = new Product(product);
				ArrayList<Product> tmpList = new ArrayList<Product>();
				tmpList.add(tmpProduct); 
				NDBox value = new NDBox(key.getProdName(), key.getProductionDate(), key.getExpirationDate(), tmpList,
						1);
				result.put(key, value);
			} else {
				Product tmpProduct = new Product(product);
				NDBox resultNDBox = result.get(key);
				resultNDBox.addProduct(tmpProduct);
				resultNDBox.setQuantity(resultNDBox.getQuantity() + 1);
			}
		}
		return result;
	}
	
	public Map<String, NBox> makeNBox(Map<NDKey, NDBox> ndboxMap) {
		Map<String, NBox> result = new HashMap<String, NBox>();

		for (NDKey key : ndboxMap.keySet()) {

			String name = key.getProdName();
			if (result.get(name) != null) {
				NDBox tmpNDBox = ndboxMap.get(key);
				
				ArrayList<Product> tmpProductList = tmpNDBox.getProductList();
				NBox resultNBox = result.get(name);
				Iterator itr = tmpProductList.iterator(); 

				while (itr.hasNext()) {
					Product p = (Product) itr.next();
					resultNBox.getProductList().add(p);
					resultNBox.setQuantity(resultNBox.getQuantity() + 1);
				}

			} else {
				NDBox tmpNDBox = ndboxMap.get(key);
				NBox tmpProducts = new NBox(name, tmpNDBox.getProductList(), tmpNDBox.getQuantity());
				result.put(name, tmpProducts);
			}
		}
		return result;
	}

	public void showProductInfoDialog(NDBox ndBox) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/ProductInfoDialog.fxml"));
			AnchorPane productInfoPane = (AnchorPane) loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Product Info");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainApp.getPrimaryStage());
			Scene scene = new Scene(productInfoPane);
			dialogStage.setScene(scene);

			ProductInfoDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setProduct(ndBox);

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

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Product Info");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainApp.getPrimaryStage());
			Scene scene = new Scene(productInfoPane);
			dialogStage.setScene(scene);

			ProductInfoDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setProduct(nBox);

			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showAddressBookDialog() {

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/AddressBookDialog.fxml"));
			AnchorPane addressBookPane = (AnchorPane) loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Address Book");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainApp.getPrimaryStage());
			Scene scene = new Scene(addressBookPane);
			dialogStage.setScene(scene);

			AddressBookDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setCompanyTextField(companyTextField);
			controller.setAddressTextField(addressTextField);

			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
}
