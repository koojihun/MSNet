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

		// NDBox�� ���� Ŭ���ϸ� Product ����� ����
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
		
		// NBox�� ���� Ŭ���ϸ� Product ����� ����
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
	 * get_current_products�� ����� List<Map> ������ productList ���ڷ� �޾Ƽ� ��ǰ��� ������, ���������
	 * Ű�� ������, NDBox�� ������ ������ Map<NDKey, NDBox> ������ ���� ��ȯ
	 * 
	 * @param productList
	 * @return
	 */
	public Map<NDKey, NDBox> makeNDBox(List<Map> productList) {

		Map<NDKey, NDBox> result = new HashMap<NDKey, NDBox>();

		for (Map product : productList) {
			// get_current_products�� ���ؼ� ��ȯ ���� productList�� for���� ���鼭 Map<String[],
			// Products> �������� ��ȯ
			NDKey key = new NDKey(String.valueOf(product.get("prodName")),
					String.valueOf(product.get("production date")), String.valueOf(product.get("expiration date")));
			if (result.get(key) == null) {
				// key�� �����ϴ� NDBox�� ���� ��
				Product tmpProduct = new Product(product); // product�� Product �������� ��ȯ
				ArrayList<Product> tmpList = new ArrayList<Product>(); // Products�� ArrayList<Product>�� �� �ӽ� ����Ʈ�� �ϳ�
																		// ����
				tmpList.add(tmpProduct); // �ӽ� ����Ʈ�� tmpProduct �߰�
				NDBox value = new NDBox(key.getProdName(), key.getProductionDate(), key.getExpirationDate(), tmpList,
						1);
				result.put(key, value);
			} else {
				// key�� �����ϴ� NDBox�� ���� ��
				Product tmpProduct = new Product(product); // product�� Product �������� ��ȯ
				NDBox resultNDBox = result.get(key);
				resultNDBox.addProduct(tmpProduct);
				resultNDBox.setQuantity(resultNDBox.getQuantity() + 1);
			}
		}
		return result;
	}

	/**
	 * makeNDBox()�� ����� Map<NDKey, NDBox>�� �μ��� �޾Ƽ� ���� prodName���� ��� Map<String,
	 * NBox>�� ���·� ��ȯ����.
	 * 
	 * @param ndboxMap
	 * @return
	 */
	public Map<String, NBox> makeNBox(Map<NDKey, NDBox> ndboxMap) {
		Map<String, NBox> result = new HashMap<String, NBox>();

		for (NDKey key : ndboxMap.keySet()) {

			String name = key.getProdName();
			if (result.get(name) != null) {
				// result�� name�� key�� ���� ���� ������ ��
				NDBox tmpNDBox = ndboxMap.get(key);
				ArrayList<Product> tmpProductList = tmpNDBox.getProductList(); // ����ž� �� Product���� ����Ʈ
				NBox resultNBox = result.get(name); // �̹� ����Ǿ� �ִ� result�� Products. resultProducts���ٰ� tmpProductsList��
													// Product���� �����ؾ� ��.

				Iterator itr = tmpProductList.iterator(); // ����ž� �� Product���� ����Ʈ�� iterator�� ����

				while (itr.hasNext()) {
					// itr�� �̿��ؼ� tmpProductsList�� ��� Product�� result�� NBox�� ����.
					Product p = (Product) itr.next();
					resultNBox.getProductList().add(p);
					resultNBox.setQuantity(resultNBox.getQuantity() + 1);
				}

			} else {
				// result�� name�� key�� ���� ���� �������� ���� ��
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

			// ���̾�α� ���������� �����.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Product Info");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainApp.getPrimaryStage());
			Scene scene = new Scene(productInfoPane);
			dialogStage.setScene(scene);

			// product�� ��Ʈ�ѷ��� �����Ѵ�.
			ProductInfoDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setProduct(ndBox);
			
			// ���̾�α׸� �����ְ� ����ڰ� ���� ������ ��ٸ���.
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

			// ���̾�α� ���������� �����.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Product Info");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainApp.getPrimaryStage());
			Scene scene = new Scene(productInfoPane);
			dialogStage.setScene(scene);
			
			// product�� ��Ʈ�ѷ��� �����Ѵ�.
			ProductInfoDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setProduct(nBox);
			// ���̾�α׸� �����ְ� ����ڰ� ���� ������ ��ٸ���.
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
			
			// ���̾�α� ���������� �����.
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
