package cn.edu.sustech.cs307;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cn.edu.sustech.cs307.datamanager.DataManager;
import cn.edu.sustech.cs307.datamanager.DataRecord;
import cn.edu.sustech.cs307.datamanager.DataRecord.RecordAttribute;
import cn.edu.sustech.cs307.sqlconnector.MySQLConnector;
import cn.edu.sustech.cs307.sqlconnector.PostgreSQLConnector;
import cn.edu.sustech.cs307.sqlconnector.SQLConnector;
import cn.edu.sustech.cs307.sqlconnector.SQLUtils;
import cn.edu.sustech.cs307.datamanager.FastDataManager;
import cn.edu.sustech.cs307.datamanager.FileDataManager;
import cn.edu.sustech.cs307.datamanager.MultiThreadDataManager;
import cn.edu.sustech.cs307.datamanager.SimpleDataManager;

public class Main {
	
	private static PrintWriter writer;
	private static StringBuilder logger = new StringBuilder();
	private static Properties ppty;
	
	
	public static void debug(String raw, boolean isWarnning) {
		Calendar c = Calendar.getInstance();
		String msg = String.format("[" + (!isWarnning ? "INFO" : "WARN") + "][%d/%d/%d %02d:%02d:%02d] %s", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DATE), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND), raw);
		writer.println(msg);
		writer.flush();
	}
	
	public static void log(String raw) {
		Calendar c = Calendar.getInstance();
		String msg = String.format("[LOG][%d/%d/%d %02d:%02d:%02d] %s", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DATE), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND), raw);
		logger.append(msg + "\n");
		writer.println(msg);
		writer.flush();
	}
	
	public static String getProperty(String key) {
		return ppty.getProperty(key);
	}
	
	private static void loadConfig() throws IOException {
		
		HashMap<String, String> pmap = new HashMap<>();
		pmap.put("postgresql-host", "localhost");
		pmap.put("postgresql-port", "5432");
		pmap.put("postgresql-user", "postgres");
		pmap.put("postgresql-password", "123456");
		pmap.put("postgresql-database", "database");
		pmap.put("mysql-host", "localhost");
		pmap.put("mysql-port", "3306");
		pmap.put("mysql-user", "test");
		pmap.put("mysql-password", "123456");
		pmap.put("mysql-database", "cslab");
		pmap.put("data-file-path", "G:\\data.csv");
		pmap.put("table-maker-file-path", "D:\\workspace-git\\SUSTech_databse_Project01_SUSTC\\sql\\table_maker.sql");
		pmap.put("table-dropper-file-path", "D:\\workspace-git\\SUSTech_databse_Project01_SUSTC\\sql\\table_dropper.sql");
		pmap.put("file-storage-directory", "G:\\CS307Project1");
		
		
		ppty = new Properties();
		File configFile = new File("config.properties");
		if (!configFile.exists()) {
			configFile.createNewFile();
		}
		InputStream configInput = new FileInputStream(configFile);
		ppty.load(configInput);
		
		for (Map.Entry<String, String> entry : pmap.entrySet()) {
			if (ppty.getProperty(entry.getKey()) == null) {
				ppty.setProperty(entry.getKey(), entry.getValue());
			}
		}
		OutputStream configOutput = new FileOutputStream(configFile);
		ppty.store(configOutput, "Course CS307 DBMS Project by Lun Tianle and Luo Jiacheng");
		
	}
	
	public static void main(String args[]) throws SQLException, IOException {
		
		writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
		
		loadConfig();
		
		PerformanceAnalysis.initTest();
		PerformanceAnalysis.allTest();
		
		File log = new File("log.text");
		if (!log.exists()) {
			log.createNewFile();
		}
		FileWriter writer = new FileWriter(log);
		writer.write(logger.toString());
		writer.flush();
		writer.close();
	}
	
	public static List<DataRecord> loadRecords(File file) throws IOException {
		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String titles[] = reader.readLine().substring(1).split(","); //Read Title Line
		String line = null;
		List<DataRecord> records = new ArrayList<>();
		int total = 0;
		while ((line = reader.readLine()) != null) {
			String[] datas = line.split(",");
			DataRecord record = new DataRecord();
			int i = 0;
			for (String title : titles) {
				RecordAttribute attribute = RecordAttribute.valueOf(title.replace(" ", "_").toUpperCase());
				if (!datas[i++].isEmpty()) {
					if (attribute.name().contains("PHONE_NUMBER")) {
						datas[i - 1] = datas[i - 1].split("-")[1];
					}
					record.putValue(attribute, datas[i - 1]);
				}
			}
			records.add(record);
			
			
			++total;
			if (total % 10000 == 0) {
				debug("Successfully load " + total + " items from csv file...", false);
			}
		}
		reader.close();
		return records;
	}
	
}
