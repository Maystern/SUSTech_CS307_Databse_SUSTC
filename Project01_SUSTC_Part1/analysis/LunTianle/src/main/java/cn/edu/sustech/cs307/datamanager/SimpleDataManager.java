package cn.edu.sustech.cs307.datamanager;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import cn.edu.sustech.cs307.Main;
import cn.edu.sustech.cs307.datamanager.DataRecord.RecordAttribute;
import cn.edu.sustech.cs307.sqlconnector.PostgreSQLConnector;
import cn.edu.sustech.cs307.sqlconnector.SQLConnector;
import cn.edu.sustech.cs307.utils.CalendarUtils;

public class SimpleDataManager extends DataManager {
	
	private SQLConnector sqlConnector;
	private boolean debug = true;
	private HashSet<String> shipSet;
	private HashSet<String> containerSet;
	private HashSet<String> courierSet;
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private static final String containerSql = "INSERT INTO container (code, type) VALUES(?, ?)";
	private static final String shipSql = "INSERT INTO ship (name, company) VALUES(?, ?)";
	private static final String importSql = "INSERT INTO import_information(item, city, time, tax) VALUES(?, ?, ?, ?)";
	private static final String exportSql = "INSERT INTO export_information(item, city, time, tax) VALUES(?, ?, ?, ?)";
	private static final String deliverySql = "INSERT INTO delivery_information(item, city, finish_time, courier_phone_number) VALUES(?, ?, ?, ?)";
	private static final String retrievalSql = "INSERT INTO retrieval_information(item, city, start_time, courier_phone_number) VALUES(?, ?, ?, ?)";
	private static final String itemSql = "INSERT INTO item(name, type, price, container_code, ship_name, log_time) VALUES(?, ?, ?, ?, ?, ?)";
	private static final String courierSql = "INSERT INTO courier(phone_number, name, gender, birth_year, company) VALUES(?, ?, ?, ?, ?)";
	
	public SimpleDataManager(SQLConnector sqlConnector) {
		this.sqlConnector = sqlConnector;
		shipSet = new HashSet<>();
		containerSet = new HashSet<>();
		courierSet = new HashSet<>();
	}
	
