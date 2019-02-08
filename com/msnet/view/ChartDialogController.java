package com.msnet.view;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.msnet.model.NBox;
import com.msnet.model.NDBox;
import com.msnet.model.Reservation;
import com.msnet.util.PDB;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

public class ChartDialogController {
	@FXML
	private BarChart<String, Integer> barChart;
	@FXML
	private CategoryAxis bar_xAxis;
	
	@FXML
	private LineChart<String, Integer> lineChart;
	@FXML
	private CategoryAxis line_xAxis;

	@FXML
	private StackedBarChart<String, Integer> stackChart;
	@FXML
	private CategoryAxis stack_xAxis;
	
	private ObservableList<NDBox> ndList = FXCollections.observableArrayList();
	private ObservableList<NBox> nList = FXCollections.observableArrayList();
	private ObservableList<String> nameList = FXCollections.observableArrayList();
	private ObservableList<Reservation> completedRList = FXCollections.observableArrayList();

	public ChartDialogController() {
	}

	public void setNDList(ObservableList<NDBox> ndList) {
		this.ndList = ndList;
	}

	public void setNList(ObservableList<NBox> nList) {
		this.nList = nList;
		for (NBox nBox : nList) {
			nameList.add(nBox.getProductName());
		}
		bar_xAxis.setCategories(nameList);

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
	
	public void setLineChart() {
		
		this.completedRList = PDB.getComplitedRList();
		Map<String, Integer> by_YearMonth = completedRList.stream().collect(Collectors.groupingBy(r -> r.getYEARMONTH(), Collectors.summingInt(r -> r.getSuccess())));
		
		Series<String, Integer> seriesSuccess = new XYChart.Series<>();
		seriesSuccess.setName("Success");
		for (String key : by_YearMonth.keySet()) {
			seriesSuccess.getData().add(new XYChart.Data<String, Integer>(key, by_YearMonth.get(key)));
		}		
		Collections.sort(line_xAxis.getCategories());
		lineChart.getData().add(seriesSuccess);		
	}
	
	public void setStackChart() {
		this.completedRList = PDB.getComplitedRList();
		Map<String, Map<String, Integer>> by_DateProdName = completedRList.stream().collect(Collectors.groupingBy(r -> r.getProductName(),
				Collectors.groupingBy(r -> r.getYEARMONTH(), Collectors.summingInt(r -> r.getSuccess()))));
	 	Series<String, Integer> seriesSuccess;
		for (String key : by_DateProdName.keySet()) {
			seriesSuccess = new XYChart.Series<>();
			seriesSuccess.setName(key);
			for (String innerKey : by_DateProdName.get(key).keySet()) {
				seriesSuccess.getData().add(new XYChart.Data<String, Integer>(innerKey, by_DateProdName.get(key).get(innerKey)));
			}
			stackChart.getData().add(seriesSuccess);
		} 	
	}
}