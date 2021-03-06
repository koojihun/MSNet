package com.msnet.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.json.simple.JSONObject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.msnet.MainApp;
import com.msnet.model.Reservation;
import com.msnet.model.Worker;
import com.msnet.util.AES;
import com.msnet.util.Alert;
import com.msnet.util.DB;
import com.msnet.util.HTTP;
import com.msnet.util.PDB;
import com.msnet.util.QRMaker;
import com.msnet.util.Settings;
import com.msnet.util.ThreadGroup;
import com.msnet.util.WDB;
import com.msnet.model.NBox;
import com.msnet.model.NDBox;
import com.msnet.model.Product;

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
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SystemOverviewController implements Initializable {
	private final String serverURL = "http://www.godqr.com/";
	//////////////////////////////////////////////////
	// Accordion
	@FXML
	private JFXButton inventoryStatusButton;
	@FXML
	private JFXTextField product_prodNameTextField;
	@FXML
	private JFXTextField product_quantityTextField;
	@FXML
	private JFXTextField product_p_year_TextField; // production date
	@FXML
	private JFXTextField product_p_month_TextField;
	@FXML
	private JFXTextField product_p_day_TextField;
	@FXML
	private JFXTextField product_p_hour_TextField;
	@FXML
	private JFXTextField product_p_minute_TextField;
	@FXML
	private JFXTextField product_p_second_TextField;
	@FXML
	private JFXTextField product_e_year_TextField; // expiration date
	@FXML
	private JFXTextField product_e_month_TextField;
	@FXML
	private JFXTextField product_e_day_TextField;
	@FXML
	private JFXTextField product_e_hour_TextField;
	@FXML
	private JFXTextField product_e_minute_TextField;
	@FXML
	private JFXTextField product_e_second_TextField;
	@FXML
	private JFXTextField termTextField;
	@FXML
	private JFXComboBox<String> dateBox;
	@FXML
	private JFXButton miningButton;
	//////////////////////////////////////////////////
	// Tab
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
	@FXML
	private AnchorPane left_anchor;
	@FXML
	private TabPane right_tab;
	public double xOffset = 0;
	public double yOffset = 0;

	//////////////////////////////////////////////////
	public SystemOverviewController() {
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//////////////////////////////////////
		// In Memory Database Initialize. //
		new PDB(); //
		new WDB(); //
		/////////////////////////////////////////////////////////////
		// To move an undecorated stage. //
		left_anchor.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = event.getSceneX();
				yOffset = event.getSceneY();
			}
		});

		left_anchor.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mainApp.getPrimaryStage().setX(event.getScreenX() - xOffset);
				mainApp.getPrimaryStage().setY(event.getScreenY() - yOffset);
			}
		});

		right_tab.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = event.getSceneX();
				yOffset = event.getSceneY();
			}
		});

		right_tab.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mainApp.getPrimaryStage().setX(event.getScreenX() - xOffset);
				mainApp.getPrimaryStage().setY(event.getScreenY() - yOffset);
			}
		});
		////////////////////////////////////////////////////////////////////
		product_e_year_TextField.setEditable(false);
		product_e_month_TextField.setEditable(false);
		product_e_day_TextField.setEditable(false);
		product_e_hour_TextField.setEditable(false);
		product_e_minute_TextField.setEditable(false);
		product_e_second_TextField.setEditable(false);

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
								List<JSONObject> jsonPList = MainApp.bitcoinJSONRPClient.get_current_products_by_ndd(
										selectedNDBox.getProductName(), selectedNDBox.getProductionDate(),
										selectedNDBox.getExpirationDate());
								ArrayList<Product> pList = new ArrayList<Product>();
								for (JSONObject obj : jsonPList)
									pList.add(new Product(obj));
								Platform.runLater(() -> {
									showProductInfoDialog(pList);
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
			NDBox selectedNDBox = (NDBox) inventoryStatusTableView.getSelectionModel().getSelectedItem();
			List<JSONObject> plist = MainApp.bitcoinJSONRPClient.get_current_products_by_ndd(
					selectedNDBox.getProductName(), selectedNDBox.getProductionDate(),
					selectedNDBox.getExpirationDate());
			handleQRGenerate(plist);
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
								List<JSONObject> jsonPList = MainApp.bitcoinJSONRPClient
										.get_current_products_by_name(selectedNBox.getProductName());
								List<Product> pList = new ArrayList<Product>();
								for (JSONObject obj : jsonPList)
									pList.add(new Product(obj));
								Platform.runLater(() -> {
									showProductInfoDialog(pList);
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
			NBox selectedNBox = total_inventoryStatusTableView.getSelectionModel().getSelectedItem();
			List<JSONObject> plist = MainApp.bitcoinJSONRPClient
					.get_current_products_by_name(selectedNBox.getProductName());
			handleQRGenerate(plist);
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
		///////////////////////////////////////////////////////////////////
		// Reservation tab에서 오른쪽 마우스 눌러서 reservation을 지울 수 있는 기능.
		// (reservation의 success가 0보다 클 때 -> completedReservation에 추가)
		MenuItem mi_reservation = new MenuItem("Delete");
		mi_reservation.setOnAction((ActionEvent event) -> {
			Reservation r = reservationStatusTableView.getSelectionModel().getSelectedItem();
			DB db = new DB();
			if (r.getSuccess() > 0) {
				r.setQuantity(r.getSuccess());
				db.writeCompletedReservation(r);
			}
			db.deleteReservation(r);
			PDB.getRList().remove(r);
		});
		ContextMenu menu_reservation = new ContextMenu();
		menu_reservation.getItems().add(mi_reservation);
		reservationStatusTableView.setContextMenu(menu_reservation);
		///////////////////////////////////////////////////////////////////
		// worker tab에서 오른쪽 마우스 눌러서 worker를 지울 수 있는 기능
		MenuItem mi_worker = new MenuItem("Delete");
		mi_worker.setOnAction((ActionEvent event) -> {
			Worker w = workerTableView.getSelectionModel().getSelectedItem();
			DB db = new DB();
			db.deleteWorker(w);
			WDB.delete(w);
		});
		ContextMenu menu_worker = new ContextMenu();
		menu_worker.getItems().add(mi_worker);
		workerTableView.setContextMenu(menu_worker);
		///////////////////////////////////////////////////////////////////
	}

	@FXML
	public void handleMinimize() {
		mainApp.getPrimaryStage().setIconified(true);
	}

	@FXML
	public void handleClose() {
		try {
			mainApp.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Platform.exit();
		System.exit(0);
	}

	@FXML
	public void handleAddressBook() {
		showAddressBookDialog();
	}

	@FXML
	public void handleFileWrite() {
		DB db = new DB();
		db.writeToFile();
	}
	
	@FXML
	public void handleOpenFolder() {
		try {
			Desktop.getDesktop().open(new File("C:\\Users\\" + Settings.getSysUsrName() + "\\AppData\\Roaming\\bitcoin"));
		} catch (IOException e) {
			e.printStackTrace();
		}
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

		if (company.equals("") || address.equals("") || prodName.equals("") || str_quantity.equals("")
				|| productionDate.equals("") || expirationDate.equals("")) {
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

		String p_year = product_p_year_TextField.getText();
		String p_month = product_p_month_TextField.getText();
		String p_day = product_p_day_TextField.getText();
		String p_hour = product_p_hour_TextField.getText();
		String p_minute = product_p_minute_TextField.getText();
		String p_second = product_p_second_TextField.getText();
		String productionDate = p_year + p_month + p_day + "T" + p_hour + p_minute + p_second;

		String e_year = product_e_year_TextField.getText();
		String e_month = product_e_month_TextField.getText();
		String e_day = product_e_day_TextField.getText();
		String e_hour = product_e_hour_TextField.getText();
		String e_minute = product_e_minute_TextField.getText();
		String e_second = product_e_second_TextField.getText();
		String expirationDate = e_year + e_month + e_day + "T" + e_hour + e_minute + e_second;

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
				boolean result = false;

				// 서버에 나의 할당량을 물어봐서 할당량에 여유가 있으면 true를 return. 
				// true일 경우에만 gen_new_product() 진행
				ArrayList<String> key = new ArrayList<String>();
				ArrayList<String> val = new ArrayList<String>();

				key.add("id");
				key.add("password");
				key.add("quantity");
				val.add(Settings.getId());
				val.add(Settings.getPassword());
				val.add(str_quantity);

				try {
					JSONObject jsonResult = HTTP.send(serverURL + "reportGen.do.pc", "post", key, val);
					result = (boolean) jsonResult.get("result");
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (result) {
					// 할당량에 여유가 있는 경우, gen_new_product() 실행.
					ProgressDialog.show(mainApp.getPrimaryStage(), false);
					Thread t = new Thread() {
						public void run() {
							MainApp.bitcoinJSONRPClient.gen_new_product(prodName, productionDate, expirationDate,
									quantity);
							MainApp.bitcoinJSONRPClient.set_generate();
							Platform.runLater(() -> {
								ProgressDialog.close();
							});
						}
					};
					ThreadGroup.addThread(t);
				} else {
					// 할당량에 여유가 없는 경우, 경고창 뜸.
					String head = "Low quota";
					String body = "Go to the site and increase your quota.";
					new Alert(systemOverview, head, body);
				}

				product_prodNameTextField.setText("");
				product_quantityTextField.setText("");
				product_p_year_TextField.setText("");
				product_p_month_TextField.setText("");
				product_p_day_TextField.setText("");
				product_p_hour_TextField.setText("");
				product_p_minute_TextField.setText("");
				product_p_second_TextField.setText("");
				product_e_year_TextField.setText("");
				product_e_month_TextField.setText("");
				product_e_day_TextField.setText("");
				product_e_hour_TextField.setText("");
				product_e_minute_TextField.setText("");
				product_e_second_TextField.setText("");
				termTextField.setText("");
			}
		}
	}

	@FXML
	public void handleAdd() {
		
		String p_year = product_p_year_TextField.getText();
		String p_month = product_p_month_TextField.getText();
		String p_day = product_p_day_TextField.getText();
		String p_hour = product_p_hour_TextField.getText();
		String p_minute = product_p_minute_TextField.getText();
		String p_second = product_p_second_TextField.getText();
		String time = p_year + "-" + p_month + "-" + p_day + " " + p_hour + ":" + p_minute + ":" + p_second;
		String str_term = termTextField.getText();
		
		if (p_year.equals("") | p_month.equals("") | p_day.equals("") | p_hour.equals("") | p_minute.equals("")
				| p_second.equals("")) {
			String head = "날짜를 입력하세요.";
			String body = "모든 날짜 정보를 올바르게 기입하세요.";
			new Alert(systemOverview, head, body);
		} else {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int term = Integer.parseInt(str_term);
			String unitOfTerm = dateBox.getSelectionModel().getSelectedItem();
			
			try {
				Date d = format.parse(time);
				Calendar cal = Calendar.getInstance();
				cal.setTime(d);
				String expTime = "";
				if (unitOfTerm.equals("시간")) {
					cal.add(Calendar.HOUR, term);
					expTime = format.format(cal.getTimeInMillis());
				} else if (unitOfTerm.equals("일")) {
					cal.add(Calendar.DATE, term);
					expTime = format.format(cal.getTimeInMillis());
				} else if (unitOfTerm.equals("월")) {
					cal.add(Calendar.MONTH, term);
					expTime = format.format(cal.getTimeInMillis());

				} else if (unitOfTerm.equals("년")) {
					cal.add(Calendar.YEAR, term);
					expTime = format.format(cal.getTimeInMillis());
				}
				product_e_year_TextField.setText(expTime.substring(0, 4));
				product_e_month_TextField.setText(expTime.substring(5, 7));
				product_e_day_TextField.setText(expTime.substring(8, 10));
				product_e_hour_TextField.setText(expTime.substring(11, 13));
				product_e_minute_TextField.setText(expTime.substring(14, 16));
				product_e_second_TextField.setText(expTime.substring(17, 19));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}
	}

	@FXML
	public void handleGetCurrentTime() {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date current = new Date(System.currentTimeMillis());
		Calendar cal = Calendar.getInstance();
		cal.setTime(current);
		String prodTime = format.format(cal.getTimeInMillis());
		product_p_year_TextField.setText(prodTime.substring(0, 4));
		product_p_month_TextField.setText(prodTime.substring(5, 7));
		product_p_day_TextField.setText(prodTime.substring(8, 10));
		product_p_hour_TextField.setText(prodTime.substring(11, 13));
		product_p_minute_TextField.setText(prodTime.substring(14, 16));
		product_p_second_TextField.setText(prodTime.substring(17, 19));

		String str_term = termTextField.getText();

		if (!str_term.equals("")) {
			int term = Integer.parseInt(str_term);
			String unitOfTerm = dateBox.getSelectionModel().getSelectedItem();
			String expTime = "";
			if (unitOfTerm.equals("Hour")) {
				cal.add(Calendar.HOUR, term);
				expTime = format.format(cal.getTimeInMillis());

			} else if (unitOfTerm.equals("Day")) {
				cal.add(Calendar.DATE, term);
				expTime = format.format(cal.getTimeInMillis());

			} else if (unitOfTerm.equals("Month")) {
				cal.add(Calendar.MONTH, term);
				expTime = format.format(cal.getTimeInMillis());

			} else if (unitOfTerm.equals("Year")) {
				cal.add(Calendar.YEAR, term);
				expTime = format.format(cal.getTimeInMillis());

			}
			product_e_year_TextField.setText(expTime.substring(0, 4));
			product_e_month_TextField.setText(expTime.substring(5, 7));
			product_e_day_TextField.setText(expTime.substring(8, 10));
			product_e_hour_TextField.setText(expTime.substring(11, 13));
			product_e_minute_TextField.setText(expTime.substring(14, 16));
			product_e_second_TextField.setText(expTime.substring(17, 19));
		}
	}

	public void handleQRGenerate(List<JSONObject> plist) {
		QRMaker qrMaker = new QRMaker(300, 300);
		String fileName;
		String pid;
		String prodName;
		String productionDate;
		String expirationDate;
		String input;
		String filePath;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd:HHmmss", Locale.KOREA);
		SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
		Date current = new Date(System.currentTimeMillis());
		Calendar cal = Calendar.getInstance();
		cal.setTime(current);
		String prodTime = format.format(cal.getTimeInMillis()).replace(":", "T");
		String prodTime2 = format2.format(cal.getTimeInMillis());
		int i = 1;
		for (JSONObject p : plist) {
			pid = AES.encrypt(((String) p.get("PID")));
			prodName = (String) p.get("prodName");
			productionDate = (String) p.get("production date");
			expirationDate = (String) p.get("expiration date");
			input = "http://www.godqr.com/track.do.web?&pid=" + pid + "&prodName=" + prodName
					+ "&productionDate=" + productionDate + "&expirationDate=" + expirationDate;
			fileName = prodTime + "_" + prodName + "_" + i;
			filePath = prodName + "\\" + prodTime2;
			qrMaker.makeQR(fileName, input, filePath);
			i++;
		}
	}

	public void showProductInfoDialog(List<Product> prodList) {
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
			controller.setDialogStage(dialogStage);
			controller.setProduct(prodList);
			controller.setMain(mainApp);
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
			controller.setMain(mainApp);
			controller.setDialogStage(dialogStage);
			controller.setNDList(PDB.getNDList());
			controller.setNList(PDB.getNList());
			controller.setBarChart();
			controller.setLineChart("Month");
			controller.setStackChart("Month");
			dialogStage.showAndWait();
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
