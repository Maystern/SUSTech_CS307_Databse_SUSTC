package cn.edu.sustech.cs307;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import cn.edu.sustech.cs307.datamanager.DataRecord;
import cn.edu.sustech.cs307.datamanager.FastDataManager;
import cn.edu.sustech.cs307.datamanager.FileDataManager;
import cn.edu.sustech.cs307.datamanager.MultiThreadDataManager;
import cn.edu.sustech.cs307.datamanager.SimpleDataManager;
import cn.edu.sustech.cs307.datamanager.DataRecord.RecordAttribute;
import cn.edu.sustech.cs307.sqlconnector.MySQLConnector;
import cn.edu.sustech.cs307.sqlconnector.PostgreSQLConnector;
import cn.edu.sustech.cs307.sqlconnector.SQLConnector;
import cn.edu.sustech.cs307.sqlconnector.SQLUtils;

public class PerformanceAnalysis {
	
	protected static void initTest() throws IOException, SQLException {
		
		List<DataRecord> raw = Main.loadRecords(new File(Main.getProperty("data-file-path")));
		List<DataRecord> records = new ArrayList<>();
		records.addAll(raw.subList(0, 10));
		records.addAll(raw.subList(100000, 100010));
		records.addAll(raw.subList(200000, 200010));
		records.addAll(raw.subList(300000, 300010));
		records.addAll(raw.subList(400000, 400010));
		//PostgreSQL part
		
		PostgreSQLConnector psql = SQLUtils.newPostgreSQLConnector();
		psql.connect();
		psql.setAutoCommit(true);
		
		for (int i = 0; i < 3; ++i) {
			resetTable(psql);
			long cost = testTime(() -> {
				new SimpleDataManager(psql).init(records);
			});
			Main.log("PostgreSQL SimpleDataManager " + records.size() + " Records Init Cost #" + i + ": " + cost + "ms");
		}
		for (int i = 0; i < 3; ++i) {
			resetTable(psql);
			long cost = testTime(() -> {
				new FastDataManager(psql).init(records);
			});
			Main.log("PostgreSQL FastDataManager " + records.size() + " Records Init Cost #" + i + ": " + cost + "ms");
		}
		for (int i = 0; i < 3; ++i) {
			resetTable(psql);
			long cost = testTime(() -> {
				new MultiThreadDataManager(PostgreSQLConnector.class).init(records);
			});
			Main.log("PostgreSQL MutilThreadDataManager " + records.size() + " Records Init Cost #" + i + ": " + cost + "ms");
		}
		
		//MySQL part
		MySQLConnector msql = SQLUtils.newMySQLConnector();
		msql.connect();
		msql.setAutoCommit(true);
		
		//Very slow
		for (int i = 0; i < 3; ++i) {
			resetTable(msql);
			long cost = testTime(() -> {
				new SimpleDataManager(msql).init(records);
			});
			Main.log("MySQL SimpleDataManager " + records.size() + " Records Init Cost #" + i + ": " + cost + "ms");
		}
		
		for (int i = 0; i < 3; ++i) {
			resetTable(msql);
			long cost = testTime(() -> {
				new FastDataManager(msql).init(records);
			});
			Main.log("MySQL FastDataManager " + records.size() + " Records Init Cost #" + i + ": " + cost + "ms");
		}
		
		for (int i = 0; i < 3; ++i) {
			resetTable(msql);
			long cost = testTime(() -> {
				new MultiThreadDataManager(MySQLConnector.class).init(records);
			});
			Main.log("MySQL MutilThreadDataManager " + records.size() + " Records Init Cost #" + i + ": " + cost + "ms");
		}
		
		for (int i = 0; i < 3; ++i) {
			resetFile();
			long cost = testTime(() -> {
				new FileDataManager(new File(Main.getProperty("file-storage-directory"))).init(records);
			});
			Main.log("FileIO " + records.size() + " Records Init Cost #" + i +": " + cost + "ms");
		}
		
		
		
	}
	
	
	
