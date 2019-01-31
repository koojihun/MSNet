package com.msnet.view;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.json.simple.JSONObject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.msnet.MainApp;
import com.msnet.model.Reservation;
import com.msnet.model.WDB;
import com.msnet.model.Worker;
import com.msnet.util.AES;
import com.msnet.util.Alert;
import com.msnet.util.PDB;
import com.msnet.util.QRMaker;
import com.msnet.util.Settings;
import com.msnet.util.ThreadGroup;
import com.msnet.model.NBox;
import com.msnet.model.NDBox;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
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
	private JFXButton inventoryStatusButton;
	@FXML
	private JFXTextField product_prodNameTextField;
	@FXML
	private JFXTextField product_quantityTextField;
	@FXML
	private JFXTextField product_productionDateTextField;
	@FXML
	private JFXTextField product_expirationDateTextField;
	@FXML
	private JFXTextField termTextField;
	@FXML
	private JFXComboBox<String> dateBox;
	@FXML
	private JFXButton miningButton;
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
	private JFXButton addressBookButton;
	@FXML
	private JFXTextField companyTextField;
	@FXML
	private JFXTextField addressTextField;
	@FXML
	private JFXTextField productNameTextField;
	@FXML
	private JFXTextField quantityTextField;
	@FXML
	private JFXTextField productionDateTextField;
	@FXML
	private JFXTextField expirationDateTextField;
	//////////////////////////////////////////////////
	// Worker management
	@FXML
	private TableView<Worker> workerTableView;
	@FXML
	private TableColumn<Worker, String> worker_idColumn;
	@FXML
	private TableColumn<Worker, String> worker_nameColumn;
	@FXML
	private TableColumn<Worker, Boolean> worker_isLoginColumn;
	//////////////////////////////////////////////////
	private MainApp mainApp;
	private ObservableList<String> dateList;
	private static AnchorPane systemOverview;

	//////////////////////////////////////////////////
	public SystemOverviewController() { }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//////////////////////////////////////
		// Product Database Initialize. //
		new PDB(reservationStatusTableView);//
		new WDB(workerTableView); //
		//////////////////////////////////////
		companyTextField.setEditable(false);
		addressTextField.setEditable(false);
		productNameTextField.setEditable(false);
		productionDateTextField.setEditable(false);
		expirationDateTextField.setEditable(false);

		dateList = FXCollections.observableArrayList("Hour", "Day", "Month", "Year");
		dateBox.setItems(dateList);
		dateBox.setPromptText("Select date unit");

		productionDateColumn.setCellValueFactory(cellData -> cellData.getValue().productionDateProperty());
		expirationDateColumn.setCellValueFactory(cellData -> cellData.getValue().expirationDateProperty());
		productNameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
		quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
		availableColumn.setCellValueFactory(cellData -> cellData.getValue().availableProperty().asObject());
		inventoryStatusTableView.setItems(PDB.getNDList());

		total_productNameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
		total_quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
		total_availableColumn.setCellValueFactory(cellData -> cellData.getValue().availableProperty().asObject());
		total_inventoryStatusTableView.setItems(PDB.getNList());

		r_timeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
		r_companyColumn.setCellValueFactory(cellData -> cellData.getValue().toCompanyProperty());
		r_productionDateColumn.setCellValueFactory(cellData -> cellData.getValue().productionDateProperty());
		r_expirationDateColumn.setCellValueFactory(cellData -> cellData.getValue().expirationDateProperty());
		r_productNameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
		r_quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
		r_successColumn.setCellValueFactory(cellData -> cellData.getValue().successProperty().asObject());

		// 프로그램이 실행될 때 reservation.dat에 저장된 reservation 데이터를 읽어서 rList에 추가
		reservationStatusTableView.setItems(PDB.getRList());

		worker_idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
		worker_nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		worker_isLoginColumn.setCellValueFactory(cellData -> cellData.getValue().isLoginProperty());
		workerTableView.setItems(WDB.getWorkerList());

		inventoryStatusTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				NDBox selectedNDBox = inventoryStatusTableView.getSelectionModel().getSelectedItem();
				if (selectedNDBox != null) {
					if (event.getClickCount() >= 2) {
						ProgressDialog.show(mainApp.getPrimaryStage(), false);
						Thread t = new Thread() {
							public void run() {
								List<JSONObject> plist = MainApp.bitcoinJSONRPClient.get_current_products_by_ndd(
										selectedNDBox.getProductName(), selectedNDBox.getProductionDate(),
										selectedNDBox.getExpirationDate());
								Platform.runLater(() -> {
									showProductInfoDialog(plist);
									ProgressDialog.close();
								});
							}
						};
						ThreadGroup.addThread(t);
					} else if (event.getClickCount() == 1) {
						productNameTextField.setText(selectedNDBox.getProductName());
						productionDateTextField.setText(selectedNDBox.getProductionDate());
						expirationDateTextField.setText(selectedNDBox.getExpirationDate());
						quantityTextField.setText(String.valueOf(selectedNDBox.getAvailable()));
					}
				}
			}
		});

		/////////////////////////////////////////////////////////////////////////////////////
		// (inventoryStatus에서) 오른쪽 마우스 클릭하면 QR code 생성 관련
		MenuItem mi_qr_inventory = new MenuItem("Generate QR code");
		mi_qr_inventory.setOnAction((ActionEvent event) -> {
			QRMaker qrMaker = new QRMaker(300, 300);
			NDBox selectedNDBox = (NDBox) inventoryStatusTableView.getSelectionModel().getSelectedItem();
			List<JSONObject> plist = MainApp.bitcoinJSONRPClient.get_current_products_by_ndd(
					selectedNDBox.getProductName(), selectedNDBox.getProductionDate(),
					selectedNDBox.getExpirationDate());
			String fileName;
			String pid;
			String prodName;
			String productionDate;
			String expirationDate;
			String input;
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd:HHmmss");
			Date current = new Date(System.currentTimeMillis());
			Calendar cal = Calendar.getInstance();
			cal.setTime(current);
			String prodTime = format.format(cal.getTimeInMillis()).replace(":", "T");
			int i = 1;
			for (JSONObject p : plist) {
				pid = AES.encrypt(((String) p.get("PID")));
				prodName = (String) p.get("prodName");
				productionDate = (String) p.get("production date");
				expirationDate = (String) p.get("expiration date");
				input = "http://www.godqr.com:8090/NewSystem/track.do?&pid=" + pid + "&prodName="
						+ prodName + "&productionDate=" + productionDate
						+ "&expirationDate=" + expirationDate;
				fileName = prodTime + "_" + prodName + "_" + i;
				qrMaker.makeQR(fileName, input);
				i++;
			}
		});

		ContextMenu menu_inventory = new ContextMenu();
		menu_inventory.getItems().add(mi_qr_inventory);
		inventoryStatusTableView.setContextMenu(menu_inventory);
		////////////////////////////////////////////////////////////////////////////////////
		total_inventoryStatusTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() >= 2) {
					NBox selectedNBox = total_inventoryStatusTableView.getSelectionModel().getSelectedItem();
					if (selectedNBox != null) {
						ProgressDialog.show(mainApp.getPrimaryStage(), false);
						Thread t = new Thread() {
							public void run() {
								List<JSONObject> plist = MainApp.bitcoinJSONRPClient
										.get_current_products_by_name(selectedNBox.getProductName());
								Platform.runLater(() -> {
									showProductInfoDialog(plist);
									ProgressDialog.close();
								});
							}
						};
						ThreadGroup.addThread(t);
					}
				}
			}
		});

		/////////////////////////////////////////////////////////////////////////////////////
		// (total_inventoryStatus에서) 오른쪽 마우스 클릭하면 QR code 생성 관련
		MenuItem mi_qr_totalInventory = new MenuItem("Generate QR code");
		mi_qr_totalInventory.setOnAction((ActionEvent event) -> {
			QRMaker qrMaker = new QRMaker(300, 300);
			NBox selectedNBox = total_inventoryStatusTableView.getSelectionModel().getSelectedItem();
			List<JSONObject> plist = MainApp.bitcoinJSONRPClient
					.get_current_products_by_name(selectedNBox.getProductName());
			String fileName;
			String pid;
			String prodName;
			String productionDate;
			String expirationDate;
			String input;
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd:HHmmss");
			Date current = new Date(System.currentTimeMillis());
			Calendar cal = Calendar.getInstance();
			cal.setTime(current);
			String prodTime = format.format(cal.getTimeInMillis()).replace(":", "T");
			int i = 1;
			for (JSONObject p : plist) {
				pid = AES.encrypt(((String) p.get("PID")));
				prodName = (String) p.get("prodName");
				productionDate = (String) p.get("production date");
				expirationDate = (String) p.get("expiration date");
				input = "http://www.godqr.com:8090/NewSystem/track.do?&pid=" + pid + "&prodName="
						+ prodName + "&productionDate" + productionDate
						+ "&expirationDate" + expirationDate;
				fileName = prodTime + "_" + prodName + "_" + i;
				qrMaker.makeQR(fileName, input);
				i++;
			}
		});

		ContextMenu menu_totalInvtory = new ContextMenu();
		menu_totalInvtory.getItems().add(mi_qr_totalInventory);
		total_inventoryStatusTableView.setContextMenu(menu_totalInvtory);
		/////////////////////////////////////////////////////////////////////////////////////
		reservationStatusTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() >= 2) {
					Reservation selectedReservation = reservationStatusTableView.getSelectionModel().getSelectedItem();
					showProductInfoDialog(selectedReservation.getProductList());
				}
			}
		});
	}

	@FXML
	public void handleAddressBook() {
		showAddressBookDialog();
	}

	@FXML
	public void handleMining() {
		ProgressDialog.show(mainApp.getPrimaryStage(), false);
		Thread t = new Thread() {
			public void run() {
				try {
					MainApp.bitcoinJSONRPClient.set_generate();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					Platform.runLater(() -> {
						ProgressDialog.close();
					});
				}
			}
		};
		ThreadGroup.addThread(t);
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

		if (address.equals("") || prodName.equals("") || str_quantity.equals("") || productionDate.equals("")
				|| expirationDate.equals("")) {
			// 필수 정보(address, prodName, quantity)가 하나라도 없을 때
			String head = "Enter the information";
			String body = "Please enter the required information(Address, Product name, Quantity, Production date, Expiration date)";
			new Alert(systemOverview, head, body);
		} else {
			// 필수 정보(address, prodName, quantity, productionDate, expirationDate)가 모두 있을 때
			if (productionDate.equals("") && expirationDate.equals("") == false) {
				// production date와 expiration date 둘 중에 하나만 있을 때 -> 경고!!
				String head = "Enter the date information";
				String body = "Please enter the exact date information";
				new Alert(systemOverview, head, body);
			} else {
				int beSendedQauntity = Integer.parseInt(str_quantity);
				NDBox selectedNDBox = inventoryStatusTableView.getSelectionModel().getSelectedItem();
				int available = selectedNDBox.getAvailable();

				if (available >= beSendedQauntity) {
					PDB.reserveProduct(time, address, company, selectedNDBox, beSendedQauntity);
					companyTextField.setText("");
					addressTextField.setText("");
					productNameTextField.setText("");
					quantityTextField.setText("");
					productionDateTextField.setText("");
					expirationDateTextField.setText("");
				} else {
					// available의 양이 보내고자 하는 양(beSendedQuantity)보다 적을 때
					if (available == 0) {
						String head = "Wrong Quantity";
						String body = "There is nothing to send";
						new Alert(systemOverview, head, body);

					} else {
						String head = "Wrong Quantity";
						String body = "Please enter the 'Quantity' less than " + available;
						new Alert(systemOverview, head, body);
					}
				}
			}
		}
	}

	@FXML
	public void handleInventoryStatus() {
		ProgressDialog.show(mainApp.getPrimaryStage(), false);
		Thread t = new Thread() {
			public void run() {
				PDB.refreshInventory(MainApp.bitcoinJSONRPClient.get_ndd_boxes());
				inventoryStatusTableView.setItems(PDB.getNDList());
				total_inventoryStatusTableView.setItems(PDB.getNList());
				Platform.runLater(() -> {
					ProgressDialog.close();
				});
			}
		};
		ThreadGroup.addThread(t);
	}

	@FXML
	public void handleMakeProducts() {
		String prodName = product_prodNameTextField.getText();
		String str_quantity = product_quantityTextField.getText();
		String productionDate = product_productionDateTextField.getText();
		String expirationDate = product_expirationDateTextField.getText();

		if (prodName.equals("") || str_quantity.equals("") || productionDate.equals("") || expirationDate.equals("")) {
			String head = "Enter the information";
			String body = "Please enter the required information(Name, Quantity, Production date, Expiration date)";
			new Alert(systemOverview, head, body);
		} else {
			int quantity = Integer.parseInt(str_quantity);

			if (quantity > 10000) {
				String head = "Too much quantities.";
				String body = "Please enter the 'Quantity' less than 10000";
				new Alert(systemOverview, head, body);
			} else {
				ProgressDialog.show(mainApp.getPrimaryStage(), false);
				Thread t = new Thread() {
					public void run() {
						System.out.println(prodName);
						MainApp.bitcoinJSONRPClient.gen_new_product(prodName, productionDate, expirationDate, quantity, Settings.getBitcoinAddress());
						Platform.runLater(() -> {
							ProgressDialog.close();
						});
					}
				};
				ThreadGroup.addThread(t);
				product_prodNameTextField.setText("");
				product_quantityTextField.setText("");
				product_productionDateTextField.setText("");
				product_expirationDateTextField.setText("");
				termTextField.setText("");
			}
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

	public void showProductInfoDialog(List<JSONObject> prodList) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/ProductInfoDialog.fxml"));
			AnchorPane productInfoPane = (AnchorPane) loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Product Info");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainApp.getPrimaryStage());
			dialogStage.setResizable(false);

			Scene scene = new Scene(productInfoPane);
			dialogStage.setScene(scene);

			ProductInfoDialogController controller = loader.getController();
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

	@FXML
	public void showChart() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/ChartDialog.fxml"));
			AnchorPane chartPane = (AnchorPane) loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Dashboard");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainApp.getPrimaryStage());
			Scene scene = new Scene(chartPane);
			dialogStage.setScene(scene);

			ChartDialogController controller = loader.getController();
			controller.setNDList(PDB.getNDList());
			controller.setNList(PDB.getNList());
			dialogStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

	}

	public void setPane(AnchorPane systemOverview) {
		SystemOverviewController.systemOverview = systemOverview;
	}

	public static AnchorPane getSystemOverview() {
		return systemOverview;
	}

}
