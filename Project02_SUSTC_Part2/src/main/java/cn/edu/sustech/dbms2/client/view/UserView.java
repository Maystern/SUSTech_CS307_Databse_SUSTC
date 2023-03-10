package cn.edu.sustech.dbms2.client.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXDialog.DialogTransition;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;

import cn.edu.sustech.dbms2.client.DBClient;
import cn.edu.sustech.dbms2.client.Main;
import cn.edu.sustech.dbms2.client.packet.client.CityCountPacket;
import cn.edu.sustech.dbms2.client.packet.client.CompanyCountPacket;
import cn.edu.sustech.dbms2.client.packet.client.ContainerPacket;
import cn.edu.sustech.dbms2.client.packet.client.CourierCountPacket;
import cn.edu.sustech.dbms2.client.packet.client.ExportTaxRatePacket;
import cn.edu.sustech.dbms2.client.packet.client.GetAllItemsAtPortPacket;
import cn.edu.sustech.dbms2.client.packet.client.ImportTaxRatePacket;
import cn.edu.sustech.dbms2.client.packet.client.ItemPacket;
import cn.edu.sustech.dbms2.client.packet.client.ItemWaitForCheckingPacket;
import cn.edu.sustech.dbms2.client.packet.client.LoadContainerToShipPacket;
import cn.edu.sustech.dbms2.client.packet.client.LoadItemToContainerPacket;
import cn.edu.sustech.dbms2.client.packet.client.LogoutPacket;
import cn.edu.sustech.dbms2.client.packet.client.NewItemPacket;
import cn.edu.sustech.dbms2.client.packet.client.SetItemCheckStatePacket;
import cn.edu.sustech.dbms2.client.packet.client.SetItemStatePacket;
import cn.edu.sustech.dbms2.client.packet.client.ShipCountPacket;
import cn.edu.sustech.dbms2.client.packet.client.ShipPacket;
import cn.edu.sustech.dbms2.client.packet.client.StaffPacket;
import cn.edu.sustech.dbms2.client.packet.client.StartShipSailingPacket;
import cn.edu.sustech.dbms2.client.packet.client.UnloadItemPacket;
import cn.edu.sustech.dbms2.client.packet.server.CityCountInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.CompanyCountInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ContainerInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.CourierCountInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ExportTaxRateInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.GetAllItemsAtPortInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ImportTaxRateInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ItemInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ItemWaitForCheckingInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.LoadContainerToShipInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.LoadItemToContainerInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.LoginInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.NewItemInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.SetItemCheckStateInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.SetItemStateInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ShipCountInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ShipInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.StaffInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.StartShipSailingInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.UnloadItemInfoPacket;
import cn.edu.sustech.dbms2.client.interfaces.ContainerInfo;
import cn.edu.sustech.dbms2.client.interfaces.ItemInfo;
import cn.edu.sustech.dbms2.client.interfaces.ShipInfo;
import cn.edu.sustech.dbms2.client.interfaces.StaffInfo;
import cn.edu.sustech.dbms2.client.interfaces.ItemInfo.ImportExportInfo;
import cn.edu.sustech.dbms2.client.interfaces.ItemInfo.RetrievalDeliveryInfo;
import cn.edu.sustech.dbms2.client.interfaces.ItemState;
import cn.edu.sustech.dbms2.client.interfaces.ContainerInfo.Type;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class UserView {

	private Stage stage;
	private String user;
	private String staffType;
	private String company;
	private String city;
	private String phone;
	private int age;
	private boolean gender;
	private String cookie;

	public UserView(LoginInfoPacket infoPacket) {
		this.user = infoPacket.getUserName();
		this.company = infoPacket.getCompany();
		this.city = infoPacket.getCity();
		this.phone = infoPacket.getPhoneNumber();
		this.age = infoPacket.getAge();
		this.gender = infoPacket.getGender();
		this.cookie = infoPacket.getCookie();
		this.staffType = staffTypeStringMap.get(infoPacket.getStaffType());
		this.initPane();
	}

	public Stage getStage() {
		return this.stage;
	}

	private void showDialog(String text, StackPane pane, boolean isWarning) {
		JFXDialog dialog = new JFXDialog();
		dialog.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
		dialog.setTransitionType(DialogTransition.CENTER);
		JFXDialogLayout layout = new JFXDialogLayout();
		layout.setHeading(new Label(isWarning ? "??????" : "??????"));
		TextFlow textFlow = new TextFlow();
		Text t = new Text(text);
		textFlow.getChildren().add(t);
		layout.setBody(textFlow);
		JFXButton closeButton = new JFXButton(isWarning ? "??????" : "??????");
		closeButton.setStyle("-fx-text-fill: " + (isWarning ? "#FF0000" : "SKYBLUE"));
		closeButton.setOnAction(event -> {
			dialog.close();
		});
		layout.setActions(closeButton);
		closeButton.getStyleClass().add("dialog-accept");
		dialog.setContent(layout);
		dialog.setDialogContainer(pane);
		dialog.show();
	}

	public void initSustcManagerBoard(HBox hbox, StackPane top) {
		StackPane leftPane = new StackPane();
		leftPane.getStylesheets().add(getClass().getResource("/css/userview.css").toExternalForm());
		leftPane.setPrefHeight(550);
		leftPane.setPrefWidth(590);
		leftPane.setStyle("-fx-background-color: WHITE");
		leftPane.setPadding(new Insets(30, 0, 0, 30));
		JFXDepthManager.setDepth(leftPane, 4);
		hbox.getChildren().add(leftPane);

		HBox leftHBox = new HBox();
		leftHBox.setPrefHeight(500);
		leftHBox.setPrefWidth(270);

		VBox leftBox = new VBox();
		leftBox.setPrefWidth(270);
		leftBox.setPrefHeight(500);
		leftBox.setSpacing(25);
		leftHBox.getChildren().add(leftBox);

		VBox companyBox = new VBox();
		companyBox.setSpacing(5);
		JFXButton companyButton = new JFXButton("??????????????????");
		TextFlow companyFlow = new TextFlow();
		Text companyTitle = new Text("????????????: ");
		companyTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		Text companyCount = new Text("????????????");
		companyButton.setOnAction(e -> {
			DBClient client = new DBClient();
			try {
				CompanyCountInfoPacket receive = (CompanyCountInfoPacket) client
						.sendAndReceivePacket(new CompanyCountPacket(this.cookie));
				int count = receive.getCount();
				if (count == -1) {
					showDialog("????????????", top, true);
				} else {
					companyCount.setText("" + count);
					showDialog("???????????????????????????", top, false);
				}
			} catch (Exception e1) {
				showDialog("????????????", top, true);
			}
		});
		companyCount.setFont(Font.font(14));
		companyFlow.getChildren().addAll(companyTitle, companyCount);
		companyBox.getChildren().addAll(companyButton, companyFlow);
		leftBox.getChildren().add(companyBox);

		VBox cityBox = new VBox();
		cityBox.setSpacing(5);
		JFXButton cityButton = new JFXButton("??????????????????");
		TextFlow cityFlow = new TextFlow();
		Text cityTitle = new Text("????????????: ");
		cityTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		Text cityCount = new Text("????????????");
		cityButton.setOnAction(e -> {
			DBClient client = new DBClient();
			try {
				CityCountInfoPacket receive = (CityCountInfoPacket) client
						.sendAndReceivePacket(new CityCountPacket(this.cookie));
				int count = receive.getCount();
				if (count == -1) {
					showDialog("????????????", top, true);
				} else {
					cityCount.setText("" + count);
					showDialog("???????????????????????????", top, false);
				}
			} catch (Exception e1) {
				showDialog("????????????", top, true);
			}
		});
		cityCount.setFont(Font.font(14));
		cityFlow.getChildren().addAll(cityTitle, cityCount);
		cityBox.getChildren().addAll(cityButton, cityFlow);
		leftBox.getChildren().add(cityBox);

		VBox courierBox = new VBox();
		courierBox.setSpacing(5);
		JFXButton courierButton = new JFXButton("?????????????????????");
		TextFlow courierFlow = new TextFlow();
		Text courierTitle = new Text("???????????????: ");
		courierTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		Text courierCount = new Text("????????????");
		courierButton.setOnAction(e -> {
			DBClient client = new DBClient();
			try {
				CourierCountInfoPacket receive = (CourierCountInfoPacket) client
						.sendAndReceivePacket(new CourierCountPacket(this.cookie));
				int count = receive.getCount();
				if (count == -1) {
					showDialog("????????????", top, true);
				} else {
					courierCount.setText("" + count);
					showDialog("??????????????????????????????", top, false);
				}
			} catch (Exception e1) {
				showDialog("????????????", top, true);
			}
		});
		courierCount.setFont(Font.font(14));
		courierFlow.getChildren().addAll(courierTitle, courierCount);
		courierBox.getChildren().addAll(courierButton, courierFlow);
		leftBox.getChildren().add(courierBox);

		VBox shipBox = new VBox();
		shipBox.setSpacing(5);
		JFXButton shipButton = new JFXButton("??????????????????");
		TextFlow shipFlow = new TextFlow();
		Text shipTitle = new Text("????????????: ");
		shipTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		Text shipCount = new Text("????????????");
		shipButton.setOnAction(e -> {
			DBClient client = new DBClient();
			try {
				ShipCountInfoPacket receive = (ShipCountInfoPacket) client
						.sendAndReceivePacket(new ShipCountPacket(this.cookie));
				int count = receive.getCount();
				if (count == -1) {
					showDialog("????????????", top, true);
				} else {
					shipCount.setText("" + count);
					showDialog("???????????????????????????", top, false);
				}
			} catch (Exception e1) {
				showDialog("????????????", top, true);
			}
		});
		shipCount.setFont(Font.font(14));
		shipFlow.getChildren().addAll(shipTitle, shipCount);
		shipBox.getChildren().addAll(shipButton, shipFlow);
		leftBox.getChildren().add(shipBox);

		VBox containerInfoBox = new VBox();
		containerInfoBox.setSpacing(5);
		JFXButton containerInfoButton = new JFXButton("??????????????????");
		HBox containerHBox = new HBox();
		TextFlow containerInfoFlow = new TextFlow();
		Text containerInfoTitle = new Text("????????????: ");
		containerInfoTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14");

		JFXTextField codeTextField = new JFXTextField();
		ValidatorBase codeValid = new RequiredFieldValidator("????????????????????????");
		FontIcon codeTri = new FontIcon();
		codeTri.setIconLiteral("fas-exclamation-triangle");
		codeValid.setIcon(codeTri);
		codeTextField.setValidators(codeValid);

		containerInfoButton.setOnAction(e -> {
			DBClient client = new DBClient();
			try {
				if (codeTextField.validate()) {
					ContainerInfoPacket receive = (ContainerInfoPacket) client
							.sendAndReceivePacket(new ContainerPacket(this.cookie, codeTextField.getText()));
					ContainerInfo info = receive.getInfo();
					if (info == null) {
						showDialog("????????????", top, true);
					} else {
						showInfo(info, top);
					}
				}
			} catch (Exception e1) {
				showDialog("????????????", top, true);
			}
		});
		containerInfoFlow.getChildren().add(containerInfoTitle);
		containerHBox.getChildren().addAll(containerInfoFlow, codeTextField);
		containerInfoBox.getChildren().addAll(containerInfoButton, containerHBox);

		leftBox.getChildren().add(containerInfoBox);

		VBox shipInfoBox = new VBox();
		shipInfoBox.setSpacing(5);
		JFXButton shipInfoButton = new JFXButton("??????????????????");
		HBox shipHBox = new HBox();
		TextFlow shipInfoFlow = new TextFlow();
		Text shipInfoTitle = new Text("????????????: ");
		shipInfoTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14");

		JFXTextField shipNameTextField = new JFXTextField();
		ValidatorBase shipNameValid = new RequiredFieldValidator("????????????????????????");
		FontIcon shipNameTri = new FontIcon();
		shipNameTri.setIconLiteral("fas-exclamation-triangle");
		shipNameValid.setIcon(shipNameTri);
		shipNameTextField.setValidators(shipNameValid);

		shipInfoButton.setOnAction(e -> {
			DBClient client = new DBClient();
			try {
				if (shipNameTextField.validate()) {
					ShipInfoPacket receive = (ShipInfoPacket) client
							.sendAndReceivePacket(new ShipPacket(this.cookie, shipNameTextField.getText()));
					ShipInfo info = receive.getInfo();
					if (info == null) {
						showDialog("????????????", top, true);
					} else {
						showInfo(info, top);
					}
				}
			} catch (Exception e1) {
				showDialog("????????????", top, true);
			}
		});
		shipInfoFlow.getChildren().add(shipInfoTitle);
		shipHBox.getChildren().addAll(shipInfoFlow, shipNameTextField);
		shipInfoBox.getChildren().addAll(shipInfoButton, shipHBox);

		leftBox.getChildren().add(shipInfoBox);

		VBox leftBox2 = new VBox();
		leftBox2.setPrefWidth(270);
		leftBox2.setPrefHeight(500);
		leftBox2.setSpacing(25);
		leftHBox.getChildren().add(leftBox2);

		VBox itemInfoBox = new VBox();
		itemInfoBox.setSpacing(5);
		JFXButton itemInfoButton = new JFXButton("??????????????????");
		HBox itemHBox = new HBox();
		TextFlow itemInfoFlow = new TextFlow();
		Text itemInfoTitle = new Text("????????????: ");
		itemInfoTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14");

		JFXTextField itemNameTextField = new JFXTextField();
		ValidatorBase itemNameValid = new RequiredFieldValidator("????????????????????????");
		FontIcon itemNameTri = new FontIcon();
		itemNameTri.setIconLiteral("fas-exclamation-triangle");
		itemNameValid.setIcon(itemNameTri);
		itemNameTextField.setValidators(itemNameValid);

		itemInfoButton.setOnAction(e -> {
			DBClient client = new DBClient();
			try {
				if (itemNameTextField.validate()) {
					ItemInfoPacket receive = (ItemInfoPacket) client
							.sendAndReceivePacket(new ItemPacket(this.cookie, itemNameTextField.getText()));
					ItemInfo info = receive.getInfo();
					if (info == null) {
						showDialog("????????????", top, true);
					} else {
						showInfo(info, top);
					}
				}
			} catch (Exception e1) {
				showDialog("????????????", top, true);
			}
		});

		itemInfoFlow.getChildren().add(itemInfoTitle);
		itemHBox.getChildren().addAll(itemInfoFlow, itemNameTextField);
		itemInfoBox.getChildren().addAll(itemInfoButton, itemHBox);

		leftBox2.getChildren().add(itemInfoBox);

		VBox staffInfoBox = new VBox();
		staffInfoBox.setSpacing(5);
		JFXButton staffInfoButton = new JFXButton("??????????????????");
		HBox staffHBox = new HBox();
		TextFlow staffInfoFlow = new TextFlow();
		Text staffInfoTitle = new Text("????????????: ");
		staffInfoTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14");

		JFXTextField staffNameTextField = new JFXTextField();
		ValidatorBase staffNameValid = new RequiredFieldValidator("????????????????????????");
		FontIcon staffNameTri = new FontIcon();
		staffNameTri.setIconLiteral("fas-exclamation-triangle");
		staffNameValid.setIcon(staffNameTri);
		staffNameTextField.setValidators(staffNameValid);

		staffInfoButton.setOnAction(e -> {
			DBClient client = new DBClient();
			try {
				if (staffNameTextField.validate()) {
					StaffInfoPacket receive = (StaffInfoPacket) client
							.sendAndReceivePacket(new StaffPacket(this.cookie, staffNameTextField.getText()));
					StaffInfo info = receive.getInfo();
					if (info == null) {
						showDialog("????????????", top, true);
					} else {
						showInfo(info, top);
					}
				}
			} catch (Exception e1) {
				showDialog("????????????", top, true);
			}
		});

		staffInfoFlow.getChildren().add(staffInfoTitle);
		staffHBox.getChildren().addAll(staffInfoFlow, staffNameTextField);
		staffInfoBox.getChildren().addAll(staffInfoButton, staffHBox);

		leftBox2.getChildren().add(staffInfoBox);

		leftPane.getChildren().add(leftHBox);
	}

	private static final Map<String, String> stringStaffTypeMap = new HashMap<>();
	private static final Map<String, String> staffTypeStringMap = new HashMap<>();

	static {
		stringStaffTypeMap.put("Courier", "Courier");
		stringStaffTypeMap.put("SUSTC Department Manager", "SustcManager");
		stringStaffTypeMap.put("Company Manager", "CompanyManager");
		stringStaffTypeMap.put("Seaport Officer", "SeaportOfficer");

		for (Map.Entry<String, String> entry : stringStaffTypeMap.entrySet()) {
			staffTypeStringMap.put(entry.getValue(), entry.getKey());
		}

	}

	public void showInfo(Record record, StackPane pane) {
		JFXDialog dialog = new JFXDialog();
		dialog.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
		dialog.setTransitionType(DialogTransition.CENTER);
		JFXDialogLayout layout = new JFXDialogLayout();

		String titles[] = null;
		String texts[] = null;

		if (record instanceof ContainerInfo) {
			ContainerInfo info = (ContainerInfo) record;
			layout.setHeading(new Label("????????????"));

			String ctitles[] = { "????????????", "????????????", "????????????" };
			String ctexts[] = { info.code(), info.type().toString(), info.using() ? "?????????" : "??????" };
			titles = ctitles;
			texts = ctexts;
		}
		if (record instanceof ShipInfo) {
			ShipInfo info = (ShipInfo) record;
			layout.setHeading(new Label("????????????"));

			String ctitles[] = { "????????????", "????????????", "????????????" };
			String ctexts[] = { info.name(), info.owner().toString(), info.sailing() ? "?????????" : "??????" };
			titles = ctitles;
			texts = ctexts;
		}
		if (record instanceof ItemInfo) {
			ItemInfo info = (ItemInfo) record;
			layout.setHeading(new Label("????????????"));

			String ctitles[] = { "????????????", "????????????", "????????????", "????????????", "???????????????", "????????????", "???????????????", "????????????", "????????????", "???????????????",
					"????????????", "????????????", "???????????????" };
			String ctexts[] = { info.name(), "" + String.format("%,.2f", info.price()), info.state().toString(),
					info.retrieval().city(), info.retrieval().courier(), info.delivery().city(),
					info.delivery().courier() == null ? "[??????]" : info.delivery().courier(), info.$import().city(),
					"" + String.format("%,.2f", info.$import().tax()),
					info.$import().officer() == null ? "[??????]" : info.$import().officer(), info.export().city(),
					"" + String.format("%,.2f", info.export().tax()),
					info.export().officer() == null ? "[??????]" : info.export().officer() };
			titles = ctitles;
			texts = ctexts;
		}
		if (record instanceof StaffInfo) {
			StaffInfo info = (StaffInfo) record;
			layout.setHeading(new Label("????????????"));

			String ctitles[] = { "??????", "??????", "??????", "?????????", "??????", "??????", "??????" };
			String ctexts[] = { info.name(), info.isFemale() ? "???" : "???", "" + info.age(), info.phoneNumber(),
					info.company() == null ? "???" : info.company(), staffTypeStringMap.get(info.type()),
					info.city() == null ? "???" : info.city() };
			titles = ctitles;
			texts = ctexts;
		}

		VBox vbox = new VBox();
		vbox.setPrefSize(300, 100);
		vbox.setSpacing(10);
		for (int i = 0; i < titles.length; ++i) {
			TextFlow textFlow = new TextFlow();
			Text nameTitle = new Text(titles[i] + ": ");
			nameTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			Text nameText = new Text(texts[i]);
			nameText.setFont(Font.font(14));
			textFlow.getChildren().addAll(nameTitle, nameText);
			vbox.getChildren().add(textFlow);
		}
		layout.setBody(vbox);
		JFXButton closeButton = new JFXButton("??????");
		closeButton.setStyle("-fx-text-fill: SKYBLUE");
		closeButton.setOnAction(event -> {
			dialog.close();
		});
		layout.setActions(closeButton);
		closeButton.getStyleClass().add("dialog-accept");
		dialog.setContent(layout);
		dialog.setDialogContainer(pane);
		dialog.show();
	}

	private static final Map<String, ItemState> stringStateMap = new HashMap<>();
	private static final Map<ItemState, String> stateStringMap = new HashMap<>();
	private static final List<String> stateList = new ArrayList<>();

	static {
		stringStateMap.put("Picking-up", ItemState.PickingUp);
		stringStateMap.put("To-Export Transporting", ItemState.ToExportTransporting);
		stringStateMap.put("Export Checking", ItemState.ExportChecking);
		stringStateMap.put("Export Check Fail", ItemState.ExportCheckFailed);
		stringStateMap.put("Packing to Container", ItemState.PackingToContainer);
		stringStateMap.put("Waiting for Shipping", ItemState.WaitingForShipping);
		stringStateMap.put("Shipping", ItemState.Shipping);
		stringStateMap.put("Unpacking from Container", ItemState.UnpackingFromContainer);
		stringStateMap.put("Import Checking", ItemState.ImportChecking);
		stringStateMap.put("Import Check Fail", ItemState.ImportCheckFailed);
		stringStateMap.put("From-Import Transporting", ItemState.FromImportTransporting);
		stringStateMap.put("Delivering", ItemState.Delivering);
		stringStateMap.put("Finish", ItemState.Finish);

		for (Map.Entry<String, ItemState> entry : stringStateMap.entrySet()) {
			stateStringMap.put(entry.getValue(), entry.getKey());
		}

		for (ItemState state : ItemState.values()) {
			stateList.add(stateStringMap.get(state));
		}

	}

	private static class PageHelper {

		private int maxPages;
		private int currentPage;

		private PageHelper(int pages) {
			this.maxPages = pages;
		}

		private int getCurrentPage() {
			return this.currentPage;
		}

		private int getMaxPages() {
			return this.maxPages;
		}

		private void lastPage() {
			if (this.currentPage == 0) {
				return;
			}
			--currentPage;
		}

		private void nextPage() {
			if (this.currentPage + 1 >= this.maxPages) {
				return;
			}
			++currentPage;
		}

	}

	public void initCompanyManagerBoard(HBox hbox, StackPane top) {
		StackPane leftPane = new StackPane();
		leftPane.getStylesheets().add(getClass().getResource("/css/userview.css").toExternalForm());
		leftPane.setPrefHeight(550);
		leftPane.setPrefWidth(590);
		leftPane.setStyle("-fx-background-color: WHITE");
		leftPane.setPadding(new Insets(30, 0, 0, 30));
		JFXDepthManager.setDepth(leftPane, 4);
		hbox.getChildren().add(leftPane);

		HBox leftHBox = new HBox();
		leftHBox.setPrefWidth(540);
		leftHBox.setPrefHeight(500);

		VBox leftBox = new VBox();
		leftBox.setPrefWidth(270);
		leftBox.setPrefHeight(500);
		leftBox.setSpacing(20);

		VBox importTaxVBox = new VBox();
		importTaxVBox.setSpacing(5);
		JFXButton importTaxButton = new JFXButton("??????????????????");

		HBox importTaxBox = new HBox();
		TextFlow importTax = new TextFlow();
		Text importTaxName = new Text("????????????: ");
		importTaxName.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		importTax.getChildren().add(importTaxName);
		JFXTextField importTaxCityField = new JFXTextField();
		ValidatorBase importTaxCityValid = new RequiredFieldValidator("????????????????????????");
		FontIcon importTaxTri = new FontIcon();
		importTaxTri.setIconLiteral("fas-exclamation-triangle");
		importTaxCityValid.setIcon(importTaxTri);
		importTaxCityField.setValidators(importTaxCityValid);
		importTaxBox.getChildren().addAll(importTax, importTaxCityField);

		HBox importTaxItemBox = new HBox();
		TextFlow importTaxItem = new TextFlow();
		Text importTaxItemName = new Text("????????????: ");
		importTaxItemName.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		importTaxItem.getChildren().add(importTaxItemName);
		JFXTextField importTaxItemField = new JFXTextField();
		ValidatorBase importTaxItemValid = new RequiredFieldValidator("????????????????????????");
		FontIcon importTaxItemTri = new FontIcon();
		importTaxItemTri.setIconLiteral("fas-exclamation-triangle");
		importTaxItemValid.setIcon(importTaxItemTri);
		importTaxItemField.setValidators(importTaxItemValid);
		importTaxItemBox.getChildren().addAll(importTaxItem, importTaxItemField);
		importTaxButton.setOnAction(e -> {
			if (importTaxCityField.validate() && importTaxItemField.validate()) {
				DBClient client = new DBClient();
				try {
					ImportTaxRateInfoPacket packet = (ImportTaxRateInfoPacket) client
							.sendAndReceivePacket(new ImportTaxRatePacket(this.cookie, importTaxCityField.getText(),
									importTaxItemField.getText()));
					if (Math.abs(packet.getRate() + 1) >= 1e-8) {
						showDialog("???????????? " + importTaxItemField.getText() + " ????????? " + importTaxCityField.getText()
								+ " ???????????? " + String.format("%,.2f", packet.getRate()), top, false);
					} else {
						showDialog("????????????", top, true);
					}
				} catch (IOException e1) {
					showDialog("????????????", top, true);
				}
			}
		});

		importTaxVBox.getChildren().addAll(importTaxButton, importTaxBox, importTaxItemBox);
		leftBox.getChildren().add(importTaxVBox);

		VBox exportTaxVBox = new VBox();
		exportTaxVBox.setSpacing(5);
		JFXButton exportTaxButton = new JFXButton("??????????????????");

		HBox exportTaxBox = new HBox();
		TextFlow exportTax = new TextFlow();
		Text exportTaxName = new Text("????????????: ");
		exportTaxName.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		exportTax.getChildren().add(exportTaxName);
		JFXTextField exportTaxCityField = new JFXTextField();
		ValidatorBase exportTaxCityValid = new RequiredFieldValidator("????????????????????????");
		FontIcon exportTaxTri = new FontIcon();
		exportTaxTri.setIconLiteral("fas-exclamation-triangle");
		exportTaxCityValid.setIcon(exportTaxTri);
		exportTaxCityField.setValidators(exportTaxCityValid);
		exportTaxBox.getChildren().addAll(exportTax, exportTaxCityField);

		HBox exportTaxItemBox = new HBox();
		TextFlow exportTaxItem = new TextFlow();
		Text exportTaxItemName = new Text("????????????: ");
		exportTaxItemName.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		exportTaxItem.getChildren().add(exportTaxItemName);
		JFXTextField exportTaxItemField = new JFXTextField();
		ValidatorBase exportTaxItemValid = new RequiredFieldValidator("????????????????????????");
		FontIcon exportTaxItemTri = new FontIcon();
		exportTaxItemTri.setIconLiteral("fas-exclamation-triangle");
		exportTaxItemValid.setIcon(exportTaxItemTri);
		exportTaxItemField.setValidators(exportTaxItemValid);
		exportTaxItemBox.getChildren().addAll(exportTaxItem, exportTaxItemField);
		exportTaxButton.setOnAction(e -> {
			if (exportTaxCityField.validate() && exportTaxItemField.validate()) {
				DBClient client = new DBClient();
				try {
					ExportTaxRateInfoPacket packet = (ExportTaxRateInfoPacket) client
							.sendAndReceivePacket(new ExportTaxRatePacket(this.cookie, exportTaxCityField.getText(),
									exportTaxItemField.getText()));
					if (Math.abs(packet.getRate() + 1) >= 1e-8) {
						showDialog("???????????? " + exportTaxItemField.getText() + " ????????? " + exportTaxCityField.getText()
								+ " ???????????? " + String.format("%,.2f", packet.getRate()), top, false);
					} else {
						showDialog("????????????", top, true);
					}
				} catch (IOException e1) {
					showDialog("????????????", top, true);
				}
			}
		});

		exportTaxVBox.getChildren().addAll(exportTaxButton, exportTaxBox, exportTaxItemBox);
		leftBox.getChildren().add(exportTaxVBox);

		VBox iwfVBox = new VBox();
		iwfVBox.setSpacing(5);
		JFXButton iwfButton = new JFXButton("????????????????????????");

		HBox iwfBox = new HBox();
		TextFlow iwf = new TextFlow();
		Text iwfName = new Text("????????????: ");
		iwfName.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		iwf.getChildren().add(iwfName);
		JFXTextField iwfField = new JFXTextField();
		ValidatorBase iwfValid = new RequiredFieldValidator("????????????????????????");
		FontIcon iwfTri = new FontIcon();
		iwfTri.setIconLiteral("fas-exclamation-triangle");
		iwfValid.setIcon(iwfTri);
		iwfField.setValidators(iwfValid);
		iwfBox.getChildren().addAll(iwf, iwfField);
		iwfButton.setOnAction(e -> {
			if (iwfField.validate()) {
				DBClient client = new DBClient();
				try {
					ItemWaitForCheckingInfoPacket packet = (ItemWaitForCheckingInfoPacket) client
							.sendAndReceivePacket(new ItemWaitForCheckingPacket(this.cookie, iwfField.getText()));
					if (packet.isSuccess()) {
						this.showDialog("????????????", top, false);
					} else {
						this.showDialog("????????????", top, true);
					}
				} catch (IOException e1) {
					this.showDialog("????????????", top, true);
				}

			}
		});
		iwfVBox.getChildren().addAll(iwfButton, iwfBox);
		leftBox.getChildren().add(iwfVBox);

		VBox lctsVBox = new VBox();
		lctsVBox.setSpacing(5);
		JFXButton lctsButton = new JFXButton("?????????????????????");

		HBox lctsBox = new HBox();
		TextFlow lcts = new TextFlow();
		Text lctsName = new Text("????????????: ");
		lctsName.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		lcts.getChildren().add(lctsName);
		JFXTextField lctsCityField = new JFXTextField();
		ValidatorBase lctsCityValid = new RequiredFieldValidator("????????????????????????");
		FontIcon lctsTri = new FontIcon();
		lctsTri.setIconLiteral("fas-exclamation-triangle");
		lctsCityValid.setIcon(lctsTri);
		lctsCityField.setValidators(lctsCityValid);
		lctsBox.getChildren().addAll(lcts, lctsCityField);

		HBox lctsShipBox = new HBox();
		TextFlow lctsShip = new TextFlow();
		Text lctsShipName = new Text("????????????: ");
		lctsShipName.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		lctsShip.getChildren().add(lctsShipName);
		JFXTextField lctsShipField = new JFXTextField();
		ValidatorBase lctsShipValid = new RequiredFieldValidator("????????????????????????");
		FontIcon lctsShipTri = new FontIcon();
		lctsShipTri.setIconLiteral("fas-exclamation-triangle");
		lctsShipValid.setIcon(lctsShipTri);
		lctsShipField.setValidators(lctsShipValid);
		lctsShipBox.getChildren().addAll(lctsShip, lctsShipField);
		lctsButton.setOnAction(e -> {
			if (lctsCityField.validate() && lctsShipField.validate()) {
				DBClient client = new DBClient();
				try {
					LoadContainerToShipInfoPacket packet = (LoadContainerToShipInfoPacket) client
							.sendAndReceivePacket(new LoadContainerToShipPacket(this.cookie, lctsCityField.getText(),
									lctsShipField.getText()));
					if (packet.isSuccess()) {
						showDialog("????????????", top, false);
					} else {
						showDialog("????????????", top, true);
					}
				} catch (IOException e1) {
					showDialog("????????????", top, true);
				}
			}
		});

		lctsVBox.getChildren().addAll(lctsButton, lctsBox, lctsShipBox);
		leftBox.getChildren().add(lctsVBox);

		VBox litcVBox = new VBox();
		litcVBox.setSpacing(5);
		JFXButton litcButton = new JFXButton("?????????????????????");

		HBox litcBox = new HBox();
		TextFlow litc = new TextFlow();
		Text litcName = new Text("????????????: ");
		litcName.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		litc.getChildren().add(litcName);
		JFXTextField litcCityField = new JFXTextField();
		ValidatorBase litcCityValid = new RequiredFieldValidator("????????????????????????");
		FontIcon litcTri = new FontIcon();
		litcTri.setIconLiteral("fas-exclamation-triangle");
		litcCityValid.setIcon(litcTri);
		litcCityField.setValidators(litcCityValid);
		litcBox.getChildren().addAll(litc, litcCityField);

		HBox litcShipBox = new HBox();
		TextFlow litcShip = new TextFlow();
		Text litcShipName = new Text("????????????: ");
		litcShipName.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		litcShip.getChildren().add(litcShipName);
		JFXTextField litcShipField = new JFXTextField();
		ValidatorBase litcShipValid = new RequiredFieldValidator("????????????????????????");
		FontIcon litcShipTri = new FontIcon();
		litcShipTri.setIconLiteral("fas-exclamation-triangle");
		litcShipValid.setIcon(litcShipTri);
		litcShipField.setValidators(litcShipValid);
		litcShipBox.getChildren().addAll(litcShip, litcShipField);
		litcButton.setOnAction(e -> {
			if (litcCityField.validate() && litcShipField.validate()) {
				DBClient client = new DBClient();
				try {
					LoadItemToContainerInfoPacket packet = (LoadItemToContainerInfoPacket) client
							.sendAndReceivePacket(new LoadItemToContainerPacket(this.cookie, litcCityField.getText(),
									litcShipField.getText()));
					if (packet.isSuccess()) {
						showDialog("????????????", top, false);
					} else {
						showDialog("????????????", top, true);
					}
				} catch (IOException e1) {
					showDialog("????????????", top, true);
				}
			}
		});

		litcVBox.getChildren().addAll(litcButton, litcBox, litcShipBox);
		leftBox.getChildren().add(litcVBox);

		VBox leftBox2 = new VBox();
		leftBox2.setPrefWidth(270);
		leftBox2.setPrefHeight(500);
		leftBox2.setSpacing(20);

		VBox sssVBox = new VBox();
		sssVBox.setSpacing(5);
		JFXButton sssButton = new JFXButton("????????????");

		HBox sssBox = new HBox();
		TextFlow sss = new TextFlow();
		Text sssName = new Text("????????????: ");
		sssName.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		sss.getChildren().add(sssName);
		JFXTextField sssField = new JFXTextField();
		ValidatorBase sssValid = new RequiredFieldValidator("????????????????????????");
		FontIcon sssTri = new FontIcon();
		sssTri.setIconLiteral("fas-exclamation-triangle");
		sssValid.setIcon(sssTri);
		sssField.setValidators(sssValid);
		sssBox.getChildren().addAll(sss, sssField);
		sssButton.setOnAction(e -> {
			if (sssField.validate()) {
				DBClient client = new DBClient();
				try {
					StartShipSailingInfoPacket packet = (StartShipSailingInfoPacket) client
							.sendAndReceivePacket(new StartShipSailingPacket(this.cookie, sssField.getText()));
					if (packet.isSuccess()) {
						this.showDialog("???????????????", top, false);
					} else {
						this.showDialog("????????????", top, true);
					}
				} catch (IOException e1) {
					this.showDialog("????????????", top, true);
				}

			}
		});
		sssVBox.getChildren().addAll(sssButton, sssBox);
		leftBox2.getChildren().add(sssVBox);

		VBox uiVBox = new VBox();
		uiVBox.setSpacing(5);
		JFXButton uiButton = new JFXButton("????????????");

		HBox uiBox = new HBox();
		TextFlow ui = new TextFlow();
		Text uiName = new Text("????????????: ");
		uiName.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		ui.getChildren().add(uiName);
		JFXTextField uiField = new JFXTextField();
		ValidatorBase uiValid = new RequiredFieldValidator("????????????????????????");
		FontIcon uiTri = new FontIcon();
		uiTri.setIconLiteral("fas-exclamation-triangle");
		uiValid.setIcon(uiTri);
		uiField.setValidators(uiValid);
		uiBox.getChildren().addAll(ui, uiField);
		uiButton.setOnAction(e -> {
			if (uiField.validate()) {
				DBClient client = new DBClient();
				try {
					UnloadItemInfoPacket packet = (UnloadItemInfoPacket) client
							.sendAndReceivePacket(new UnloadItemPacket(this.cookie, uiField.getText()));
					if (packet.isSuccess()) {
						this.showDialog("????????????", top, false);
					} else {
						this.showDialog("????????????", top, true);
					}
				} catch (IOException e1) {
					this.showDialog("????????????", top, true);
				}

			}
		});
		uiVBox.getChildren().addAll(uiButton, uiBox);
		leftBox2.getChildren().add(uiVBox);

		leftHBox.getChildren().addAll(leftBox, leftBox2);
		leftPane.getChildren().add(leftHBox);

	}

	public void initSeaportOfficerBoard(HBox hbox, StackPane top) {
		StackPane leftPane = new StackPane();
		leftPane.getStylesheets().add(getClass().getResource("/css/userview.css").toExternalForm());
		leftPane.setPrefHeight(550);
		leftPane.setPrefWidth(590);
		leftPane.setStyle("-fx-background-color: WHITE");
		leftPane.setPadding(new Insets(30, 0, 0, 30));
		JFXDepthManager.setDepth(leftPane, 4);
		hbox.getChildren().add(leftPane);

		VBox leftBox = new VBox();
		leftBox.setPrefWidth(540);
		leftBox.setPrefHeight(500);
		leftBox.setSpacing(20);

		JFXButton getAllItemsAtPortButton = new JFXButton("????????????????????????");
		getAllItemsAtPortButton.setOnAction(e -> {
			DBClient client = new DBClient();
			GetAllItemsAtPortInfoPacket packet;
			try {
				packet = (GetAllItemsAtPortInfoPacket) client
						.sendAndReceivePacket(new GetAllItemsAtPortPacket(this.cookie));
				String items[] = packet.getItems();

				if (items.length == 0) {
					this.showDialog("?????????????????????", top, false);
					return;
				}

				PageHelper helper = new PageHelper((items.length / 50) + (items.length % 50 == 0 ? 0 : 1));

				VBox vbox = new VBox();
				vbox.setAlignment(Pos.TOP_CENTER);
				JFXListView<String> lv = new JFXListView<>();
				List<String> list = new ArrayList<>(Arrays.asList(items));
				lv.getItems().setAll(list.subList(helper.getCurrentPage() * 50,
						Math.min(list.size(), (helper.getCurrentPage() + 1) * 50)));
				lv.getStylesheets().add(getClass().getResource("/css/scrollpane.css").toExternalForm());

				Text tableText = new Text("???????????????");
				tableText.setStyle("-fx-font-weight: bold; -fx-font-size: 13px");
				HBox pageBox = new HBox();

				pageBox.setSpacing(10);
				JFXButton lastPage = new JFXButton("?????????");
				lastPage.getStyleClass().add("page-button");
				Text text = new Text("??? " + (helper.getCurrentPage() + 1) + "/" + helper.getMaxPages() + " ???");
				text.setFont(Font.font(13));

				pageBox.setAlignment(Pos.CENTER);

				JFXButton nextPage = new JFXButton("?????????");
				nextPage.getStyleClass().add("page-button");

				lastPage.setOnAction(e1 -> {
					helper.lastPage();
					text.setText("??? " + (helper.getCurrentPage() + 1) + "/" + helper.getMaxPages() + " ???");
					lv.getItems().setAll(list.subList(helper.getCurrentPage() * 50,
							Math.min(list.size(), (helper.getCurrentPage() + 1) * 50)));
				});

				nextPage.setOnAction(e1 -> {
					helper.nextPage();
					text.setText("??? " + (helper.getCurrentPage() + 1) + "/" + helper.getMaxPages() + " ???");
					lv.getItems().setAll(list.subList(helper.getCurrentPage() * 50,
							Math.min(list.size(), (helper.getCurrentPage() + 1) * 50)));
				});

				pageBox.getChildren().addAll(lastPage, text, nextPage);
				pageBox.setSpacing(5);

				vbox.getChildren().addAll(tableText, pageBox, lv);
				JFXDialog dialog = new JFXDialog();
				dialog.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
				dialog.setTransitionType(DialogTransition.CENTER);
				JFXDialogLayout layout = new JFXDialogLayout();
				layout.setHeading(new Label("??????"));
				layout.setBody(vbox);
				JFXButton closeButton = new JFXButton("??????");
				closeButton.setStyle("-fx-text-fill: SKYBLUE");
				closeButton.setOnAction(event -> {
					dialog.close();
				});
				layout.setActions(closeButton);
				closeButton.getStyleClass().add("dialog-accept");
				dialog.setContent(layout);
				dialog.setDialogContainer(top);
				dialog.show();
			} catch (IOException e1) {
				this.showDialog("????????????", top, true);
			}

		});

		leftBox.getChildren().add(getAllItemsAtPortButton);

		VBox sicsVBox = new VBox();
		sicsVBox.setSpacing(5);
		JFXButton sicsButton = new JFXButton("????????????????????????");

		HBox sicsItemBox = new HBox();
		TextFlow sicsItem = new TextFlow();
		Text sicsItemName = new Text("????????????: ");
		sicsItemName.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		sicsItem.getChildren().add(sicsItemName);
		JFXTextField sicsItemField = new JFXTextField();
		ValidatorBase sicsItemValid = new RequiredFieldValidator("????????????????????????");
		FontIcon sicsItemTri = new FontIcon();
		sicsItemTri.setIconLiteral("fas-exclamation-triangle");
		sicsItemValid.setIcon(sicsItemTri);
		sicsItemField.setValidators(sicsItemValid);
		sicsItemBox.getChildren().addAll(sicsItem, sicsItemField);

		HBox sicsStateBox = new HBox();
		TextFlow sicsState = new TextFlow();
		Text sicsStateName = new Text("????????????: ");
		sicsStateName.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		sicsState.getChildren().add(sicsStateName);
		JFXCheckBox jcb = new JFXCheckBox();
		sicsStateBox.getChildren().addAll(sicsState, jcb);

		sicsButton.setOnAction(e -> {
			if (sicsItemField.validate()) {
				DBClient client = new DBClient();
				try {
					SetItemCheckStateInfoPacket packet = (SetItemCheckStateInfoPacket) client.sendAndReceivePacket(
							new SetItemCheckStatePacket(this.cookie, sicsItemField.getText(), jcb.isSelected()));
					if (packet.isSuccess()) {
						showDialog("????????????", top, false);
					} else {
						showDialog("????????????", top, true);
					}
				} catch (IOException e1) {
					showDialog("????????????", top, true);
				}
			}
		});

		sicsVBox.getChildren().addAll(sicsButton, sicsItemBox, sicsStateBox);
		leftBox.getChildren().add(sicsVBox);

		leftPane.getChildren().add(leftBox);

	}

	public void initCourierManagerBoard(HBox hbox, StackPane top) {
		StackPane leftPane = new StackPane();
		leftPane.getStylesheets().add(getClass().getResource("/css/userview.css").toExternalForm());
		leftPane.setPrefHeight(550);
		leftPane.setPrefWidth(590);
		leftPane.setStyle("-fx-background-color: WHITE");
		leftPane.setPadding(new Insets(30, 0, 0, 30));
		JFXDepthManager.setDepth(leftPane, 4);
		hbox.getChildren().add(leftPane);

		VBox leftBox = new VBox();
		leftBox.setPrefWidth(540);
		leftBox.setPrefHeight(500);
		leftBox.setSpacing(20);

		JFXButton newItemButton = new JFXButton("????????????");
		newItemButton.setOnAction(e -> {
			JFXDialog dialog = new JFXDialog();
			dialog.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
			dialog.setTransitionType(DialogTransition.CENTER);
			JFXDialogLayout layout = new JFXDialogLayout();
			layout.setHeading(new Label("????????????"));

			HBox dBox = new HBox();
			dBox.setPrefSize(650, 300);
			dBox.setMinSize(650, 300);
			dBox.setSpacing(25);

			VBox vbox = new VBox();
			vbox.setPrefSize(300, 300);
			vbox.setSpacing(20);

			HBox nameBox = new HBox();
			TextFlow nameTitle = new TextFlow();
			Text nameText = new Text("????????????: ");
			nameText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			nameTitle.getChildren().add(nameText);
			JFXTextField nameField = new JFXTextField();
			ValidatorBase nameValid = new RequiredFieldValidator("????????????????????????");
			FontIcon nameTri = new FontIcon();
			nameTri.setIconLiteral("fas-exclamation-triangle");
			nameValid.setIcon(nameTri);
			nameField.setValidators(nameValid);
			nameBox.getChildren().addAll(nameTitle, nameField);
			vbox.getChildren().add(nameBox);

			HBox typeBox = new HBox();
			TextFlow typeTitle = new TextFlow();
			Text typeText = new Text("????????????: ");
			typeText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			typeTitle.getChildren().add(typeText);
			JFXTextField typeField = new JFXTextField();
			ValidatorBase typeValid = new RequiredFieldValidator("????????????????????????");
			FontIcon typeTri = new FontIcon();
			typeTri.setIconLiteral("fas-exclamation-triangle");
			typeValid.setIcon(typeTri);
			typeField.setValidators(typeValid);
			typeBox.getChildren().addAll(typeTitle, typeField);
			vbox.getChildren().add(typeBox);

			HBox priceBox = new HBox();
			TextFlow priceTitle = new TextFlow();
			Text priceText = new Text("????????????: ");
			priceText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			priceTitle.getChildren().add(priceText);
			JFXTextField priceField = new JFXTextField();
			ValidatorBase priceValid = new RequiredFieldValidator("????????????????????????");
			ValidatorBase numberValid = new ValidatorBase("???????????????????????????") {

				@Override
				protected void eval() {
					try {
						TextInputControl text = (TextInputControl) srcControl.get();
						double price = Double.parseDouble(text.getText());
						hasErrors.set(false);
					} catch (NumberFormatException e) {
						hasErrors.set(true);
					}
				}

			};
			FontIcon priceTri = new FontIcon();
			priceTri.setIconLiteral("fas-exclamation-triangle");
			priceValid.setIcon(priceTri);
			FontIcon numberTri = new FontIcon();
			numberTri.setIconLiteral("fas-exclamation-triangle");
			numberValid.setIcon(numberTri);
			priceField.setValidators(priceValid, numberValid);
			priceBox.getChildren().addAll(priceTitle, priceField);
			vbox.getChildren().add(priceBox);

			HBox stateBox = new HBox();
			TextFlow stateTitle = new TextFlow();
			Text stateText = new Text("????????????: ");
			stateText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			JFXComboBox<String> stateCB = new JFXComboBox<>();
			stateCB.getStylesheets().add(getClass().getResource("/css/scrollpane.css").toExternalForm());

			stateCB.setItems(FXCollections.observableArrayList(stateList));
			stateCB.getSelectionModel().select(0);

			stateTitle.getChildren().add(stateText);

			stateBox.getChildren().addAll(stateTitle, stateCB);
			vbox.getChildren().add(stateBox);

			HBox retrCityBox = new HBox();
			TextFlow retrCityTitle = new TextFlow();
			Text retrCityText = new Text("????????????: ");
			retrCityText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			retrCityTitle.getChildren().add(retrCityText);
			JFXTextField retrCityField = new JFXTextField();
			ValidatorBase retrCityValid = new RequiredFieldValidator("????????????????????????");
			FontIcon retrCityTri = new FontIcon();
			retrCityTri.setIconLiteral("fas-exclamation-triangle");
			retrCityValid.setIcon(retrCityTri);
			retrCityField.setValidators(retrCityValid);
			retrCityBox.getChildren().addAll(retrCityTitle, retrCityField);
			vbox.getChildren().add(retrCityBox);

			HBox retrCourierBox = new HBox();
			TextFlow retrCourierTitle = new TextFlow();
			Text retrCourierText = new Text("???????????????: ");
			retrCourierText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			retrCourierTitle.getChildren().add(retrCourierText);
			JFXTextField retrCourierField = new JFXTextField();
			ValidatorBase retrCourierValid = new RequiredFieldValidator("???????????????????????????");
			FontIcon retrCourierTri = new FontIcon();
			retrCourierTri.setIconLiteral("fas-exclamation-triangle");
			retrCourierValid.setIcon(retrCourierTri);
			retrCourierField.setValidators(retrCourierValid);
			retrCourierBox.getChildren().addAll(retrCourierTitle, retrCourierField);
			vbox.getChildren().add(retrCourierBox);

			HBox deliCityBox = new HBox();
			TextFlow deliCityTitle = new TextFlow();
			Text deliCityText = new Text("????????????: ");
			deliCityText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			deliCityTitle.getChildren().add(deliCityText);
			JFXTextField deliCityField = new JFXTextField();
			ValidatorBase deliCityValid = new RequiredFieldValidator("????????????????????????");
			FontIcon deliCityTri = new FontIcon();
			deliCityTri.setIconLiteral("fas-exclamation-triangle");
			deliCityValid.setIcon(deliCityTri);
			deliCityField.setValidators(deliCityValid);
			deliCityBox.getChildren().addAll(deliCityTitle, deliCityField);
			vbox.getChildren().add(deliCityBox);

			VBox vbox2 = new VBox();
			vbox2.setPrefSize(300, 300);
			vbox2.setSpacing(20);

			HBox deliCourierBox = new HBox();
			TextFlow deliCourierTitle = new TextFlow();
			Text deliCourierText = new Text("???????????????: ");
			deliCourierText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			deliCourierTitle.getChildren().add(deliCourierText);
			JFXTextField deliCourierField = new JFXTextField();
			deliCourierBox.getChildren().addAll(deliCourierTitle, deliCourierField);
			vbox2.getChildren().add(deliCourierBox);

			HBox importCityBox = new HBox();
			TextFlow importCityTitle = new TextFlow();
			Text importCityText = new Text("????????????: ");
			importCityText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			importCityTitle.getChildren().add(importCityText);
			JFXTextField importCityField = new JFXTextField();
			ValidatorBase importCityValid = new RequiredFieldValidator("????????????????????????");
			FontIcon importCityTri = new FontIcon();
			importCityTri.setIconLiteral("fas-exclamation-triangle");
			importCityValid.setIcon(importCityTri);
			importCityField.setValidators(importCityValid);
			importCityBox.getChildren().addAll(importCityTitle, importCityField);
			vbox2.getChildren().add(importCityBox);

			HBox importTaxBox = new HBox();
			TextFlow importTaxTitle = new TextFlow();
			Text importTaxText = new Text("?????????: ");
			importTaxText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			importTaxTitle.getChildren().add(importTaxText);
			JFXTextField importTaxField = new JFXTextField();
			ValidatorBase importTaxValid = new RequiredFieldValidator("?????????????????????");
			FontIcon importTaxTri = new FontIcon();
			importTaxTri.setIconLiteral("fas-exclamation-triangle");
			importTaxValid.setIcon(importTaxTri);
			ValidatorBase importNumberValid = new ValidatorBase("????????????????????????") {

				@Override
				protected void eval() {
					try {
						TextInputControl text = (TextInputControl) srcControl.get();
						double price = Double.parseDouble(text.getText());
						hasErrors.set(false);
					} catch (NumberFormatException e) {
						hasErrors.set(true);
					}
				}

			};
			FontIcon importNumberTri = new FontIcon();
			importNumberTri.setIconLiteral("fas-exclamation-triangle");
			importNumberValid.setIcon(importNumberTri);
			importTaxField.setValidators(importTaxValid, importNumberValid);
			importTaxBox.getChildren().addAll(importTaxTitle, importTaxField);
			vbox2.getChildren().add(importTaxBox);

			HBox importOfficerBox = new HBox();
			TextFlow importOfficerTitle = new TextFlow();
			Text importOfficerText = new Text("???????????????: ");
			importOfficerText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			importOfficerTitle.getChildren().add(importOfficerText);
			JFXTextField importOfficerField = new JFXTextField();
			importOfficerBox.getChildren().addAll(importOfficerTitle, importOfficerField);
			vbox2.getChildren().add(importOfficerBox);

			HBox exportCityBox = new HBox();
			TextFlow exportCityTitle = new TextFlow();
			Text exportCityText = new Text("????????????: ");
			exportCityText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			exportCityTitle.getChildren().add(exportCityText);
			JFXTextField exportCityField = new JFXTextField();
			ValidatorBase exportCityValid = new RequiredFieldValidator("????????????????????????");
			FontIcon exportCityTri = new FontIcon();
			exportCityTri.setIconLiteral("fas-exclamation-triangle");
			exportCityValid.setIcon(exportCityTri);
			exportCityField.setValidators(exportCityValid);
			exportCityBox.getChildren().addAll(exportCityTitle, exportCityField);
			vbox2.getChildren().add(exportCityBox);

			HBox exportTaxBox = new HBox();
			TextFlow exportTaxTitle = new TextFlow();
			Text exportTaxText = new Text("?????????: ");
			exportTaxText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			exportTaxTitle.getChildren().add(exportTaxText);
			JFXTextField exportTaxField = new JFXTextField();
			ValidatorBase exportTaxValid = new RequiredFieldValidator("?????????????????????");
			FontIcon exportTaxTri = new FontIcon();
			exportTaxTri.setIconLiteral("fas-exclamation-triangle");
			exportTaxValid.setIcon(exportTaxTri);
			ValidatorBase exportNumberValid = new ValidatorBase("????????????????????????") {

				@Override
				protected void eval() {
					try {
						TextInputControl text = (TextInputControl) srcControl.get();
						double price = Double.parseDouble(text.getText());
						hasErrors.set(false);
					} catch (NumberFormatException e) {
						hasErrors.set(true);
					}
				}

			};
			FontIcon exportNumberTri = new FontIcon();
			exportNumberTri.setIconLiteral("fas-exclamation-triangle");
			exportNumberValid.setIcon(exportNumberTri);
			exportTaxField.setValidators(exportTaxValid, exportNumberValid);
			exportTaxBox.getChildren().addAll(exportTaxTitle, exportTaxField);
			vbox2.getChildren().add(exportTaxBox);

			HBox exportOfficerBox = new HBox();
			TextFlow exportOfficerTitle = new TextFlow();
			Text exportOfficerText = new Text("???????????????: ");
			exportOfficerText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			exportOfficerTitle.getChildren().add(exportOfficerText);
			JFXTextField exportOfficerField = new JFXTextField();
			exportOfficerBox.getChildren().addAll(exportOfficerTitle, exportOfficerField);
			vbox2.getChildren().add(exportOfficerBox);

			dBox.getChildren().addAll(vbox, vbox2);
			layout.setBody(dBox);

			JFXButton closeButton = new JFXButton("??????");
			closeButton.setStyle("-fx-text-fill: #ff0000");
			closeButton.setOnAction(e1 -> {
				dialog.close();
			});
			JFXButton submitButton = new JFXButton("??????");
			submitButton.setOnAction(e1 -> {
				if (nameField.validate() && typeField.validate() && priceField.validate() && retrCityField.validate()
						&& retrCourierField.validate() && deliCityField.validate() && deliCourierField.validate()
						&& importCityField.validate() && importTaxField.validate() && importOfficerField.validate()
						&& exportCityField.validate() && exportTaxField.validate() && exportOfficerField.validate()) {
					dialog.close();
					String deliCourier = deliCourierField.getText();
					if (deliCourier != null && deliCourier.isEmpty()) {
						deliCourier = null;
					}
					String importOfficer = importOfficerField.getText();
					if (importOfficer != null && importOfficer.isEmpty()) {
						importOfficer = null;
					}
					String exportOfficer = exportOfficerField.getText();
					if (exportOfficer != null && exportOfficer.isEmpty()) {
						exportOfficer = null;
					}
					ItemInfo info = new ItemInfo(nameField.getText(), typeField.getText(),
							Double.parseDouble(priceField.getText()),
							stringStateMap.get(stateCB.getSelectionModel().getSelectedItem()),
							new RetrievalDeliveryInfo(retrCityField.getText(), retrCourierField.getText()),
							new RetrievalDeliveryInfo(deliCityField.getText(), deliCourier),
							new ImportExportInfo(importCityField.getText(), importOfficer,
									Double.parseDouble(importTaxField.getText())),
							new ImportExportInfo(exportCityField.getText(), exportOfficer,
									Double.parseDouble(exportTaxField.getText())));
					DBClient client = new DBClient();
					NewItemInfoPacket infoPacket;
					try {
						infoPacket = (NewItemInfoPacket) client
								.sendAndReceivePacket(new NewItemPacket(this.cookie, info));
						if (infoPacket.isSuccess()) {
							this.showDialog("??????????????????", top, false);
						} else {
							this.showDialog("??????????????????", top, true);
						}
					} catch (IOException e2) {
						this.showDialog("??????????????????", top, true);
					}

				}
			});

			layout.setActions(closeButton, submitButton);
			dialog.setContent(layout);
			dialog.setDialogContainer(top);
			dialog.show();
		});
		leftBox.getChildren().add(newItemButton);

		VBox setStateBox = new VBox();
		setStateBox.setSpacing(5);
		JFXButton setStateButton = new JFXButton("??????????????????");
		HBox setStateHBox = new HBox();
		TextFlow setStateFlow = new TextFlow();
		Text setStateTitle = new Text("????????????: ");
		setStateTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14");

		JFXTextField setStateNameTextField = new JFXTextField();
		ValidatorBase setStateNameValid = new RequiredFieldValidator("????????????????????????");
		FontIcon setStateNameTri = new FontIcon();
		setStateNameTri.setIconLiteral("fas-exclamation-triangle");
		setStateNameValid.setIcon(setStateNameTri);
		setStateNameTextField.setValidators(setStateNameValid);

		setStateFlow.getChildren().add(setStateTitle);
		setStateHBox.getChildren().addAll(setStateFlow, setStateNameTextField);

		HBox stateBox2 = new HBox();
		TextFlow stateTitle2 = new TextFlow();
		Text stateText2 = new Text("????????????: ");
		stateText2.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		JFXComboBox<String> stateCB2 = new JFXComboBox<>();
		stateCB2.getStylesheets().add(getClass().getResource("/css/scrollpane.css").toExternalForm());
		stateCB2.setItems(FXCollections.observableArrayList(stateList));
		stateCB2.getSelectionModel().select(0);
		stateTitle2.getChildren().add(stateText2);
		stateBox2.getChildren().addAll(stateTitle2, stateCB2);
		setStateBox.getChildren().addAll(setStateButton, setStateHBox, stateBox2);

		setStateButton.setOnAction(e1 -> {
			DBClient client = new DBClient();
			try {
				if (setStateNameTextField.validate()) {
					SetItemStateInfoPacket packet = (SetItemStateInfoPacket) client
							.sendAndReceivePacket(new SetItemStatePacket(this.cookie, setStateNameTextField.getText(),
									stringStateMap.get(stateCB2.getSelectionModel().getSelectedItem())));
					if (packet.isSuccess()) {
						showDialog("????????????", top, false);
					} else {
						showDialog("????????????", top, true);
					}
				}
			} catch (Exception e2) {
				showDialog("????????????", top, true);
			}
		});

		leftBox.getChildren().add(setStateBox);

		leftPane.getChildren().add(leftBox);
	}

	public void initPane() {
		stage = new Stage();
		stage.setMaxHeight(720);
		stage.setMaxWidth(1080);
		stage.setTitle(this.staffType + " ??????");

		StackPane top = new StackPane();
		top.setPrefSize(1080, 720);
		HBox hbox = new HBox();
		hbox.setMaxHeight(550);
		hbox.setMaxWidth(900);
		hbox.setSpacing(60);

		if (this.staffType.startsWith("SUSTC")) {
			this.initSustcManagerBoard(hbox, top);
		}
		if (this.staffType.startsWith("Company")) {
			this.initCompanyManagerBoard(hbox, top);
		}
		if (this.staffType.startsWith("Courier")) {
			this.initCourierManagerBoard(hbox, top);
		}
		if (this.staffType.startsWith("Seaport")) {
			this.initSeaportOfficerBoard(hbox, top);
		}

		StackPane rightPane = new StackPane();
		rightPane.setPrefHeight(550);
		rightPane.setPrefWidth(250);
		rightPane.setStyle("-fx-background-color: WHITE");

		VBox rightBox = new VBox();
		rightBox.setPrefSize(180, 500);
		rightBox.setSpacing(40);
		rightBox.setAlignment(Pos.TOP_CENTER);

		ImageView image = new ImageView();
		BorderPane imageViewWrapper = new BorderPane(image);
		rightBox.setPadding(new Insets(30, 0, 0, 0));

		imageViewWrapper.setCursor(Cursor.HAND);
		imageViewWrapper.setMaxSize(128, 128);
		imageViewWrapper.setStyle("-fx-border-style: SOLID; -fx-border-width: 5;-fx-border-color: BLACK");
		image.setImage(new Image(getClass().getResourceAsStream("/man_profile.jpg")));
		image.setSmooth(true);
		image.setFitHeight(128);
		image.setFitWidth(128);
		rightBox.getChildren().add(imageViewWrapper);

		VBox infoBox = new VBox();
		infoBox.setSpacing(10);
		infoBox.setMaxWidth(180);

		TextFlow userFlow = new TextFlow();
		Text userText = new Text("?????????: ");
		userText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		Text userNameText = new Text(this.user);
		userNameText.setFont(Font.font(14));
		userFlow.getChildren().addAll(userText, userNameText);

		infoBox.getChildren().add(userFlow);

		TextFlow perFlow = new TextFlow();
		Text perText = new Text("??????: ");
		perText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		Text perNameText = new Text(this.staffType);
		perNameText.setFont(Font.font(14));
		perFlow.getChildren().addAll(perText, perNameText);
		infoBox.getChildren().add(perFlow);

		TextFlow genderFlow = new TextFlow();
		Text genderText = new Text("??????: ");
		genderText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		Text genderNameText = new Text((this.gender ? "???" : "???"));
		genderNameText.setFont(Font.font(14));
		genderFlow.getChildren().addAll(genderText, genderNameText);
		infoBox.getChildren().add(genderFlow);

		TextFlow cityFlow = new TextFlow();
		Text cityText = new Text("??????: ");
		cityText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		Text cityNameText = new Text((this.city == null ? "???" : this.city));
		cityNameText.setFont(Font.font(14));
		cityFlow.getChildren().addAll(cityText, cityNameText);
		infoBox.getChildren().add(cityFlow);

		TextFlow companyFlow = new TextFlow();
		Text companyText = new Text("??????: ");
		companyText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		Text companyNameText = new Text((this.company == null ? "???" : this.company));
		companyNameText.setFont(Font.font(14));
		companyFlow.getChildren().addAll(companyText, companyNameText);
		infoBox.getChildren().add(companyFlow);

		TextFlow ageFlow = new TextFlow();
		Text ageText = new Text("??????: ");
		ageText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		Text ageNameText = new Text("" + this.age);
		ageNameText.setFont(Font.font(14));
		ageFlow.getChildren().addAll(ageText, ageNameText);
		infoBox.getChildren().add(ageFlow);

		TextFlow phoneFlow = new TextFlow();
		Text phoneText = new Text("?????????: ");
		phoneText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		Text phoneNameText = new Text("" + this.phone);
		phoneNameText.setFont(Font.font(14));
		phoneFlow.getChildren().addAll(phoneText, phoneNameText);
		infoBox.getChildren().add(phoneFlow);

		rightBox.getChildren().add(infoBox);

		rightPane.getChildren().add(rightBox);
		StackPane.setAlignment(rightBox, Pos.CENTER);
		hbox.getChildren().add(rightPane);

		JFXDepthManager.setDepth(rightPane, 4);

		top.getChildren().add(hbox);
		StackPane.setAlignment(hbox, Pos.CENTER);
		JFXDecorator dec = new JFXDecorator(stage, top);
		Node[] nodes = new Node[] { ((HBox) dec.getChildren().get(0)).getChildren().get(1),
				((HBox) dec.getChildren().get(0)).getChildren().get(2),
				((HBox) dec.getChildren().get(0)).getChildren().get(3),
				((HBox) dec.getChildren().get(0)).getChildren().get(4) };
		((HBox) dec.getChildren().get(0)).getChildren().remove(1);
		((HBox) dec.getChildren().get(0)).getChildren().remove(1);
		((HBox) dec.getChildren().get(0)).getChildren().remove(1);
		((HBox) dec.getChildren().get(0)).getChildren().remove(1);

		SVGGlyph graphic = new SVGGlyph(0, "SETTING",
				"M733.866667 469.333333l-46.933334-46.933333 59.733334-59.733333L896 512l-149.333333 149.333333-59.733334-64 42.666667-42.666666H512v-85.333334h221.866667zM725.333333 341.333333h-85.333333V256H341.333333v512h298.666667v-85.333333h85.333333v170.666666H256V170.666667h469.333333v170.666666z",
				Color.WHITE);
		graphic.setSizeForHeight(16);
		JFXButton btnLogout = new JFXButton();
		btnLogout.getStyleClass().add("jfx-decorator-button");
		btnLogout.setCursor(Cursor.HAND);
		btnLogout.setRipplerFill(Color.WHITE);
		btnLogout.setOnAction((action) -> {
			DBClient client = new DBClient();
			try {
				client.sendPacket(new LogoutPacket(this.cookie));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.stage.close();
			Main.logout();
		});
		btnLogout.setTranslateX(-30);
		btnLogout.setGraphic(graphic);
		((HBox) dec.getChildren().get(0)).getChildren().add(btnLogout);

		for (Node n : nodes) {
			((HBox) dec.getChildren().get(0)).getChildren().add(n);
		}
		ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/icon.png")));
		icon.setFitHeight(24);
		icon.setFitWidth(24);
		dec.setGraphic(icon);
		this.stage.getIcons().clear();
		this.stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
		Scene scene = new Scene(dec, 1080, 720);
		stage.setScene(scene);
	}

}
