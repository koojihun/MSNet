package com.msnet.view;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;

import com.jfoenix.controls.JFXComboBox;
import com.msnet.MainApp;
import com.msnet.model.NBox;
import com.msnet.model.NDBox;
import com.msnet.model.Product;
import com.msnet.model.Reservation;
import com.msnet.util.PDB;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ChartDialogController implements Initializable {
	private MainApp mainApp;
	private Stage dialogStage;
	private ObservableList<String> dateList;
	@FXML
	private TabPane pane;
	@FXML
	private BarChart<String, Integer> barChart;
	@FXML
	private CategoryAxis bar_xAxis;

	@FXML
	private LineChart<String, Integer> lineChart;
	@FXML
	private CategoryAxis line_xAxis;
	@FXML
	private JFXComboBox<String> line_dateBox;

	@FXML
	private StackedBarChart<String, Integer> stackChart;
	@FXML
	private CategoryAxis stack_xAxis;
	@FXML
	private JFXComboBox<String> stack_dateBox;

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

	private ObservableList<NDBox> ndList = FXCollections.observableArrayList();
	private ObservableList<NBox> nList = FXCollections.observableArrayList();
	private ObservableList<String> nameList = FXCollections.observableArrayList();
	private ObservableList<Reservation> completedRList = FXCollections.observableArrayList();

	public double xOffset = 0;
	public double yOffset = 0;

	public ChartDialogController() {
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		r_timeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
		r_companyColumn.setCellValueFactory(cellData -> cellData.getValue().toCompanyProperty());
		r_productionDateColumn.setCellValueFactory(cellData -> cellData.getValue().productionDateProperty());
		r_expirationDateColumn.setCellValueFactory(cellData -> cellData.getValue().expirationDateProperty());
		r_productNameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
		r_quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
		r_successColumn.setCellValueFactory(cellData -> cellData.getValue().successProperty().asObject());

		// 프로그램이 실행될 때 reservation.dat에 저장된 reservation 데이터를 읽어서 rList에 추가
		reservationStatusTableView.setItems(PDB.getCompletedRList());

		reservationStatusTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() >= 2) {
					Reservation selectedReservation = reservationStatusTableView.getSelectionModel().getSelectedItem();
					showProductInfoDialog(selectedReservation.getProductList());
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

		dateList = FXCollections.observableArrayList("Year", "Month", "Day", "Hour");
		stack_dateBox.setItems(dateList);
		line_dateBox.setItems(dateList);
		stack_dateBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				setStackChart(newValue);
			}
		});
		line_dateBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				setLineChart(newValue);
			}
		});
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

	public void setNDList(ObservableList<NDBox> ndList) {
		this.ndList = ndList;
	}

	public void setNList(ObservableList<NBox> nList) {
		this.nList = nList;
	}

	public void setBarChart() {
		for (NBox nBox : this.nList) {
			nameList.add(nBox.getProductName());
		}
		bar_xAxis.setCategories(nameList);

		XYChart.Series<String, Integer> seriesQuantity = new XYChart.Series<>();
		XYChart.Series<String, Integer> seriesAvailable = new XYChart.Series<>();
		seriesQuantity.setName("Quantity");
		seriesAvailable.setName("Available");

		for (int i = 0; i < nameList.size(); i++) {
			seriesQuantity.getData().add(new XYChart.Data<>(nameList.get(i), this.nList.get(i).getQuantity()));
			seriesAvailable.getData().add(new XYChart.Data<>(nameList.get(i), this.nList.get(i).getAvailable()));
		}

		barChart.getData().add(seriesQuantity);
		barChart.getData().add(seriesAvailable);
	}
	
	public void setLineChart(String unit) {

		this.completedRList = PDB.getCompletedRList();
		Map<String, Integer> by_Date = completedRList.stream()
				.collect(Collectors.groupingBy(r -> r.getDateByUnit(unit), Collectors.summingInt(r -> r.getSuccess())));

		Series<String, Integer> seriesSuccess = new XYChart.Series<>();
		seriesSuccess.setName("Success");
		for (String key : by_Date.keySet()) {
			seriesSuccess.getData().add(new XYChart.Data<String, Integer>(key, by_Date.get(key)));
		}
		Collections.sort(line_xAxis.getCategories());
		lineChart.getData().clear();
		lineChart.getData().add(seriesSuccess);
	}
	
	public void setStackChart(String unit) {
		stackChart.getData().clear();
		this.completedRList = PDB.getCompletedRList();
		Map<String, Map<String, Integer>> by_DateProdName = completedRList.stream()
				.collect(Collectors.groupingBy(r -> r.getProductName(),
						Collectors.groupingBy(r -> r.getDateByUnit(unit), Collectors.summingInt(r -> r.getSuccess()))));
		Series<String, Integer> seriesSuccess;
		for (String key : by_DateProdName.keySet()) {
			seriesSuccess = new XYChart.Series<>();
			seriesSuccess.setName(key);
			for (String innerKey : by_DateProdName.get(key).keySet()) {
				seriesSuccess.getData()
						.add(new XYChart.Data<String, Integer>(innerKey, by_DateProdName.get(key).get(innerKey)));
			}
			stackChart.getData().add(seriesSuccess);
		}
	}

	public void setMain(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@FXML
	public void handleClose() {
		dialogStage.close();
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
		dialogStage.setResizable(false);
		this.dialogStage.initStyle(StageStyle.UNDECORATED); // 제목표시줄 안보이게 하기 위한 작업
	}

}