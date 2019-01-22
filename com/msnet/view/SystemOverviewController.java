package com.msnet.view;

import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.msnet.MainApp;
import com.msnet.model.Product;
import com.msnet.model.Reservation;
import com.msnet.util.Bitcoind;
import com.msnet.util.Settings;
import com.msnet.model.NBox;
import com.msnet.model.NDBox;
import com.msnet.model.NDKey;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
	private TextField product_prodNameTextField;
	@FXML
	private TextField product_quantityTextField;
	@FXML
	private TextField product_productionDateTextField;
	@FXML
	private TextField product_expirationDateTextField;
	@FXML
	private TextField termTextField;
	@FXML
	private ComboBox<String> dateBox;
	@FXML
	private Button miningButton;
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
	@FXML
	private TableColumn<NDBox, Integer> availableColumn;

	@FXML
	private TableView<Reservation> reservationStatusTableView;
	@FXML
	private TableColumn<Reservation, String> r_timeColumn;
	@FXML
	private TableColumn<Reservation, String> r_companyColumn;
	@FXML
	private TableColumn<Reservation, String> r_productNameColumn;
	@FXML
	private TableColumn<Reservation, String> r_productionDateColumn;
	@FXML
	private TableColumn<Reservation, String> r_expirationDateColumn;
	@FXML
	private TableColumn<Reservation, Integer> r_quantityColumn;
	@FXML
	private TableColumn<Reservation, Integer> r_successColumn;
	//////////////////////////////////////////////////
	// Total Quantity
	@FXML
	private TableView<NBox> total_inventoryStatusTableView;
	@FXML
	private TableColumn<NBox, String> total_productNameColumn;
	@FXML
	private TableColumn<NBox, Integer> total_quantityColumn;
	@FXML
	private TableColumn<NBox, Integer> total_availableColumn;
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
	private ObservableList<Reservation> rList;
	private ObservableList<String> list;
	private ObservableList<String> dateList;
	//////////////////////////////////////////////////

	public SystemOverviewController() {
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dateList = FXCollections.observableArrayList("Hour", "Day", "Month", "Year");
		dateBox.setItems(dateList);
		dateBox.setPromptText("Select unit");

		productionDateColumn.setCellValueFactory(cellData -> cellData.getValue().productionDateProperty());
		expirationDateColumn.setCellValueFactory(cellData -> cellData.getValue().expirationDateProperty());
		productNameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
		quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
		availableColumn.setCellValueFactory(cellData -> cellData.getValue().availableProperty().asObject());
		inventoryStatusTableView.setItems(ndList);

		total_productNameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
		total_quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
		total_availableColumn.setCellValueFactory(cellData -> cellData.getValue().availableProperty().asObject());
		total_inventoryStatusTableView.setItems(nList);

		rList = FXCollections.observableArrayList();
		r_timeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
		r_companyColumn.setCellValueFactory(cellData -> cellData.getValue().toCompanyProperty());
		r_productionDateColumn.setCellValueFactory(cellData -> cellData.getValue().productionDateProperty());
		r_expirationDateColumn.setCellValueFactory(cellData -> cellData.getValue().expirationDateProperty());
		r_productNameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
		r_quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
		r_successColumn.setCellValueFactory(cellData -> cellData.getValue().successProperty().asObject());

		// 프로그램이 실행될 때 reservation.dat에 저장된 reservation 데이터를 읽어서 rList에 추가
		String filePath = "C:\\Users\\triz\\AppData\\Roaming\\Bitcoin\\reservation.dat";
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			while (true) {
				String line;
				line = br.readLine();
				if (line == null)
					break;
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject) jsonParser.parse(line);
				String time = jsonObject.get("time").toString();
				String toAddress = jsonObject.get("toAddress").toString();
				String toCompany = jsonObject.get("toCompany").toString();
				String productName = jsonObject.get("productName").toString();
				String productionDate = jsonObject.get("productionDate").toString();
				String expirationDate = jsonObject.get("expirationDate").toString();
				int quantity = Integer.parseInt(jsonObject.get("quantity").toString());
				int success = Integer.parseInt(jsonObject.get("success").toString());

				ArrayList<Product> productList = (ArrayList<Product>) jsonObject.get("productList");

				Reservation tmpReservation = new Reservation(time, toAddress, toCompany, productName, productionDate,
						expirationDate, quantity, success, productList);

				rList.add(tmpReservation);
			}
			br.close();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

		reservationStatusTableView.setItems(rList);

		inventoryStatusTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() >= 2) {
					NDBox selectedNDBox = inventoryStatusTableView.getSelectionModel().getSelectedItem();
					showProductInfoDialog(selectedNDBox);
				} else if (event.getClickCount() == 1) {
					productionDateTextField.setDisable(false);
					expirationDateTextField.setDisable(false);

					NDBox selectedBox = inventoryStatusTableView.getSelectionModel().getSelectedItem();
					productNameTextField.setText(selectedBox.getProductName());
					productionDateTextField.setText(selectedBox.getProductionDate());
					expirationDateTextField.setText(selectedBox.getExpirationDate());
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

		reservationStatusTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() >= 2) {
					Reservation selectedReservation = reservationStatusTableView.getSelectionModel().getSelectedItem();
					showProductInfoDialog(selectedReservation.getProductList());
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
	public void handleMining() {
		MainApp.bitcoinJSONRPClient.set_generate();
	}

	@FXML
	public void handleSendToAddress() {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String time = format.format(cal.getTime());
		String company = companyTextField.getText();
		String address = addressTextField.getText();
		String prodName = productNameTextField.getText();
		String str_quantity = quantityTextField.getText();
		String productionDate = productionDateTextField.getText();
		String expirationDate = expirationDateTextField.getText();

		Reservation r;

		if (address.equals("") || prodName.equals("") || str_quantity.equals("") || productionDate.equals("")
				|| expirationDate.equals("")) {
			// 필수 정보(address, prodName, quantity)가 하나라도 없을 때
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Caution");
			alert.setHeaderText("Enter the information");
			alert.setContentText(
					"Please enter the required information(Address, Product name, Quantity, Production date, Expiration date)");
			alert.showAndWait();
		} else {
			// 필수 정보(address, prodName, quantity, productionDate, expirationDate)가 모두 있을 때
			if (productionDate.equals("") && expirationDate.equals("") == false) {
				// production date와 expiration date 둘 중에 하나만 있을 때 -> 경고!!
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Caution");
				alert.setHeaderText("Enter the date information");
				alert.setContentText("Please enter the exact date information");
				alert.showAndWait();
			} else {
				int beSendedQauntity = Integer.parseInt(str_quantity);
				List<Map> productList = MainApp.bitcoinJSONRPClient.get_current_products();
				Map<NDKey, NDBox> result_NDBox = makeNDBox(productList);
				ArrayList<Product> prodList = new ArrayList<Product>();

				// production date와 expiration date 둘다 있을 때 -> date에 따라 상품 send
				NDKey key = new NDKey(prodName, productionDate, expirationDate);
				NDBox selectedNDBox = result_NDBox.get(key);
				int available = selectedNDBox.getAvailable();
				if (available >= beSendedQauntity) {
					selectedNDBox.setAvailable(available - beSendedQauntity);
					r = new Reservation(time, address, company, prodName, productionDate, expirationDate,
							beSendedQauntity, 0, prodList);

					try {
						// File file = new File("C:\\Users\\" + Settings.getSysUsrName()
						// + "\\AppData\\Roaming\\Bitcoin\\reservation.dat");
						String filePath = "C:\\Users\\triz\\AppData\\Roaming\\Bitcoin\\reservation.dat";
						FileWriter fw = new FileWriter(filePath, true);
						JSONObject jsonObj = new JSONObject();
						JSONArray jsonArray = arrayProductToJSONArray(prodList);

						jsonObj.put("time", time);
						jsonObj.put("toAddress", address);
						jsonObj.put("toCompany", company);
						jsonObj.put("productName", prodName);
						jsonObj.put("productionDate", productionDate);
						jsonObj.put("expirationDate", expirationDate);
						jsonObj.put("quantity", beSendedQauntity);
						jsonObj.put("success", 0);
						jsonObj.put("productList", jsonArray);
						fw.write(jsonObj.toJSONString() + "\n");
						fw.close();
					} catch (IOException e) {
						System.err.println("File writer error");
						e.printStackTrace();
					}

					rList.add(r);
					// ndList.add(selectedNDBox);
					handleInventoryStatus();
					companyTextField.setText("");
					addressTextField.setText("");
					productNameTextField.setText("");
					quantityTextField.setText("");
					productionDateTextField.setText("");
					expirationDateTextField.setText("");
				} else {
					// available의 양이 보내고자 하는 양(beSendedQuantity)보다 적을 때
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Caution");
					if (available == 0) {
						// available이 0일 때는 보낼 수 있는 물건이 없을 때.
						alert.setHeaderText("Wrong Quantity");
						alert.setContentText("There is nothing to send");
					} else {
						alert.setHeaderText("Wrong Quantity");
						alert.setContentText("Please enter the 'Quantity' less than " + available);
					}
					alert.showAndWait();
				}
			}
		}
	}

	public JSONArray arrayProductToJSONArray(ArrayList<Product> productList) {
		JSONArray jsonArr = new JSONArray();
		for (Product p : productList) {
			JSONObject tmpObj = new JSONObject();
			tmpObj.put("productionDate", p.getProductionDate());
			tmpObj.put("expirationDate", p.getExpirationDate());
			tmpObj.put("productName", p.getProductName());
			tmpObj.put("pid", p.getPID());
			jsonArr.add(tmpObj);
		}
		return jsonArr;
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
			alert.setContentText(
					"Please enter the required information(Name, Quantity, Production date, Expiration date)");
			alert.showAndWait();
		} else {
			int quantity = Integer.parseInt(str_quantity);
			List<String> pid = MainApp.bitcoinJSONRPClient.gen_new_product(name, productionDate, expirationDate,
					quantity);
		}
	}

	@FXML
	public void handleGetCurrentTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd:HHmmss");
		Date current = new Date(System.currentTimeMillis());
		Calendar cal = Calendar.getInstance();
		cal.setTime(current);
		String prodTime = format.format(cal.getTimeInMillis()).replace(":", "T");
		String str_term = termTextField.getText();
		product_productionDateTextField.setText(prodTime);
		if (!str_term.equals("")) {
			int term = Integer.parseInt(termTextField.getText());
			String unitOfTerm = dateBox.getSelectionModel().getSelectedItem();
			String expTime = "";
			if (unitOfTerm.equals("Hour")) {
				cal.add(Calendar.HOUR, term);
				expTime = format.format(cal.getTimeInMillis()).replace(":", "T");
			} else if (unitOfTerm.equals("Day")) {
				cal.add(Calendar.DATE, term);
				expTime = format.format(cal.getTimeInMillis()).replace(":", "T");
			} else if (unitOfTerm.equals("Month")) {
				cal.add(Calendar.MONTH, term);
				expTime = format.format(cal.getTimeInMillis()).replace(":", "T");
			} else if (unitOfTerm.equals("Year")) {
				cal.add(Calendar.YEAR, term);
				expTime = format.format(cal.getTimeInMillis()).replace(":", "T");
			}
			product_expirationDateTextField.setText(expTime);
		}
	}

	public void handleQRGenerate() {

	}

	public Map<NDKey, NDBox> makeNDBox(List<Map> productList) {

		Map<NDKey, NDBox> result = new HashMap<NDKey, NDBox>();
		NDKey key;
		Product tmpProduct;
		ArrayList<Product> tmpList;
		NDBox resultNDBox;
		for (Map product : productList) {
			key = new NDKey(String.valueOf(product.get("prodName")), String.valueOf(product.get("production date")),
					String.valueOf(product.get("expiration date")));
			if (result.get(key) == null) {
				tmpProduct = new Product(product);
				tmpList = new ArrayList<Product>();
				tmpList.add(tmpProduct);
				NDBox value = new NDBox(key.getProdName(), key.getProductionDate(), key.getExpirationDate(), tmpList, 1,
						1);
				result.put(key, value);
			} else {
				tmpProduct = new Product(product);
				resultNDBox = result.get(key);
				resultNDBox.addProduct(tmpProduct);
				resultNDBox.setQuantity(resultNDBox.getQuantity() + 1);
				resultNDBox.setAvailable(resultNDBox.getAvailable() + 1);
			}
		}

		// Reservation List에서 NDBox map(result)의 key(prodName, prodDate, expDate)와 동일한
		// 값을 갖는
		// NDBox의 quantity를 구해서 result의 available에서 뺌.
		Iterator<Reservation> reservationItr = rList.iterator();
		Reservation thisReservation;
		NDBox thisNDBox;
		while (reservationItr.hasNext()) {
			thisReservation = reservationItr.next();
			for (NDKey resultKey : result.keySet()) {
				if (thisReservation.getProductName().equals(resultKey.getProdName())
						&& thisReservation.getProductionDate().equals(resultKey.getProductionDate())
						&& thisReservation.getExpirationDate().equals(resultKey.getExpirationDate())) {
					thisNDBox = result.get(resultKey);
					thisNDBox.setAvailable(thisNDBox.getAvailable() - thisReservation.getQuantity());
				}
			}
		}
		return result;
	}

	public Map<String, NBox> makeNBox(Map<NDKey, NDBox> ndboxMap) {
		
		Map<String, NBox> result = new HashMap<String, NBox>();
		String name;
		NDBox tmpNDBox;
		ArrayList<Product> tmpProductList;
		NBox resultNBox;
		Iterator itr;
		Product p;
		NBox tmpProducts;
		
		for (NDKey key : ndboxMap.keySet()) {

			name = key.getProdName();
			if (result.get(name) != null) {
				// result에 key 값을 갖는 NBox가 하나라도 있을 때 -> 원래 있던 NBox에 추가
				tmpNDBox = ndboxMap.get(key);
				tmpProductList = tmpNDBox.getProductList();
				resultNBox = result.get(name);
				itr = tmpProductList.iterator();

				while (itr.hasNext()) {
					p = (Product) itr.next();
					resultNBox.getProductList().add(p);
					resultNBox.setQuantity(resultNBox.getQuantity() + 1);
				}
				resultNBox.setAvailable(resultNBox.getAvailable() + tmpNDBox.getAvailable());
			} else {
				// result에 key 값을 갖는 NBox가 하나도 없을 때 -> 새로 만들어줌
				tmpNDBox = ndboxMap.get(key);
				tmpProducts = new NBox(name, tmpNDBox.getProductList(), tmpNDBox.getQuantity(),
						tmpNDBox.getAvailable());
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

	public void showProductInfoDialog(ArrayList<Product> prodList) {

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
			controller.setProduct(prodList);

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
