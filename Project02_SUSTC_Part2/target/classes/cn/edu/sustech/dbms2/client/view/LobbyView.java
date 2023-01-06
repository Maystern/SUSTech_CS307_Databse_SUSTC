package cn.edu.sustech.dbms2.client.view;

import java.io.IOException;
import java.util.ArrayList;

import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXDialog.DialogTransition;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;

import cn.edu.sustech.dbms2.client.DBClient;
import cn.edu.sustech.dbms2.client.ThrowableHandler;
import cn.edu.sustech.dbms2.client.packet.client.LoginPacket;
import cn.edu.sustech.dbms2.client.packet.server.LoginInfoPacket;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LobbyView {
	
	private Stage stage;
	
	public LobbyView() {
		
	}
	
	public void initStage() {
		stage = new Stage();
		stage.setMaxHeight(600);
		stage.setMaxWidth(900);
		stage.setTitle("数据库管理系统");
		stage.setResizable(false);
		
		StackPane pane = new StackPane();
		pane.setMaxHeight(450);
		pane.setMaxWidth(750);
		pane.setStyle("-fx-background-color:WHITESMOKE");
		JFXDepthManager.setDepth(pane, 4);
		
		VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.setSpacing(50);
		
		Label title = new Label("数据库管理系统");
		title.setFont(Font.font(25));
		vbox.getChildren().add(title);
		
		VBox textBox = new VBox();
		textBox.setSpacing(30);
		textBox.setAlignment(Pos.CENTER);
		
		HBox accountBox = new HBox();
		Label label = new Label("账号:  ");
		label.setFont(Font.font(18));
		
		JFXTextField account = new JFXTextField();
		account.setFont(Font.font(18));
		account.setValidators(new RequiredFieldValidator("账号不能为空"));
		accountBox.getChildren().add(label);
		accountBox.getChildren().add(account);
		accountBox.setAlignment(Pos.CENTER);
		textBox.getChildren().add(accountBox);
		
		HBox passwordBox = new HBox();
		Label passwordLabel = new Label("密码:  ");
		passwordLabel.setFont(Font.font(18));
		JFXPasswordField password = new JFXPasswordField();
		password.setText("");
		password.setFont(Font.font(18));
		password.setValidators(new RequiredFieldValidator("密码不能为空"));
		passwordBox.getChildren().add(passwordLabel);
		passwordBox.getChildren().add(password);
		passwordBox.setAlignment(Pos.CENTER);
		ArrayList<ValidatorBase> list = new ArrayList<>();
		list.addAll(account.getValidators());
		list.addAll(password.getValidators());
		for (ValidatorBase b : list) {
			FontIcon triangle = new FontIcon();
			
			triangle.setIconLiteral("fas-exclamation-triangle");
			b.setIcon(triangle);
		}
		
		textBox.getChildren().add(passwordBox);
		vbox.getChildren().add(textBox);
		
		HBox buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setSpacing(20);
		
		
		
		
		JFXButton confirm = new JFXButton("登录");
		confirm.setFont(Font.font(18));
		confirm.setPrefHeight(30);
		confirm.setPrefWidth(80);
		confirm.setOnAction(e -> {
			if (account.validate() && password.validate()) {
				JFXDialog dialog = new JFXDialog();
				dialog.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
				dialog.setTransitionType(DialogTransition.CENTER);
				JFXDialogLayout layout = new JFXDialogLayout();
				layout.setHeading(new Label("登录中"));
				layout.setBody(new Label("正在登录...."));
				JFXButton closeButton = new JFXButton("取消");
				closeButton.setDisable(true);
				closeButton.setStyle("-fx-text-fill: #FF0000");
				closeButton.setOnAction(event -> {
					dialog.close();
				});
				layout.setActions(closeButton);
				closeButton.getStyleClass().add("dialog-accept");
				dialog.setContent(layout);
				dialog.setDialogContainer(pane);
				dialog.show();
				DBClient client = new DBClient();
				try {
					LoginInfoPacket packet = (LoginInfoPacket) client.sendAndReceivePacket(new LoginPacket(account.getText(), password.getText()));
					if (packet.isSuccess()) {
						((Label) layout.getHeading().get(0)).setText("登录");
						((Label) layout.getBody().get(0)).setText("登录成功！");
						closeButton.setText("确认");
						closeButton.setStyle("-fx-text-fill: #03A9F4");
						closeButton.setDisable(false);
						this.getStage().hide();
						new UserView(packet).getStage().show();
					} else {
						((Label) layout.getHeading().get(0)).setText("错误");
						((Label) layout.getBody().get(0)).setText("登录失败，请检查账号与密码。");
						closeButton.setDisable(false);
					}
				} catch (IOException e1) {
					ThrowableHandler.handleThrowable(e1);
				}
			}
		});
		passwordBox.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
			if (ev.getCode() == KeyCode.ENTER) {
				confirm.fire();
				ev.consume();
			}
		});
		buttonBox.getChildren().add(confirm);
		vbox.getChildren().add(buttonBox);
		vbox.getStylesheets().add(getClass().getResource("/css/general.css").toExternalForm());
		
		StackPane.setAlignment(vbox, Pos.TOP_CENTER);
		pane.getChildren().add(vbox);
		JFXDecorator dec = new JFXDecorator(stage, pane);
		ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/icon.png")));
		icon.setFitHeight(24);
		icon.setFitWidth(24);
		dec.setGraphic(icon);
		this.stage.getIcons().clear();
		this.stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
		Scene scene = new Scene(dec, 900, 600);
		stage.setScene(scene);
	}
	
	public Stage getStage() {
		return this.stage;
	}
	
}
