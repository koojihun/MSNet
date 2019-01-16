package com.msnet.view;

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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SystemOverviewController implements Initializable {
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
	private Button inventoryStatusButton;

	@FXML
	private TableView<NBox> total_inventoryStatusTableView;
	@FXML
	private TableColumn<NBox, String> total_productNameColumn;
	@FXML
	private TableColumn<NBox, Integer> total_quantityColumn;
	@FXML
	private Button addressBookButton;

	private MainApp mainApp;
	private ObservableList<NBox> nList;
	private ObservableList<NDBox> ndList;

	public SystemOverviewController() {}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		productionDateColumn.setCellValueFactory(cellData -> cellData.getValue().productionDateProperty());
		expirationDateColumn.setCellValueFactory(cellData -> cellData.getValue().expirationDateProperty());
		productNameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
		quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
		inventoryStatusTableView.setItems(ndList);

		total_productNameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
		total_quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
		total_inventoryStatusTableView.setItems(nList);

		// NDBox를 더블 클릭하면 Product 목록이 나옴
		inventoryStatusTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() >= 2) {
					NDBox selectedNDBox = inventoryStatusTableView.getSelectionModel()
							.getSelectedItem();
					showProductInfoDialog(selectedNDBox);
				}
			}
		});
		
		// NBox를 더블 클릭하면 Product 목록이 나옴
		total_inventoryStatusTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() >= 2) {
					NBox selectedNBox = total_inventoryStatusTableView.getSelectionModel()
							.getSelectedItem();
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

	/**
	 * get_current_products의 결과인 List<Map> 형태의 productList 인자로 받아서 제품명과 생성일, 유통기한을
	 * 키로 가지고, NDBox를 값으로 가지는 Map<NDKey, NDBox> 형태의 값을 반환
	 * 
	 * @param productList
	 * @return
	 */
	public Map<NDKey, NDBox> makeNDBox(List<Map> productList) {

		Map<NDKey, NDBox> result = new HashMap<NDKey, NDBox>();

		for (Map product : productList) {
			// get_current_products를 통해서 반환 받은 productList를 for문을 돌면서 Map<String[],
			// Products> 형식으로 변환
			NDKey key = new NDKey(String.valueOf(product.get("prodName")),
					String.valueOf(product.get("production date")), String.valueOf(product.get("expiration date")));
			if (result.get(key) == null) {
				// key에 대응하는 NDBox가 없을 때
				Product tmpProduct = new Product(product); // product를 Product 형식으로 변환
				ArrayList<Product> tmpList = new ArrayList<Product>(); // Products의 ArrayList<Product>에 들어갈 임시 리스트를 하나
																		// 생성
				tmpList.add(tmpProduct); // 임시 리스트에 tmpProduct 추가
				NDBox value = new NDBox(key.getProdName(), key.getProductionDate(), key.getExpirationDate(), tmpList,
						1);
				result.put(key, value);
			} else {
				// key에 대응하는 NDBox가 있을 때
				Product tmpProduct = new Product(product); // product를 Product 형식으로 변환
				NDBox resultNDBox = result.get(key);
				resultNDBox.addProduct(tmpProduct);
				resultNDBox.setQuantity(resultNDBox.getQuantity() + 1);
			}
		}
		return result;
	}

	/**
	 * makeNDBox()의 결과인 Map<NDKey, NDBox>를 인수로 받아서 같은 prodName별로 묶어서 Map<String,
	 * NBox>의 형태로 변환해줌.
	 * 
	 * @param ndboxMap
	 * @return
	 */
	public Map<String, NBox> makeNBox(Map<NDKey, NDBox> ndboxMap) {
		Map<String, NBox> result = new HashMap<String, NBox>();

		for (NDKey key : ndboxMap.keySet()) {

			String name = key.getProdName();
			if (result.get(name) != null) {
				// result에 name을 key로 갖는 값이 존재할 때
				NDBox tmpNDBox = ndboxMap.get(key);
				ArrayList<Product> tmpProductList = tmpNDBox.getProductList(); // 저장돼야 할 Product들의 리스트
				NBox resultNBox = result.get(name); // 이미 저장되어 있는 result의 Products. resultProducts에다가 tmpProductsList의
													// Product들을 저장해야 함.

				Iterator itr = tmpProductList.iterator(); // 저장돼야 할 Product들의 리스트를 iterator로 만듦

				while (itr.hasNext()) {
					// itr를 이용해서 tmpProductsList의 모든 Product를 result의 NBox에 저장.
					Product p = (Product) itr.next();
					resultNBox.getProductList().add(p);
					resultNBox.setQuantity(resultNBox.getQuantity() + 1);
				}

			} else {
				// result에 name을 key로 갖는 값이 존재하지 않을 때
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

			// 다이얼로그 스테이지를 만든다.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Product Info");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainApp.getPrimaryStage());
			Scene scene = new Scene(productInfoPane);
			dialogStage.setScene(scene);

			// product를 컨트롤러에 설정한다.
			ProductInfoDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setProduct(ndBox);
			
			// 다이얼로그를 보여주고 사용자가 닫을 때까지 기다린다.
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

			// 다이얼로그 스테이지를 만든다.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Product Info");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainApp.getPrimaryStage());
			Scene scene = new Scene(productInfoPane);
			dialogStage.setScene(scene);
			
			// product를 컨트롤러에 설정한다.
			ProductInfoDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setProduct(nBox);
			// 다이얼로그를 보여주고 사용자가 닫을 때까지 기다린다.
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
			
			// 다이얼로그 스테이지를 만든다.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Address Book");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainApp.getPrimaryStage());
			Scene scene = new Scene(addressBookPane);
			dialogStage.setScene(scene);
			
			AddressBookDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

}
