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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import cn.edu.sustech.cs307.Main;
import cn.edu.sustech.cs307.datamanager.DataRecord.RecordAttribute;
import cn.edu.sustech.cs307.sqlconnector.PostgreSQLConnector;
import cn.edu.sustech.cs307.sqlconnector.SQLConnector;
import cn.edu.sustech.cs307.utils.CalendarUtils;

public class FastDataManager extends DataManager {

	private SQLConnector sqlConnector;
	private boolean debug = true;
	private HashSet<String> shipSet;
	private HashSet<String> containerSet;
	private HashSet<String> courierSet;
	private static final String containerSql = "INSERT INTO container (code, type) VALUES(?, ?)";
	private static final String shipSql = "INSERT INTO ship (name, company) VALUES(?, ?)";
	private static final String importSql = "INSERT INTO import_information(item, city, time, tax) VALUES(?, ?, ?, ?)";
	private static final String exportSql = "INSERT INTO export_information(item, city, time, tax) VALUES(?, ?, ?, ?)";
	private static final String deliverySql = "INSERT INTO delivery_information(item, city, finish_time, courier_phone_number) VALUES(?, ?, ?, ?)";
	private static final String retrievalSql = "INSERT INTO retrieval_information(item, city, start_time, courier_phone_number) VALUES(?, ?, ?, ?)";
	private static final String itemSql = "INSERT INTO item(name, type, price, container_code, ship_name, log_time) VALUES(?, ?, ?, ?, ?, ?)";
	private static final String courierSql = "INSERT INTO courier(phone_number, name, gender, birth_year, company) VALUES(?, ?, ?, ?, ?)";
	