	public static void allTest() throws SQLException, IOException {
		
		List<DataRecord> raw = Main.loadRecords(new File(Main.getProperty("data-file-path")));
		List<DataRecord> records = new ArrayList<>();
		records.addAll(raw.subList(0, 1000));
		records.addAll(raw.subList(100000, 101000));
		records.addAll(raw.subList(200000, 201000));
		records.addAll(raw.subList(300000, 301000));
		records.addAll(raw.subList(400000, 401000));
		
		//MySQL
		
		MySQLConnector msql = SQLUtils.newMySQLConnector();
		msql.connect();
		
		resetTable(msql);
		new MultiThreadDataManager(MySQLConnector.class).init(raw);
		
		FastDataManager msqlDM = new FastDataManager(msql);
		
		long mcost1 = testTime(() -> {
			for (DataRecord record : records) {
				msqlDM.deleteRetrievalInformationByItem((String) record.getValue(RecordAttribute.ITEM_NAME));
			}
		});
		
		Main.log("MySQL Delete " + records.size() + " Records By Item Name (one by one mode) Cost: " + mcost1 + "ms");
		
		long mcost2 = testTime(() -> {
			for (DataRecord record : records) {
				msqlDM.insertRetrievalInformation(record);
			}
		});
		
		Main.log("MySQL Insert " + records.size() + " Records (one by one mode) Cost: " + mcost2 + "ms");
		
		msqlDM.addCourier("123", "LLLLL", '男', 2002, "13580789877");
		
		long mcost21 = testTime(() -> {
			for (DataRecord record : records) {
				msqlDM.selectRetrievalInformationByItem((String) record.getValue(RecordAttribute.ITEM_NAME));
			}
		});
		
		Main.log("MySQL Select " + records.size() + " Records (one by one mode) Cost: " + mcost21 + "ms");
		
		long mcost3 = testTime(() -> {
			for (DataRecord record : records) {
				msqlDM.updateRetrievalCourierByItem((String) record.getValue(RecordAttribute.ITEM_NAME), "13580789877");
			}
		});
		
		Main.log("MySQL Update " + records.size() + " Records (one by one mode) Cost: " + mcost3 + "ms");
		
		msqlDM.addCourier("123", "LLLLL", '女', 2002, "13580789876");
		
		long mcost4 = testTime(() -> {
			msqlDM.updateRetrievalCourierByCourier("13580789877", "13580789876");
		});
		
		Main.log("MySQL MultiUpdate " + records.size() + " Records Cost: " + mcost4 + "ms");
		
		long mcost41 = testTime(() -> {
			msqlDM.selectRetrievalInformationByCourier("13580789876");
		});
		
		Main.log("MySQL MultiSelect " + records.size() + " Records Cost: " + mcost41 + "ms");
		
		long mcost5 = testTime(() -> {
			msqlDM.deleteRetrievalInformationByCourier("13580789876");
		});
		
		Main.log("MySQL MultiDelete " + records.size() + " Records Cost: " + mcost5 + "ms");
		
		long mcost6 = testTime(() -> {
			msqlDM.multiInsertRetrievalInformation(records);
		});
		
		Main.log("MySQL MultiInsert " + records.size() + " Records Cost: " + mcost6 + "ms");
		
		//PostgreSQL
		
		PostgreSQLConnector psql = SQLUtils.newPostgreSQLConnector();
		psql.connect();
		resetTable(psql);
		new MultiThreadDataManager(PostgreSQLConnector.class).init(raw);
		
		FastDataManager psqlDM = new FastDataManager(psql);
		
		long pcost1 = testTime(() -> {
			for (DataRecord record : records) {
				psqlDM.deleteRetrievalInformationByItem((String) record.getValue(RecordAttribute.ITEM_NAME));
			}
		});
		
		Main.log("PostgreSQL Delete " + records.size() + " Records By Item Name (one by one mode) Cost: " + pcost1 + "ms");
		
		long pcost2 = testTime(() -> {
			for (DataRecord record : records) {
				psqlDM.insertRetrievalInformation(record);
			}
		});
		
		Main.log("PostgreSQL Insert " + records.size() + " Records (one by one mode) Cost: " + pcost2 + "ms");
		
		psqlDM.addCourier("123", "LLLLL", '男', 2002, "13580789877");
		
		long pcost21 = testTime(() -> {
			for (DataRecord record : records) {
				psqlDM.selectRetrievalInformationByItem((String) record.getValue(RecordAttribute.ITEM_NAME));
			}
		});
		
		Main.log("PostgreSQL Select " + records.size() + " Records (one by one mode) Cost: " + pcost21 + "ms");
		
		long pcost3 = testTime(() -> {
			for (DataRecord record : records) {
				psqlDM.updateRetrievalCourierByItem((String) record.getValue(RecordAttribute.ITEM_NAME), "13580789877");
			}
		});
		
		Main.log("PostgreSQL Update " + records.size() + " Records (one by one mode) Cost: " + pcost3 + "ms");
		
		psqlDM.addCourier("123", "LLLLL", '女', 2002, "13580789876");
		
		long pcost4 = testTime(() -> {
			psqlDM.updateRetrievalCourierByCourier("13580789877", "13580789876");
		});
		
		Main.log("PostgreSQL MultiUpdate " + records.size() + " Records Cost: " + pcost4 + "ms");
		
		long pcost41 = testTime(() -> {
			psqlDM.selectRetrievalInformationByCourier("13580789876");
		});
		
		Main.log("PostgreSQL MultiSelect " + records.size() + " Records Cost: " + pcost41 + "ms");
		
		long pcost5 = testTime(() -> {
			psqlDM.deleteRetrievalInformationByCourier("13580789876");
		});
		
		Main.log("PostgreSQL MultiDelete " + records.size() + " Records Cost: " + pcost5 + "ms");
		
		long pcost6 = testTime(() -> {
			psqlDM.multiInsertRetrievalInformation(records);
		});
		
		Main.log("PostgreSQL MultiInsert " + records.size() + " Records Cost: " + pcost6 + "ms");
		
		resetFile();
		FileDataManager fdm = new FileDataManager(new File(Main.getProperty("file-storage-directory")));
		fdm.init(raw);
		
		long fcost1 = testTime(() -> {
			for (DataRecord record : records) {
				fdm.deleteRetrievalInformationByItem((String) record.getValue(RecordAttribute.ITEM_NAME));
			}
		});
		
		Main.log("File Delete " + records.size() + " Records By Item Name (one by one mode) Cost: " + fcost1 + "ms");
		
		long fcost2 = testTime(() -> {
			for (DataRecord record : records) {
				fdm.insertRetrievalInformation(record);
			}
		});
		
		Main.log("File Insert " + records.size() + " Records (one by one mode) Cost: " + fcost2 + "ms");
		
		fdm.addCourier("123", "LLLLL", '男', 2002, "13580789877");
		
		long fcost21 = testTime(() -> {
			for (DataRecord record : records) {
				fdm.selectRetrievalInformationByItem((String) record.getValue(RecordAttribute.ITEM_NAME));
			}
		});
		
		Main.log("File Select " + records.size() + " Records (one by one mode) Cost: " + fcost21 + "ms");
		
		long fcost3 = testTime(() -> {
			for (DataRecord record : records) {
				fdm.updateRetrievalCourierByItem((String) record.getValue(RecordAttribute.ITEM_NAME), "13580789877");
			}
		});
		
		Main.log("File Update " + records.size() + " Records (one by one mode) Cost: " + fcost3 + "ms");
		
		fdm.addCourier("123", "LLLLL", '女', 2002, "13580789876");
		
		long fcost4 = testTime(() -> {
			fdm.updateRetrievalCourierByCourier("13580789877", "13580789876");
		});
		
		Main.log("File MultiUpdate " + records.size() + " Records Cost: " + fcost4 + "ms");
		
		long fcost41 = testTime(() -> {
			fdm.selectRetrievalInformationByCourier("13580789876");
		});
		
		Main.log("File MultiSelect " + records.size() + " Records Cost: " + fcost41 + "ms");
		
		long fcost5 = testTime(() -> {
			fdm.deleteRetrievalInformationByCourier("13580789876");
		});
		
		Main.log("File MultiDelete " + records.size() + " Records Cost: " + fcost5 + "ms");
		
		long fcost6 = testTime(() -> {
			fdm.multiInsertRetrievalInformation(records);
		});
		
		Main.log("File MultiInsert " + records.size() + " Records Cost: " + fcost6 + "ms");
		
	}
	
