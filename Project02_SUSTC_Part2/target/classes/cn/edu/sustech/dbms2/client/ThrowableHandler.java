package cn.edu.sustech.dbms2.client;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.effects.JFXDepthManager;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public final class ThrowableHandler {
	private ThrowableHandler() {throw new NullPointerException("You can not initiallize this");}
	
	public static void handleThrowable(Throwable t) {
		Platform.runLater(() -> {
		int code = 000;
		
		t.printStackTrace();
		
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		
			
		VBox vbox = new VBox();
		Label title = new Label("出现预料之外的错误");
		FontIcon alert = new FontIcon();
		alert.setIconLiteral("fas-exclamation-triangle");
		alert.setIconSize(28);
		title.setGraphic(alert);
		title.setFont(Font.font(28));
		
		JFXDecorator dec = new JFXDecorator(stage, vbox);
		ImageView icon = new ImageView(new Image(ThrowableHandler.class.getResourceAsStream("/alert.png")));
		icon.setFitHeight(24);
		icon.setFitWidth(24);
		dec.setGraphic(icon);
		
		Label codeLabel = new Label("错误代码: " + code);
		codeLabel.setFont(Font.font(18));
		
		Scene scene = new Scene(dec, 400, 400);
		stage.setTitle("出错");
		
		vbox.setSpacing(20);
		vbox.setPadding(new Insets(30));
		vbox.getChildren().addAll(title, codeLabel);
		
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		scrollPane.setPrefHeight(10000);
		JFXDepthManager.setDepth(scrollPane, 2);
		StringWriter writer = new StringWriter();
		t.printStackTrace(new PrintWriter(writer, true));
		JFXTextArea message = new JFXTextArea(writer.toString());
		message.setEditable(false);
		scrollPane.setContent(message);
		vbox.getChildren().add(scrollPane);
		
		scene.getStylesheets().add(ThrowableHandler.class.getResource("/css/general.css").toExternalForm());
		scene.getStylesheets().add(ThrowableHandler.class.getResource("/css/scrollpane.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
		});
	}
	
}