	private static ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };
	
	private static ThreadLocal<DateFormat> timestampFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };
	
	public FastDataManager(SQLConnector sqlConnector) {
		this.sqlConnector = sqlConnector;
		shipSet = new HashSet<>();
		containerSet = new HashSet<>();
		courierSet = new HashSet<>();
	}
	
	protected FastDataManager() {
		shipSet = new HashSet<>();
		containerSet = new HashSet<>();
		courierSet = new HashSet<>();
	}
	
	protected void initContainer(List<DataRecord> records, SQLConnector sqlConnector) throws SQLException {
		PreparedStatement statement = sqlConnector.prepareStatement(containerSql);
		for (DataRecord record : records) {
			String containerCode = (String) record.getValue(RecordAttribute.CONTAINER_CODE);
			if (containerCode != null && !containerSet.contains(containerCode)) {
				statement.setString(1, containerCode);
				statement.setString(2, (String) record.getValue(RecordAttribute.CONTAINER_TYPE));
				statement.addBatch();
				containerSet.add(containerCode);
			}
		}
		statement.executeBatch();
	}
	
	protected void initShip(List<DataRecord> records, SQLConnector sqlConnector) throws SQLException {
		PreparedStatement statement = sqlConnector.prepareStatement(shipSql);
		int i = 0;
		for (DataRecord record : records) {
			String companyName = (String) record.getValue(RecordAttribute.COMPANY_NAME);
			String shipName = (String) record.getValue(RecordAttribute.SHIP_NAME);
			if (shipName != null && !shipSet.contains(shipName)) {
				statement.setString(1, shipName);
				statement.setString(2, companyName);
				statement.addBatch();
				shipSet.add(shipName);
				if ((++i) % 5000 == 0) {
					statement.executeBatch();
				}
			}
			
		}
		statement.executeBatch();
	}
	
	public boolean deleteRetrievalInformationByItem(String item) {
		try {
			PreparedStatement statement = this.sqlConnector.prepareStatement("DELETE FROM retrieval_information WHERE item='" + item + "'");
			statement.execute();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	public int deleteRetrievalInformationByCourier(String cpn) {
		try {
			PreparedStatement statement = this.sqlConnector.prepareStatement("DELETE FROM retrieval_information WHERE courier_phone_number='" + cpn + "'");
			return statement.executeUpdate();
		} catch (SQLException e) {
			return -1;
		}
	}
	
	public boolean insertRetrievalInformation(DataRecord record) {
		try {
			PreparedStatement statement = this.sqlConnector.prepareStatement(retrievalSql);
			statement.setString(1, (String) record.getValue(RecordAttribute.ITEM_NAME));
			statement.setString(2, (String) record.getValue(RecordAttribute.RETRIEVAL_CITY));
			
			statement.setDate(3, new Date(dateFormat.get().parse((String) record.getValue(RecordAttribute.RETRIEVAL_START_TIME)).getTime()));
			statement.setString(4, (String) record.getValue(RecordAttribute.RETRIEVAL_COURIER_PHONE_NUMBER));
			dateFormat.remove();
			return statement.execute();
		} catch (SQLException | ParseException e) {
			Main.debug("Inserting failed...", true);
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean multiInsertRetrievalInformation(List<DataRecord> records) {
		try {
			this.sqlConnector.setAutoCommit(false);
			this.initRetrievalInformation(records, this.sqlConnector);
			this.sqlConnector.commit();
			this.sqlConnector.setAutoCommit(true);
			return true;
		} catch (SQLException | ParseException e) {
			Main.debug("Inserting failed...", true);
			return false;
		}
	}
	
	public int selectRetrievalInformationByItem(String item) {
		try {
			PreparedStatement statement = this.sqlConnector.prepareStatement("SELECT * FROM retrieval_information WHERE item='" + item + "'");
			return statement.executeQuery().getFetchSize();
		} catch (SQLException e) {
			Main.debug("Selecting failed...", true);
			return -1;
		}
	}
	
	public int selectRetrievalInformationByCourier(String courierPN) {
		try {
			PreparedStatement statement = this.sqlConnector.prepareStatement("SELECT * FROM retrieval_information WHERE courier_phone_number='" + courierPN + "'");
			return statement.executeQuery().getFetchSize();
		} catch (SQLException e) {
			Main.debug("Selecting failed...", true);
			return -1;
		}
	}
	
	public int updateRetrievalCourierByItem(String item, String newCourierCPN) {
		try {
			PreparedStatement statement = this.sqlConnector.prepareStatement("UPDATE retrieval_information SET courier_phone_number='" + newCourierCPN + "' WHERE item='" + item + "'");
			return statement.executeUpdate();
		} catch (SQLException e) {
			Main.debug("Updating failed...", true);
			return -1;
		}
	}
	
	public int updateRetrievalCourierByCourier(String oldCourierPN, String newCourierPN) {
		try {
			PreparedStatement statement = this.sqlConnector.prepareStatement("UPDATE retrieval_information SET courier_phone_number='" + newCourierPN + "' WHERE courier_phone_number='" + oldCourierPN + "'");
			return statement.executeUpdate();
		} catch (SQLException e) {
			Main.debug("Updating failed...", true);
			return -1;
		}
	}
	
	public boolean addCourier(String company, String name, char gender, int birthYear, String pn) {
		try {
			PreparedStatement statement = this.sqlConnector.prepareStatement(courierSql);
			statement.setString(1, pn);
			statement.setString(2, name);
			statement.setString(3, "" + gender);
			statement.setInt(4, birthYear);
			statement.setString(5, company);
			return statement.execute();
		} catch (SQLException e) {
			Main.debug("Courier \"" + name + "\"adding failed...", true);
			return false;
		}
	}
	
	protected void initCourier(List<DataRecord> records, SQLConnector sqlConnector) throws SQLException, NumberFormatException, ParseException {
		PreparedStatement statement = sqlConnector.prepareStatement(courierSql);
		int i = 0;
		for (DataRecord record : records) {
			// courier table
			String companyName = (String) record.getValue(RecordAttribute.COMPANY_NAME);
			String deliveryCPN = (String) record.getValue(RecordAttribute.DELIVERY_COURIER_PHONE_NUMBER);
			if (deliveryCPN != null && !courierSet.contains(deliveryCPN)) {
				statement.setString(1, deliveryCPN);
				statement.setString(2, (String) record.getValue(RecordAttribute.DELIVERY_COURIER));
				statement.setString(3, (String) record.getValue(RecordAttribute.DELIVERY_COURIER_GENDER));
				int birthYear = CalendarUtils.getCalendar((String) record.getValue(RecordAttribute.DELIVERY_FINISHED_TIME)).get(Calendar.YEAR) - (int) Double.parseDouble((String) record.getValue(RecordAttribute.DELIVERY_COURIER_AGE));
				statement.setInt(4, birthYear);
				statement.setString(5, companyName);
				statement.addBatch();
				courierSet.add(deliveryCPN);
				++i;
			}
			String retrievalCPN = (String) record.getValue(RecordAttribute.RETRIEVAL_COURIER_PHONE_NUMBER);
			if (!courierSet.contains(retrievalCPN)) {
				statement.setString(1, retrievalCPN);
				statement.setString(2, (String) record.getValue(RecordAttribute.RETRIEVAL_COURIER));
				statement.setString(3, (String) record.getValue(RecordAttribute.RETRIEVAL_COURIER_GENDER));
				int birthYear = CalendarUtils.getCalendar((String) record.getValue(RecordAttribute.RETRIEVAL_START_TIME)).get(Calendar.YEAR) - (int) Double.parseDouble((String) record.getValue(RecordAttribute.RETRIEVAL_COURIER_AGE));
				statement.setInt(4, birthYear);
				statement.setString(5, companyName);
				statement.addBatch();
				courierSet.add(retrievalCPN);
				++i;
			}
			if (i % 5000 == 0) {
				statement.executeBatch();
			}
		}
		CalendarUtils.removeThreadLocal();
		statement.executeBatch();
	}
	
	protected void initItem(List<DataRecord> records, SQLConnector sqlConnector) throws SQLException, ParseException {
		PreparedStatement statement = sqlConnector.prepareStatement(itemSql);
		int i = 0;
		for (DataRecord record : records) {
			// item table
			String containerCode = (String) record.getValue(RecordAttribute.CONTAINER_CODE);
			String shipName = (String) record.getValue(RecordAttribute.SHIP_NAME);
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
			statement.setTimestamp(6, new Timestamp(timestampFormat.get().parse((String) record.getValue(RecordAttribute.LOG_TIME)).getTime()));
			statement.addBatch();
			if ((++i) % 5000 == 0) {
				statement.executeBatch();
			}
		}
		timestampFormat.remove();
		statement.executeBatch();
	}
	
	protected void initImportInformation(List<DataRecord> records, SQLConnector sqlConnector) throws SQLException, ParseException {
		PreparedStatement statement = sqlConnector.prepareStatement(importSql);
		int i = 0;
		for (DataRecord record : records) {
			// import_information table
			String importCity = (String) record.getValue(RecordAttribute.ITEM_IMPORT_CITY);
			String importTime = (String) record.getValue(RecordAttribute.ITEM_IMPORT_TIME);
			String importTax = (String) record.getValue(RecordAttribute.ITEM_IMPORT_TAX);
			statement.setString(1, (String) record.getValue(RecordAttribute.ITEM_NAME));
			statement.setString(2, importCity);
			if (importTime != null) {
				statement.setDate(3, new Date(dateFormat.get().parse(importTime).getTime()));
			} else {
				statement.setNull(3, Types.DATE);
			}
			statement.setBigDecimal(4, new BigDecimal(importTax));
			statement.addBatch();
			if ((++i) % 5000 == 0) {
				statement.executeBatch();
			}
		}
		dateFormat.remove();
		statement.executeBatch();
	}
	
	protected void initExportInformation(List<DataRecord> records, SQLConnector sqlConnector) throws SQLException, ParseException {
		PreparedStatement statement = sqlConnector.prepareStatement(exportSql);
		int i = 0;
		for (DataRecord record : records) {
			// export_information table
			String exportCity = (String) record.getValue(RecordAttribute.ITEM_EXPORT_CITY);
			String exportTime = (String) record.getValue(RecordAttribute.ITEM_EXPORT_TIME);
			String exportTax = (String) record.getValue(RecordAttribute.ITEM_EXPORT_TAX);
			statement.setString(1, (String) record.getValue(RecordAttribute.ITEM_NAME));
			statement.setString(2, exportCity);
			if (exportTime != null) {
				statement.setDate(3, new Date(dateFormat.get().parse(exportTime).getTime()));
			} else {
				statement.setNull(3, Types.DATE);
			}
			statement.setBigDecimal(4, new BigDecimal(exportTax));
			statement.addBatch();
			if ((++i) % 5000 == 0) {
				statement.executeBatch();
			}
		}
		dateFormat.remove();
		statement.executeBatch();
	}
	
	protected void initDeliveryInformation(List<DataRecord> records, SQLConnector sqlConnector) throws SQLException, ParseException {
		PreparedStatement statement = sqlConnector.prepareStatement(deliverySql);
		int i = 0;
		for (DataRecord record : records) {
			// delivery_information table
			String deliveryCPN = (String) record.getValue(RecordAttribute.DELIVERY_COURIER_PHONE_NUMBER);
			String finishTime = (String) record.getValue(RecordAttribute.DELIVERY_FINISHED_TIME);
			statement.setString(1, (String) record.getValue(RecordAttribute.ITEM_NAME));
			statement.setString(2, (String) record.getValue(RecordAttribute.DELIVERY_CITY));
			if (finishTime != null) {
				statement.setDate(3, new Date(dateFormat.get().parse(finishTime).getTime()));
			} else {
				statement.setNull(3, Types.DATE);
			}

			if (deliveryCPN != null) {
				statement.setString(4, deliveryCPN);
			} else {
				statement.setNull(4, Types.VARCHAR);
			}
			statement.addBatch();
			if ((++i) % 5000 == 0) {
				statement.executeBatch();
			}
		}
		dateFormat.remove();
		statement.executeBatch();
	}
	
	protected void initRetrievalInformation(List<DataRecord> records, SQLConnector sqlConnector) throws SQLException, ParseException {
		PreparedStatement statement = sqlConnector.prepareStatement(retrievalSql);
		int i = 0;
		for (DataRecord record : records) {
			// retrieval_information table
			statement.setString(1, (String) record.getValue(RecordAttribute.ITEM_NAME));
			statement.setString(2, (String) record.getValue(RecordAttribute.RETRIEVAL_CITY));
			
			statement.setDate(3, new Date(dateFormat.get().parse((String) record.getValue(RecordAttribute.RETRIEVAL_START_TIME)).getTime()));
			statement.setString(4, (String) record.getValue(RecordAttribute.RETRIEVAL_COURIER_PHONE_NUMBER));
			statement.addBatch();
			if ((++i) % 5000 == 0) {
				statement.executeBatch();
			}
		}
		dateFormat.remove();
		statement.executeBatch();
	}

	@Override
	public void init(List<DataRecord> records) {
		try {
			sqlConnector.setAutoCommit(false);
			
			this.initContainer(records, this.sqlConnector);
			Main.debug("Successfully import container table", false);

			this.initShip(records, this.sqlConnector);
			Main.debug("Successfully import ship table", false);
			
			this.initCourier(records, this.sqlConnector);
			Main.debug("Successfully import courier table", false);
			
			this.initItem(records, this.sqlConnector);
			Main.debug("Successfully import item table", false);
			
			this.initImportInformation(records, this.sqlConnector);
			Main.debug("Successfully import import_information table", false);
			
			this.initExportInformation(records, this.sqlConnector);
			Main.debug("Successfully import export_information table", false);

			this.initDeliveryInformation(records, this.sqlConnector);
			Main.debug("Successfully import delivery_information table", false);

			this.initRetrievalInformation(records, this.sqlConnector);
			Main.debug("Successfully import retrieval_information table", false);
			
			sqlConnector.commit();
			sqlConnector.setAutoCommit(true);
			Main.debug("Successfully import All items...", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