	private static void deleteDirectory(File dir) {
		if (!dir.exists()) {
			return;
		}
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				deleteDirectory(file);
			} else {
				file.delete();
			}
		}
		dir.delete();
	}
	
	private static void resetFile() {
		File dir = new File(Main.getProperty("file-storage-directory"));
		deleteDirectory(dir);
		dir.mkdirs();
	}
	
	protected static long testTime(Runnable runnable) {
		long start = System.currentTimeMillis();
		runnable.run();
		return System.currentTimeMillis() - start;
	}
	
	protected static void generateDatas() {
		
	}
	
	private static void executeSQLFile(SQLConnector connector, File file) throws SQLException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder builder = new StringBuilder();
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			builder.append(line);
		}
		String[] statements = builder.toString().split(";");
		for (String statement : statements) {
			connector.prepareStatement(statement).execute();
		}
	}
	
	//Test code
	static void resetTable(SQLConnector connector) throws IOException, SQLException {
		File maker = new File(Main.getProperty("table-maker-file-path"));
		File dropper = new File(Main.getProperty("table-dropper-file-path"));
		executeSQLFile(connector, dropper);
		executeSQLFile(connector, maker);
		if (connector instanceof MySQLConnector) {
			connector.prepareStatement("ALTER TABLE container ENGINE = InnoDB").execute();
			connector.prepareStatement("ALTER TABLE courier ENGINE = InnoDB").execute();
			connector.prepareStatement("ALTER TABLE ship ENGINE = InnoDB").execute();
			connector.prepareStatement("ALTER TABLE item ENGINE = InnoDB").execute();
			connector.prepareStatement("ALTER TABLE import_information ENGINE = InnoDB").execute();
			connector.prepareStatement("ALTER TABLE export_information ENGINE = InnoDB").execute();
			connector.prepareStatement("ALTER TABLE delivery_information ENGINE = InnoDB").execute();
			connector.prepareStatement("ALTER TABLE retrieval_information ENGINE = InnoDB").execute();
			
		}
	}
	
}