	@Override
	public void init(List<DataRecord> records) {
		int i = 0;
		double average = 0;
		for (DataRecord record : records) {
			long start = System.currentTimeMillis();
			try {
				String companyName = (String) record.getValue(RecordAttribute.COMPANY_NAME);
				
				//container table
				String containerCode = (String) record.getValue(RecordAttribute.CONTAINER_CODE);
				if (containerCode != null && !containerSet.contains(containerCode)) {
					PreparedStatement statement = this.sqlConnector.prepareStatement(containerSql);
					statement.setString(1, containerCode);
					statement.setString(2, (String) record.getValue(RecordAttribute.CONTAINER_TYPE));
					statement.execute();
					containerSet.add(containerCode);
				}
				
				//ship table
				String shipName = (String) record.getValue(RecordAttribute.SHIP_NAME);
				if (shipName != null && !shipSet.contains(shipName)) {
					PreparedStatement statement = this.sqlConnector.prepareStatement(shipSql);
					statement.setString(1, shipName);
					statement.setString(2, companyName);
					statement.execute();
					shipSet.add(shipName);
				}
				
				//item table
				PreparedStatement statement = sqlConnector.prepareStatement(itemSql);
				statement.setString(1, (String) record.getValue(RecordAttribute.ITEM_NAME));
				statement.setString(2, (String) record.getValue(RecordAttribute.ITEM_TYPE));
				statement.setBigDecimal(3, new BigDecimal((String) record.getValue(RecordAttribute.ITEM_PRICE)));
				if (containerCode != null) {
					statement.setString(4, containerCode);
				} else {
					statement.setNull(4, Types.VARCHAR);
				}
				if (shipName != null) {
					statement.setString(5, shipName);
				} else {
					statement.setNull(5, Types.VARCHAR);
				}
				statement.setTimestamp(6, new Timestamp(timestampFormat.parse((String) record.getValue(RecordAttribute.LOG_TIME)).getTime()));
				statement.execute();
				
				//import_information table
				String importCity = (String) record.getValue(RecordAttribute.ITEM_IMPORT_CITY);
				String importTime = (String) record.getValue(RecordAttribute.ITEM_IMPORT_TIME);
				String importTax = (String) record.getValue(RecordAttribute.ITEM_IMPORT_TAX);
				
				statement = this.sqlConnector.prepareStatement(importSql);
				statement.setString(1, (String) record.getValue(RecordAttribute.ITEM_NAME));
				statement.setString(2, importCity);
				if (importTime != null) {
					statement.setDate(3, new Date(dateFormat.parse(importTime).getTime()));
				} else {
					statement.setNull(3, Types.DATE);
				}
				statement.setBigDecimal(4, new BigDecimal(importTax));;
				statement.execute();
				
				//export_information table
				String exportCity = (String) record.getValue(RecordAttribute.ITEM_EXPORT_CITY);
				String exportTime = (String) record.getValue(RecordAttribute.ITEM_EXPORT_TIME);
				String exportTax = (String) record.getValue(RecordAttribute.ITEM_EXPORT_TAX);
				statement = this.sqlConnector.prepareStatement(exportSql);
				statement.setString(1, (String) record.getValue(RecordAttribute.ITEM_NAME));
				statement.setString(2, exportCity);
				if (exportTime != null) {
					statement.setDate(3, new Date(dateFormat.parse(exportTime).getTime()));
				} else {
					statement.setNull(3, Types.DATE);
				}
				statement.setBigDecimal(4, new BigDecimal(exportTax));;
				statement.execute();
				
				String deliveryCPN = (String) record.getValue(RecordAttribute.DELIVERY_COURIER_PHONE_NUMBER);
				if (deliveryCPN != null && !courierSet.contains(deliveryCPN)) {
					statement = this.sqlConnector.prepareStatement(courierSql);
					statement.setString(1, deliveryCPN);
					statement.setString(2, (String) record.getValue(RecordAttribute.DELIVERY_COURIER));
					statement.setString(3, (String) record.getValue(RecordAttribute.DELIVERY_COURIER_GENDER));
					int birthYear = CalendarUtils.getCalendar((String) record.getValue(RecordAttribute.DELIVERY_FINISHED_TIME)).get(Calendar.YEAR) - (int) Double.parseDouble((String) record.getValue(RecordAttribute.DELIVERY_COURIER_AGE));
					statement.setInt(4, birthYear);
					statement.setString(5, companyName);
					statement.execute();
					courierSet.add(deliveryCPN);
				}
				
				//delivery_information table
				String finishTime = (String) record.getValue(RecordAttribute.DELIVERY_FINISHED_TIME);
				statement = this.sqlConnector.prepareStatement(deliverySql);
				statement.setString(1, (String) record.getValue(RecordAttribute.ITEM_NAME));
				statement.setString(2, (String) record.getValue(RecordAttribute.DELIVERY_CITY));
				if (finishTime != null) {
					statement.setDate(3, new Date(dateFormat.parse(finishTime).getTime()));
				} else {
					statement.setNull(3, Types.DATE);
				}
				
				if (deliveryCPN != null) {
					statement.setString(4, deliveryCPN);
				} else {
					statement.setNull(4, Types.VARCHAR);
				}
				statement.execute();

				//retrieval_courier table
				String retrievalCPN = (String) record.getValue(RecordAttribute.RETRIEVAL_COURIER_PHONE_NUMBER);
				if (!courierSet.contains(retrievalCPN)) {
					statement = this.sqlConnector.prepareStatement(courierSql);
					statement.setString(1, retrievalCPN);
					statement.setString(2, (String) record.getValue(RecordAttribute.RETRIEVAL_COURIER));
					statement.setString(3, (String) record.getValue(RecordAttribute.RETRIEVAL_COURIER_GENDER));
					int birthYear = CalendarUtils.getCalendar((String) record.getValue(RecordAttribute.RETRIEVAL_START_TIME)).get(Calendar.YEAR) - (int) Double.parseDouble((String) record.getValue(RecordAttribute.RETRIEVAL_COURIER_AGE));
					statement.setInt(4, birthYear);
					statement.setString(5, companyName);
					statement.execute();
					courierSet.add(retrievalCPN);
				}
				
				//retrieval_information table
				statement = this.sqlConnector.prepareStatement(retrievalSql);
				statement.setString(1, (String) record.getValue(RecordAttribute.ITEM_NAME));
				statement.setString(2, (String) record.getValue(RecordAttribute.RETRIEVAL_CITY));
				statement.setDate(3, new Date(dateFormat.parse((String) record.getValue(RecordAttribute.RETRIEVAL_START_TIME)).getTime()));
				statement.setString(4, (String) record.getValue(RecordAttribute.RETRIEVAL_COURIER_PHONE_NUMBER));
				statement.execute();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			++i;
//			average = (average * (i - 1) + (System.currentTimeMillis() - start)) / i;
//			if (i % 1000 == 0) {
//				Main.log("SimpleDataManager average cost: " + String.format("%.6f", average) + "ms");
//			}
			if (debug && i % 10000 == 0) {
				Main.debug("Successfully import " + i + " items...", false);
			}
		}
		Main.debug("Successfully import " + i + " items...", false);
	}

}
