package com.msnet.util;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Alert {
		public Alert(AnchorPane pane, String head, String body) {
			JFXAlert alert = new JFXAlert((Stage) pane.getScene().getWindow());
			alert.initModality(Modality.APPLICATION_MODAL);
	        alert.setOverlayClose(true);
	        JFXDialogLayout layout = new JFXDialogLayout();
	        layout.setHeading(new Label(head));
	        layout.setBody(new Label(body));
	        JFXButton closeButton = new JFXButton("ACCEPT");
	        closeButton.getStyleClass().add("dialog-accept");
	        closeButton.setOnAction(event -> alert.hideWithAnimation());
	        layout.setActions(closeButton);
	        alert.setContent(layout);
	        alert.show();
		}
}
