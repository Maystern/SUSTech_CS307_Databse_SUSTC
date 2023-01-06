package cn.edu.sustech.cs307.datamanager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import cn.edu.sustech.cs307.Main;
import cn.edu.sustech.cs307.sqlconnector.MySQLConnector;
import cn.edu.sustech.cs307.sqlconnector.PostgreSQLConnector;
import cn.edu.sustech.cs307.sqlconnector.SQLConnector;
import cn.edu.sustech.cs307.sqlconnector.SQLUtils;

public class MultiThreadDataManager extends FastDataManager {

	private Class<? extends SQLConnector> useConnectorClass;

	public MultiThreadDataManager(Class<? extends SQLConnector> useConnectorClass) {
		super();
		this.useConnectorClass = useConnectorClass;
	}

	@Override 
	public void init(List<DataRecord> records) {
		SQLConnector connector;
		if (this.useConnectorClass == PostgreSQLConnector.class) {
			connector = SQLUtils.newPostgreSQLConnector();
		} else {
			connector = SQLUtils.newMySQLConnector();
		}
		try {
			connector.connect();
			connector.setAutoCommit(false);
			if (connector instanceof MySQLConnector) {
				connector.prepareStatement("SET foreign_key_checks = 'OFF'").execute();
			} else {
				connector.prepareStatement("ALTER TABLE item DISABLE TRIGGER ALL").execute();
				connector.prepareStatement("ALTER TABLE import_information DISABLE TRIGGER ALL").execute();
				connector.prepareStatement("ALTER TABLE export_information DISABLE TRIGGER ALL").execute();
				connector.prepareStatement("ALTER TABLE delivery_information DISABLE TRIGGER ALL").execute();
				connector.prepareStatement("ALTER TABLE retrieval_information DISABLE TRIGGER ALL").execute();
			}
			Main.debug("Constraints disabled...", false);
			super.initContainer(records, connector);
			Main.debug("Successfully import container table", false);
			super.initShip(records, connector);
			Main.debug("Successfully import ship table", false);
			super.initCourier(records, connector);
			Main.debug("Successfully import courier table", false);
			connector.commit();
			final CountDownLatch latch = new CountDownLatch(5);
			Thread itemThread = new Thread(() -> {
				SQLConnector sqlConnector = (this.useConnectorClass == PostgreSQLConnector.class
						? SQLUtils.newPostgreSQLConnector()
						: SQLUtils.newMySQLConnector());
				try {
					sqlConnector.connect();
					sqlConnector.setAutoCommit(false);
					super.initItem(records, sqlConnector);
					sqlConnector.commit();
					sqlConnector.setAutoCommit(true);
					sqlConnector.close();
					Main.debug("Successfully import item table", false);
					latch.countDown();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			itemThread.start();
			Thread retrievalThread = new Thread(() -> {
				SQLConnector sqlConnector = (this.useConnectorClass == PostgreSQLConnector.class
						? SQLUtils.newPostgreSQLConnector()
						: SQLUtils.newMySQLConnector());
				try {
					sqlConnector.connect();
					sqlConnector.setAutoCommit(false);
					super.initRetrievalInformation(records, sqlConnector);
					sqlConnector.commit();
					sqlConnector.setAutoCommit(true);
					sqlConnector.close();
					Main.debug("Successfully import retrieval_information table", false);
					latch.countDown();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			retrievalThread.start();
			Thread deliveryThread = new Thread(() -> {
				SQLConnector sqlConnector = (this.useConnectorClass == PostgreSQLConnector.class
						? SQLUtils.newPostgreSQLConnector()
						: SQLUtils.newMySQLConnector());
				try {
					sqlConnector.connect();
					sqlConnector.setAutoCommit(false);
					super.initDeliveryInformation(records, sqlConnector);
					sqlConnector.commit();
					sqlConnector.setAutoCommit(true);
					sqlConnector.close();
					Main.debug("Successfully import delivery_information table", false);
					latch.countDown();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			deliveryThread.start();
			Thread importThread = new Thread(() -> {
				SQLConnector sqlConnector = (this.useConnectorClass == PostgreSQLConnector.class
						? SQLUtils.newPostgreSQLConnector()
						: SQLUtils.newMySQLConnector());
				try {
					sqlConnector.connect();
					sqlConnector.setAutoCommit(false);
					super.initImportInformation(records, sqlConnector);
					sqlConnector.commit();
					sqlConnector.setAutoCommit(true);
					sqlConnector.close();
					Main.debug("Successfully import import_information table", false);
					latch.countDown();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			importThread.start();
			Thread exportThread = new Thread(() -> {
				SQLConnector sqlConnector = (this.useConnectorClass == PostgreSQLConnector.class
						? SQLUtils.newPostgreSQLConnector()
						: SQLUtils.newMySQLConnector());
				try {
					sqlConnector.connect();
					sqlConnector.setAutoCommit(false);
					super.initExportInformation(records, sqlConnector);
					sqlConnector.commit();
					sqlConnector.setAutoCommit(true);
					sqlConnector.close();
					Main.debug("Successfully import export_information table", false);
					latch.countDown();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			exportThread.start();
			latch.await();
			if (connector instanceof MySQLConnector) {
				connector.prepareStatement("SET foreign_key_checks = 'ON'").execute();
			} else {
				connector.prepareStatement("ALTER TABLE item ENABLE TRIGGER ALL").execute();
				connector.prepareStatement("ALTER TABLE import_information ENABLE TRIGGER ALL").execute();
				connector.prepareStatement("ALTER TABLE export_information ENABLE TRIGGER ALL").execute();
				connector.prepareStatement("ALTER TABLE delivery_information ENABLE TRIGGER ALL").execute();
				connector.prepareStatement("ALTER TABLE retrieval_information ENABLE TRIGGER ALL").execute();
			}
			connector.commit();
			connector.setAutoCommit(true);
			connector.close();
			Main.debug("Successfully import All items...", false);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void sampleMultiThreadInput() throws InterruptedException {
		List<ResultSet> results = new ArrayList<>();
		Thread thread1 = new Thread(() -> {
			PostgreSQLConnector connector = SQLUtils.newPostgreSQLConnector();
			try {
				connector.connect();
				PreparedStatement statement = connector.prepareStatement("select * from export_information");
				results.add(statement.executeQuery());
				connector.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
		thread1.start();
		Thread thread2 = new Thread(() -> {
			PostgreSQLConnector connector = SQLUtils.newPostgreSQLConnector();
			
			try {
				connector.connect();
				PreparedStatement statement = connector.prepareStatement("select * from import_information");
				results.add(statement.executeQuery());
				connector.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
		thread2.start();
		thread1.join();
		thread2.join();
		
		//handle result;
		
	}

}
