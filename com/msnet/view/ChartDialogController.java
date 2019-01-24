package com.msnet.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.msnet.model.NBox;
import com.msnet.model.NDBox;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;

public class ChartDialogController implements Initializable {
	@FXML
	private BarChart<String, Integer> barChart;
	@FXML
	private CategoryAxis xAxis;

	private ObservableList<NDBox> ndList = FXCollections.observableArrayList();
	private ObservableList<NBox> nList = FXCollections.observableArrayList();
	private ObservableList<String> nameList = FXCollections.observableArrayList();

	public ChartDialogController() {

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void setNDList(ObservableList<NDBox> ndList) {
		this.ndList = ndList;
	}

	public void setNList(ObservableList<NBox> nList) {
		this.nList = nList;
		for (NBox nBox : nList) {
			nameList.add(nBox.getProductName());
		}
		xAxis.setCategories(nameList);

		XYChart.Series<String, Integer> seriesQuantity = new XYChart.Series<>();
		XYChart.Series<String, Integer> seriesAvailable = new XYChart.Series<>();
		seriesQuantity.setName("Quantity");
		seriesAvailable.setName("Available");
		
		for (int i = 0; i < nameList.size(); i++) {
			seriesQuantity.getData().add(new XYChart.Data<>(nameList.get(i), nList.get(i).getQuantity()));
			seriesAvailable.getData().add(new XYChart.Data<>(nameList.get(i), nList.get(i).getAvailable()));
		}
		
		barChart.getData().add(seriesQuantity);
		barChart.getData().add(seriesAvailable);
	}
}