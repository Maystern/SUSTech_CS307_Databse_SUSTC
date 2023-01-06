package cn.edu.sustech.dbms2.client;

import cn.edu.sustech.dbms2.client.view.LobbyView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static Main instance;
	private LobbyView lv;
	
	public static void logout() {
		instance.lv.getStage().close();
		Platform.runLater(() -> {
			try {
				new Main().start(new Stage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			ThrowableHandler.handleThrowable(throwable);
		});
		Application.launch(Main.class);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		instance = this;
		this.lv = new LobbyView();
		lv.initStage();
		lv.getStage().show();
	}
}
