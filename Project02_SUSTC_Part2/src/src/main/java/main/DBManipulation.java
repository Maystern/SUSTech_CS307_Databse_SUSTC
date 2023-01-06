package main;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import main.interfaces.*;
import main.interfaces.LogInfo.StaffType;
import main.interfaces.ItemInfo.ImportExportInfo;
import main.interfaces.ItemInfo.RetrievalDeliveryInfo;
import main.interfaces.ContainerInfo.Type;

public class DBManipulation implements IDatabaseManipulation {
	
	private String database, root, pass;
	private Connection rootConn;
	private Connection sustcManagerConn;
	private Connection courierConn;;
	private Connection seaportConn;
	private Connection companyManagerConn;
	
	static {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Connection createConnection(String database, String user, String pass) throws SQLException {
		Properties properties = new Properties();
		properties.setProperty("user", root);
		properties.setProperty("password", pass);
		properties.setProperty("useSSL", "false");
		properties.setProperty("autoReconnect", "true");
		return DriverManager.getConnection("jdbc:postgresql://" + database, user, pass);
	}
	
	

	public DBManipulation(String database, String root, String pass) {
		this.database = database;
		this.root = root;
		this.pass = pass;
		try {
			this.rootConn = createConnection(database, root, pass);
			
			
			Statement sta = this.rootConn.createStatement();
			sta.executeUpdate("create table if not exists staff (" 
					+ "    name varchar not null,"
					+ "    password varchar not null," 
					+ "    type varchar not null," 
					+ "    city varchar,"
					+ "    gender boolean not null," 
					+ "    phone_number varchar not null,"
					+ "    birth_year integer not null," 
					+ "    company varchar," 
					+ "    primary key (name)" 
					+ ");"
					+ "create table if not exists export_information (" 
					+ "    item_name varchar not null,"
					+ "    city varchar not null," 
					+ "    tax numeric(20, 7) not null,"
					+ "    staff_name varchar references staff(name)," 
					+ "    primary key (item_name)" 
					+ ");"
					+ "create table if not exists import_information(" 
					+ "    item_name varchar not null,"
					+ "    city varchar not null," 
					+ "    tax numeric(20, 7) not null,"
					+ "    staff_name varchar references staff(name)," 
					+ "    primary key (item_name)" 
					+ ");"
					+ "create table if not exists ship(" 
					+ "    item_name varchar not null," 
					+ "    ship_name varchar,"
					+ "    company varchar not null," 
					+ "    primary key (item_name)" 
					+ ");"
					+ "create table if not exists container(" 
					+ "    item_name varchar not null," 
					+ "    code varchar,"
					+ "    type varchar," 
					+ "    primary key (item_name)" 
					+ ");"
					+ "create table if not exists retrieval_information(" 
					+ "    item_name varchar not null,"
					+ "    city varchar not null," 
					+ "    staff_name varchar not null references staff(name),"
					+ "    primary key (item_name)" 
					+ ");" 
					+ "create table if not exists delivery_information("
					+ "    item_name varchar not null," 
					+ "    city varchar not null,"
					+ "    staff_name varchar references staff(name)," 
					+ "    primary key (item_name)" + ");"
					+ "create table if not exists item(" 
					+ "    name varchar not null," 
					+ "    type varchar not null,"
					+ "    price numeric(20, 7) not null," 
					+ "    state varchar not null," 
					+ "    primary key (name)"
					+ ");"
					+ "alter table delivery_information drop constraint if exists foreignKey_deliveryInformation_itemName;"
					+ "alter table retrieval_information drop constraint if exists foreignKey_retrievalInformation_itemName;"
					+ "alter table export_information drop constraint if exists foreignKey_exportInformation_itemName;"
					+ "alter table import_information drop constraint if exists foreignKey_importInformation_itemName;"
					+ "alter table ship drop constraint if exists foreignKey_ship_itemName;"
					+ "alter table container drop constraint if exists foreignKey_container_itemName;"
					+ "alter table delivery_information add constraint foreignKey_deliveryInformation_itemName foreign key (item_name) references item(name);"
					+ "alter table retrieval_information add constraint foreignKey_retrievalInformation_itemName foreign key (item_name) references item(name);"
					+ "alter table export_information add constraint foreignKey_exportInformation_itemName foreign key (item_name) references item(name);"
					+ "alter table import_information add constraint foreignKey_importInformation_itemName foreign key (item_name) references item(name);"
					+ "alter table ship add constraint foreignKey_ship_itemName foreign key (item_name) references item(name);"
					+ "alter table container add constraint foreignKey_container_itemName foreign key (item_name) references item(name);");
			try {
				this.rootConn.createStatement().execute(
						"REVOKE ALL ON TABLE delivery_information, export_information, import_information, item, retrieval_information, ship, staff, container from cs307_ll_sustcm;"
						+ "REVOKE ALL ON TABLE delivery_information, export_information, import_information, item, retrieval_information, ship, staff, container from cs307_ll_courier;"
						+ "REVOKE ALL ON TABLE delivery_information, export_information, import_information, item, retrieval_information, ship, staff, container from cs307_ll_companym;"
						+ "REVOKE ALL ON TABLE delivery_information, export_information, import_information, item, retrieval_information, ship, staff, container from cs307_ll_seaportm;"
						);
			} catch (Exception e) {}
			this.rootConn.createStatement().execute("DROP USER IF EXISTS cs307_ll_sustcm;"
					+ "DROP USER IF EXISTS cs307_ll_courier;"
					+ "DROP USER IF EXISTS cs307_ll_seaportm;"
					+ "DROP USER IF EXISTS cs307_ll_companym;"
					+ "CREATE USER cs307_ll_sustcm WITH PASSWORD '123456';"
					+ "CREATE USER cs307_ll_courier WITH PASSWORD '123456';"
					+ "CREATE USER cs307_ll_seaportm WITH PASSWORD '123456';"
					+ "CREATE USER cs307_ll_companym WITH PASSWORD '123456';"
					+ "GRANT SELECT ON staff, container, ship, item, import_information, export_information,  retrieval_information, delivery_information TO cs307_ll_sustcm;"
					
					+ "GRANT SELECT(name, type, price, state) ON item TO cs307_ll_courier;"
					+ "GRANT SELECT(name, city) ON staff TO cs307_ll_courier;"
					+ "GRANT SELECT(item_name, tax, city) ON import_information, export_information TO cs307_ll_courier;"
					+ "GRANT SELECT(item_name, staff_name) ON retrieval_information TO cs307_ll_courier;"
					+ "GRANT SELECT(item_name, city, staff_name) ON delivery_information TO cs307_ll_courier;"
					+ "GRANT INSERT ON item, delivery_information, retrieval_information, import_information, export_information TO cs307_ll_courier;"
					+ "GRANT UPDATE(state) ON item TO cs307_ll_courier;"
					+ "GRANT UPDATE(staff_name) ON delivery_information TO cs307_ll_courier;"
					+ "GRANT INSERT ON delivery_information, export_information, import_information, item, retrieval_information TO cs307_ll_courier;"
					+ "GRANT UPDATE ON item, delivery_information TO cs307_ll_courier;"
					
					+ "GRANT SELECT(name, price, state, type) ON item TO cs307_ll_companym;"
					+ "GRANT SELECT(item_name, tax, city) ON import_information, export_information TO cs307_ll_companym;"
					+ "GRANT SELECT(item_name, code, type) ON container TO cs307_ll_companym;"
					+ "GRANT SELECT(item_name, ship_name, company) ON ship TO cs307_ll_companym;"
					+ "GRANT SELECT(name, company) ON staff TO cs307_ll_companym;"
					+ "GRANT UPDATE(state) ON item TO cs307_ll_companym;"
					+ "GRANT UPDATE(ship_name) ON ship TO cs307_ll_companym;"
					+ "GRANT UPDATE(code, type) ON container TO cs307_ll_companym;"

					+ "GRANT SELECT(name, state) ON item TO cs307_ll_seaportm;"
					+ "GRANT SELECT(name, city) ON staff TO cs307_ll_seaportm;"
					+ "GRANT SELECT(item_name, city, staff_name) ON import_information, export_information TO cs307_ll_seaportm;"
					+ "GRANT UPDATE(state) ON item TO cs307_ll_seaportm;"
					+ "GRANT UPDATE(staff_name) ON import_information, export_information TO cs307_ll_seaportm;");
					
			this.sustcManagerConn = createConnection(database, "cs307_ll_sustcm", "123456");
			this.companyManagerConn = createConnection(database, "cs307_ll_companym", "123456");
			this.seaportConn = createConnection(database, "cs307_ll_seaportm", "123456");
			this.courierConn = createConnection(database, "cs307_ll_courier", "123456");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static final String containerSQL = "INSERT INTO container(item_name, code, type) VALUES(?, ?, ?)";
	private static final String deliveryInformationSQL = "INSERT INTO delivery_information(item_name, city, staff_name) VALUES(?, ?, ?)";
	private static final String retrievalInformationSQL = "INSERT INTO retrieval_information(item_name, city, staff_name) VALUES(?, ?, ?)";
	private static final String exportInformationSQL = "INSERT INTO export_information(item_name, city, tax, staff_name) VALUES(?, ?, ?, ?)";
	private static final String importInformationSQL = "INSERT INTO import_information(item_name, city, tax, staff_name) VALUES(?, ?, ?, ?)";
	private static final String itemSQL = "INSERT INTO item(name, type, price, state) VALUES(?, ?, ?, ?)";
	private static final String shipSQL = "INSERT INTO ship(item_name, ship_name, company) VALUES(?, ?, ?)";
	private static final String staffSQL = "INSERT INTO staff(name, password, type, city, gender, phone_number, birth_year, company) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

	@Override
	public void $import(String recordsCSV, String staffsCSV) {
		try {
			this.rootConn.prepareStatement("ALTER TABLE delivery_information DISABLE TRIGGER ALL").execute();
			this.rootConn.prepareStatement("ALTER TABLE retrieval_information DISABLE TRIGGER ALL").execute();
			this.rootConn.prepareStatement("ALTER TABLE export_information DISABLE TRIGGER ALL").execute();
			this.rootConn.prepareStatement("ALTER TABLE import_information DISABLE TRIGGER ALL").execute();
			this.rootConn.prepareStatement("ALTER TABLE ship DISABLE TRIGGER ALL").execute();
			this.rootConn.prepareStatement("ALTER TABLE container DISABLE TRIGGER ALL").execute();
			Scanner scanner = new Scanner(staffsCSV);
			Scanner valueScanner = null;
			this.rootConn.setAutoCommit(false);
			int index = 0;
			Calendar cal = Calendar.getInstance();
			int currentYear = cal.get(Calendar.YEAR);
			int birthYear = 0;
			boolean gender = false;
			PreparedStatement statement = rootConn.prepareStatement(staffSQL);
			scanner.nextLine();
			while (scanner.hasNextLine()) {
				valueScanner = new Scanner(scanner.nextLine());
				valueScanner.useDelimiter(",");
				String password = null;
				String type = null;
				String city = null;
				String phone_number = null;
				String company = null;
				String name = null;
				while (valueScanner.hasNext()) {
					String data = valueScanner.next();
					if (data.equals("")) {
						data = null;
					}
					if (index == 0) {
						name = data;
					}
					if (index == 1) {
						type = data;
					}
					if (index == 2) {
						company = data;						
					}
					if (index == 3) {
						city = data;
					}
					if (index == 4) {
						if (data.equals("female")) {
							gender = true;
						} else {
							gender = false;
						}
					}
					if (index == 5) {
						birthYear = currentYear - Integer.parseInt(data);
					}
					if (index == 6) {
						phone_number = data;
					}
					if (index == 7) {
						password = data;
					}
					index++;
				}
				index = 0;
				statement.setString(1, name);
				statement.setString(2, password);
				statement.setString(3, type);
				statement.setString(4, city);
				statement.setBoolean(5, gender);
				statement.setString(6, phone_number);
				statement.setInt(7, birthYear);
				statement.setString(8, company);
				statement.addBatch();
			}
			statement.executeBatch();
			scanner.close();
			scanner = new Scanner(recordsCSV);
			valueScanner = null;
			
			PreparedStatement containerStatement = this.rootConn.prepareStatement(containerSQL);
			PreparedStatement deliveryInformationStatement = this.rootConn.prepareStatement(deliveryInformationSQL);
			PreparedStatement retrievalInformationStatement = this.rootConn.prepareStatement(retrievalInformationSQL);
			PreparedStatement exportInformationStatement = this.rootConn.prepareStatement(exportInformationSQL);
			PreparedStatement importInformationStatement = this.rootConn.prepareStatement(importInformationSQL);
			PreparedStatement itemStatement = this.rootConn.prepareStatement(itemSQL);
			PreparedStatement shipStatement = this.rootConn.prepareStatement(shipSQL);
			scanner.nextLine();
			int cnt = 0;
			String data = null;
			while (scanner.hasNextLine()) {
				
				String itemName = null;
				String itemClass = null;
				String retrievalCity = null;
				String retrievalCourier = null;
				String deliveryCity = null;
				String deliveryCourier = null;
				String exportCity = null;
				String importCity = null;
				String exportOfficer = null;
				String importOfficer = null;
				String containerCode = null;
				String containerType = null;
				String shipName = null;
				String companyName = null;
				String itemState = null;
				
				double itemPrice = 0;
				double exportTax = 0;
				double importTax = 0;
				
				valueScanner = new Scanner(scanner.nextLine());
				valueScanner.useDelimiter(",");
				
				while (valueScanner.hasNext()) {
					data = valueScanner.next();
					if (data.equals(""))
						data = null;
					if (index == 0) {
						itemName = data;
					}
					if (index == 1) {
						itemClass = data;
					}
					if (index == 2) {
						itemPrice = Double.parseDouble(data);
					}
					if (index == 3) {
						retrievalCity = data;
					}
					if (index == 4) {
						retrievalCourier = data;
					}
					if (index == 5) {
						deliveryCity = data;
					}
					if (index == 6) {
						deliveryCourier = data;
					}
					if (index == 7) {
						exportCity = data;
					}
					if (index == 8) {
						importCity = data;
					}
					if (index == 9) {
						exportTax = Double.parseDouble(data);
					}
					if (index == 10) {
						importTax = Double.parseDouble(data);
					}
					if (index == 11) {
						exportOfficer = data;
					}
					if (index == 12) {
						importOfficer = data;
					}
					if (index == 13) {
						containerCode = data;
					}
					if (index == 14) {
						containerType = data;
					}
					if (index == 15) {
						shipName = data;
					}
					if (index == 16) {
						companyName = data;
					}
					if (index == 17) {
						itemState = data;
					}
					index++;
				}
				
				index = 0;
				cnt++;
				
				containerStatement.setString(1, itemName);
				containerStatement.setString(2, containerCode);
				containerStatement.setString(3, containerType);
				containerStatement.addBatch();
				
				deliveryInformationStatement.setString(1, itemName);
				deliveryInformationStatement.setString(2, deliveryCity);
				deliveryInformationStatement.setString(3, deliveryCourier);
				deliveryInformationStatement.addBatch();
				
				exportInformationStatement.setString(1, itemName);
				exportInformationStatement.setString(2, exportCity);
				exportInformationStatement.setDouble(3, exportTax);
				exportInformationStatement.setString(4, exportOfficer);
				exportInformationStatement.addBatch();
				
				importInformationStatement.setString(1, itemName);
				importInformationStatement.setString(2, importCity);
				importInformationStatement.setDouble(3, importTax);
				importInformationStatement.setString(4, importOfficer);
				importInformationStatement.addBatch();
				
				itemStatement.setString(1, itemName);
				itemStatement.setString(2, itemClass);
				itemStatement.setDouble(3, itemPrice);
				itemStatement.setString(4, itemState);
				itemStatement.addBatch();
				
				retrievalInformationStatement.setString(1, itemName);
				retrievalInformationStatement.setString(2, retrievalCity);
				retrievalInformationStatement.setString(3, retrievalCourier);
				retrievalInformationStatement.addBatch();
				
				shipStatement.setString(1, itemName);
				shipStatement.setString(2, shipName);
				shipStatement.setString(3, companyName);
				shipStatement.addBatch();
				if (cnt % 3000 == 0) {
					containerStatement.executeBatch();
					deliveryInformationStatement.executeBatch();
					exportInformationStatement.executeBatch();
					importInformationStatement.executeBatch();
					itemStatement.executeBatch();
					retrievalInformationStatement.executeBatch();
					shipStatement.executeBatch();
				}
			}
			containerStatement.executeBatch();
			deliveryInformationStatement.executeBatch();
			exportInformationStatement.executeBatch();
			importInformationStatement.executeBatch();
			itemStatement.executeBatch();
			retrievalInformationStatement.executeBatch();
			shipStatement.executeBatch();
			
			this.rootConn.prepareStatement("ALTER TABLE delivery_information ENABLE TRIGGER ALL").execute();
			this.rootConn.prepareStatement("ALTER TABLE retrieval_information ENABLE TRIGGER ALL").execute();
			this.rootConn.prepareStatement("ALTER TABLE export_information ENABLE TRIGGER ALL").execute();
			this.rootConn.prepareStatement("ALTER TABLE import_information ENABLE TRIGGER ALL").execute();
			this.rootConn.prepareStatement("ALTER TABLE ship ENABLE TRIGGER ALL").execute();
			this.rootConn.prepareStatement("ALTER TABLE container ENABLE TRIGGER ALL").execute();
			this.rootConn.commit();
			this.rootConn.setAutoCommit(true);
			scanner.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//Need Type Check?
	private static final String checkUserSQL = "SELECT * FROM staff where name = ? AND password = ? ";
	
	
	private boolean checkUser(LogInfo logInfo) throws SQLException {
		PreparedStatement statement = this.rootConn.prepareStatement(checkUserSQL);
		statement.setString(1, logInfo.name());
		statement.setString(2, logInfo.password());
		ResultSet rs = statement.executeQuery();
		if (!rs.next()) {
			return false;
		}
		return getStaffTypeByDescription(rs.getString(3)) == logInfo.type();
	}
	
	


	//Company Manager User
	private static final String getImportTaxRateSQL = "with" +
			"    tmp1 as (select name, price from item where type = ?)," +
			"    tmp2 as (select item_name, tax from import_information where city = ?)" +
			"    select case (select count(*) from tmp1) when 0 then -1 else (" +
			"       select case (select count(*) from tmp2) when 0 then -1 else (" +
			"            select  sum(tax)/sum(price) as ImportTaxRate from (" +
			"            select tmp1.name, tmp1.price, tmp2.tax from tmp1" +
			"            inner join tmp2" +
			"            on tmp1.name = tmp2.item_name) as foo" +
			"       )" +
			"       end" +
			"    )" +
			"    end";
	@Override
	public double getImportTaxRate(LogInfo logInfo, String city, String itemType) {
		try {
			if (!checkUser(logInfo)) {
				return -1;
			}
			Connection userConnection = getUserConnection(logInfo.type());
			PreparedStatement statement = userConnection.prepareStatement(getImportTaxRateSQL);
			statement.setString(1, itemType);
			statement.setString(2, city);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return rs.getDouble(1);
			} else {
				return -1;
			}
		} catch (SQLException e) {
			feedbackThrowable(e);
		}
		return -1;
	}
	
	private static void feedbackThrowable(Throwable e) {
		e.printStackTrace();
	}
	
	private Connection getUserConnection(StaffType type) {
		if (type == StaffType.CompanyManager) {
			return this.companyManagerConn;
		}
		if (type == StaffType.SeaportOfficer) {
			return this.seaportConn;
		}
		if (type == StaffType.Courier) {
			return this.courierConn;
		}
		return this.sustcManagerConn;
	}

	//Company Manager User


	private static final String getExportTaxRateSQL = "with" +
			"    tmp1 as (select name, price from item where type = ?)," +
			"    tmp2 as (select item_name, tax from export_information where city = ?)" +
			"    select case (select count(*) from tmp1) when 0 then -1 else (" +
			"       select case (select count(*) from tmp2) when 0 then -1 else (" +
			"            select  sum(tax)/sum(price) as ExportTaxRate from (" +
			"            select tmp1.name, tmp1.price, tmp2.tax from tmp1" +
			"            inner join tmp2" +
			"            on tmp1.name = tmp2.item_name) as foo" +
			"       )" +
			"       end" +
			"    )" +
			"    end";
	@Override
	public double getExportTaxRate(LogInfo logInfo, String city, String itemType) {
		try {
			if (!checkUser(logInfo)) {
				return -1;
			}
			Connection userConnection = getUserConnection(logInfo.type());
			PreparedStatement statement = userConnection.prepareStatement(getExportTaxRateSQL);
			statement.setString(1, itemType);
			statement.setString(2, city);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return rs.getDouble(1);
			} else {
				return -1;
			}
		} catch (SQLException e) {
			feedbackThrowable(e);
		}
		return -1;
	}
	private static final String getItemContainerCode = "select code from container where item_name = ?";
	private static final String checkContainerCode = "select case (select count(*) from container where code = ?) when 0 then false else true end";
	private static final String checkPackingContainerSQL = "with" +
			"    tmp1 as (select name from item where state = 'Packing to Container' or state = 'Shipping' or state = 'Unpacking from Container')," +
			"    tmp2 as (select item_name from container where code = ? and item_name != ?)" +
			"    select case (select count(tmp1.name) from tmp1 inner join tmp2 on tmp1.name = tmp2.item_name) when 0 then true else false end";

	private static final String getContainerTypeSQL = "select type from container where code = ?";

	private static final String updateItemContainerCode = "update container set code = ?, type = ? where item_name = ?";
	//Company Manager User
	@Override
	public boolean loadItemToContainer(LogInfo logInfo, String itemName, String containerCode) {
		try {
			if (!checkUser(logInfo)) {
				return false;
			}
			Connection userConnection = getUserConnection(logInfo.type());
			PreparedStatement statement; ResultSet rs;
			statement = userConnection.prepareStatement(checkItemSQL);
			statement.setString(1, itemName);
			rs = statement.executeQuery();
			if (rs.next()) {
				if (rs.getBoolean(1)) return false;
			} else {
				return false;
			}
			statement.close();
			statement = userConnection.prepareStatement(getItemStateSQL);
			statement.setString(1, itemName);

			rs = statement.executeQuery();
			if (rs.next()) {
				if (!rs.getString(1).equals(getDescriptionByItemState(ItemState.PackingToContainer))) return false;
			} else {
				return false;
			}
			statement.close();
			statement = userConnection.prepareStatement(getItemContainerCode);
			statement.setString(1, itemName);
			rs = statement.executeQuery();
			String nowContainerCode;
			if (rs.next()) {
				nowContainerCode = rs.getString(1);
			} else {
				return false;
			}

			statement = userConnection.prepareStatement(checkContainerCode);
			statement.setString(1, containerCode);
			rs = statement.executeQuery();
			if (rs.next()) {
				if (!rs.getBoolean(1)) return false;
			} else {
				return false;
			}

			if (nowContainerCode == null) {
				statement.close();
				statement = userConnection.prepareStatement(checkPackingContainerSQL);
				statement.setString(1, containerCode);
				statement.setNull(2, Types.VARCHAR);
				rs = statement.executeQuery();
				if (rs.next()) {
					if (!rs.getBoolean(1)) return false;
				} else {
					return false;
				}

				String containerType = null;
				statement.close();
				statement = userConnection.prepareStatement(getContainerTypeSQL);
				statement.setString(1, containerCode);
				rs = statement.executeQuery();
				if (rs.next()) {
					containerType = rs.getString(1);
				} else {
					return false;
				}
				statement.close();
				statement = userConnection.prepareStatement(updateItemContainerCode);
				statement.setString(1, containerCode);
				statement.setString(2, containerType);
				statement.setString(3, itemName);
				statement.executeUpdate();

			} else {
				statement.close();
				statement = userConnection.prepareStatement(checkPackingContainerSQL);
				statement.setString(1, containerCode);
				statement.setString(2, itemName);
				rs.close();
				rs = statement.executeQuery();
				if (rs.next()) {
					if (!rs.getBoolean(1)) return false;
				} else {
					return false;
				}

				String containerType = null;
				statement = userConnection.prepareStatement(getContainerTypeSQL);
				statement.setString(1, containerCode);
				rs = statement.executeQuery();
				if (rs.next()) {
					containerType = rs.getString(1);
				} else {
					return false;
				}

				statement = userConnection.prepareStatement(updateItemContainerCode);
				statement.setString(1, containerCode);
				statement.setString(2, containerType);
				statement.setString(3, itemName);
				statement.executeUpdate();

			}

			return true;
		} catch (SQLException e) {
			feedbackThrowable(e);
		}
		return false;
	}
	
	//Company Manager User
	private static final String getItemNameFromContainerCodeSQL = "with" +
			"    tmp1 as (select item_name from container where code = ?)," +
			"    tmp2 as (select name from item where state = 'Packing to Container')" +
			"    select tmp1.item_name from tmp1 inner join tmp2 on tmp2.name = tmp1.item_name;";
	private static final String checkShipSailing = "with" +
			"    tmp1 as (select item_name from ship where ship_name = ?)," +
			"    tmp2 as (select name from item where state = 'Shipping')" +
			"    select case (select count(*) from tmp1) when 0 then false else (" +
			"        select case (select count(tmp1.item_name) from tmp1 inner join tmp2 on tmp1.item_name = tmp2.name)" +
			"        when 0 then true else false end" +
			"    ) end";
	private static final String getShipCompany = "select company from ship where ship_name = ?";
	private static final String getStaffCompany = "select company from staff where name = ?";
	private static final String getItemCompany = "select company from ship where item_name = ?";
	private static final String updateItemShip = "update ship set ship_name = ? where item_name = ?";
	@Override
	public boolean loadContainerToShip(LogInfo logInfo, String shipName, String containerCode) {
		try {
			if (!checkUser(logInfo)) {
				return false;
			}
			Connection userConnection = getUserConnection(logInfo.type());
			PreparedStatement statement; ResultSet rs;
			statement = userConnection.prepareStatement(getItemNameFromContainerCodeSQL);
			statement.setString(1, containerCode);
			rs = statement.executeQuery();
			String itemName = null;
			if (rs.next()) {
				itemName = rs.getString(1);
			} else {
				return false;
			}
			statement = userConnection.prepareStatement(getItemStateSQL);
			statement.setString(1, itemName);
			rs = statement.executeQuery();
			String itemState = null;
			if (rs.next()) {
				itemState = rs.getString(1);
			} else {
				return false;
			}
			if (!itemState.equals(getDescriptionByItemState(ItemState.PackingToContainer))) return false;
			statement = userConnection.prepareStatement(checkShipSailing);
			statement.setString(1, shipName);
			rs = statement.executeQuery();
			if (rs.next()) {
				if (!rs.getBoolean(1)) return false;
			} else {
				return false;
			}
			String shipCompany = null;
			statement = userConnection.prepareStatement(getShipCompany);
			statement.setString(1, shipName);
			rs = statement.executeQuery();
			if (rs.next()) {
				shipCompany = rs.getString(1);
			} else {
				return false;
			}
			String staffCompany = null;
			statement = userConnection.prepareStatement(getStaffCompany);
			statement.setString(1, logInfo.name());
			rs = statement.executeQuery();
			if (rs.next()) {
				staffCompany = rs.getString(1);
			} else {
				return false;
			}
			if (!shipCompany.equals(staffCompany)) return false;
			String itemCompany = null;
			statement = userConnection.prepareStatement(getItemCompany);
			statement.setString(1, itemName);
			rs = statement.executeQuery();
			if (rs.next()) {
				itemCompany = rs.getString(1);
			} else {
				return false;
			}
			if (!itemCompany.equals(staffCompany)) return false;
			statement = userConnection.prepareStatement(updateItemState);
			statement.setString(1, getDescriptionByItemState(ItemState.WaitingForShipping));
			statement.setString(2, itemName);
			statement.executeUpdate();

			statement = userConnection.prepareStatement(updateItemShip);
			statement.setString(1, shipName);
			statement.setString(2, itemName);
			statement.executeUpdate();
			return true;
		} catch (SQLException e) {
			feedbackThrowable(e);
		}
		return false;
	}
	

	
	//Company Manager User
	private static final String getWaitingShippingItems = "with" +
			"    tmp1 as (select item_name from ship where ship_name = ? and company = ?)," +
			"    tmp2 as (select name from item where state = 'Waiting for Shipping')" +
			"    select tmp1.item_name from tmp1 inner join tmp2 on tmp2.name = tmp1.item_name";
	@Override
	public boolean shipStartSailing(LogInfo logInfo, String shipName) {
		try {
			if (!checkUser(logInfo)) {
				return false;
			}
			Connection userConnection = getUserConnection(logInfo.type());
			PreparedStatement statement; ResultSet rs;
			statement = userConnection.prepareStatement(checkShipSailing);
			statement.setString(1, shipName);
			rs = statement.executeQuery();
			if (rs.next()) {
				if (!rs.getBoolean(1)) return false;
			} else {
				return false;
			}
			statement = userConnection.prepareStatement(getStaffCompany);
			statement.setString(1, logInfo.name());
			rs = statement.executeQuery();
			String staffCompany = null;
			if (rs.next()) {
				staffCompany = rs.getString(1);
			} else {
				return false;
			}
			statement = userConnection.prepareStatement(getWaitingShippingItems);
			statement.setString(1, shipName);
			statement.setString(2, staffCompany);
			rs = statement.executeQuery();
			ArrayList<String> list = new ArrayList<>();
			if (rs.next()) {
				do {
					list.add(rs.getString(1));
				} while (rs.next());
			} else {
				return false;
			}
			for (String item : list) {
				statement = userConnection.prepareStatement(updateItemState);
				statement.setString(1, getDescriptionByItemState(ItemState.Shipping));
				statement.setString(2, item);
				statement.executeUpdate();
			}
			return true;
		} catch (SQLException e) {
			feedbackThrowable(e);
		}
		return false;
	}
	
	//Company Manager User
	@Override
	public boolean unloadItem(LogInfo logInfo, String item) {
		try {
			if (!checkUser(logInfo)) {
				return false;
			}
			Connection userConnection = getUserConnection(logInfo.type());
			PreparedStatement statement; ResultSet rs;
			statement = userConnection.prepareStatement(getItemStateSQL);
			statement.setString(1, item);
			rs = statement.executeQuery();
			if (rs.next()) {
				if (!rs.getString(1).equals(getDescriptionByItemState(ItemState.Shipping))) return false;
			} else {
				return false;
			}
			statement = userConnection.prepareStatement(updateItemState);
			statement.setString(1, getDescriptionByItemState(ItemState.UnpackingFromContainer));
			statement.setString(2, item);
			statement.executeUpdate();
			return true;
		} catch (SQLException e) {
			feedbackThrowable(e);
		}
		return false;
	}
	
	//Company Manager User
	@Override
	public boolean itemWaitForChecking(LogInfo logInfo, String item) {
		try {
			if (!checkUser(logInfo)) {
				return false;
			}
			Connection userConnection = getUserConnection(logInfo.type());
			PreparedStatement statement; ResultSet rs;
			statement = userConnection.prepareStatement(getItemStateSQL);
			statement.setString(1, item);
			rs = statement.executeQuery();
			if (rs.next()) {
				if (!rs.getString(1).equals(getDescriptionByItemState(ItemState.UnpackingFromContainer))) return false;
			} else {
				return false;
			}
			statement = userConnection.prepareStatement(updateItemState);
			statement.setString(1, getDescriptionByItemState(ItemState.ImportChecking));
			statement.setString(2, item);
			statement.executeUpdate();
			return true;
		} catch (SQLException e) {
			feedbackThrowable(e);
		}
		return false;
	}
	
	//Courier User
	private static final String checkItemSQL = "select case (select count(*) from item where name = ?)" +
			"    when 0 then true" +
			"    else false" +
			" end";
//	private static final String checkContainerSQL = "select * from container where item_name = ?";
//	private static final String checkShipSQL = "select * from ship where item_name = ?";

	private static final String checkStaffCity = "select case (select count(*) from staff where name = ? and city = ?)" +
			"	when 0 then false" +
			"	else true" +
			" end";

	public static int getOrdinal(ItemState state) {
		if (state == null) {
			return -1;
		} else return state.ordinal();
	}

	public boolean checkItem(ItemInfo itemInfo, String retrievalCourier, Connection userConnection) {
		if (itemInfo == null) return false;
		try {
			PreparedStatement statement;
			statement = userConnection.prepareStatement(checkItemSQL);
			statement.setString(1, itemInfo.name());
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				if (rs.getBoolean(1) == false) return false;
			} else {
				return false;
			}
			if (itemInfo.name() == null) return false;
			if (itemInfo.$class() == null) return false;
			if (itemInfo.retrieval() == null) return false;
			if (itemInfo.retrieval().city() == null) return false;
			if (itemInfo.retrieval().courier() != null && !itemInfo.retrieval().courier().equals(retrievalCourier))
				return false;
			statement = userConnection.prepareStatement(checkStaffCity);
			statement.setString(1, retrievalCourier);
			statement.setString(2, itemInfo.retrieval().city());
			rs = statement.executeQuery();
			if (rs.next()) {
				if (rs.getBoolean(1) == false) return false;
			} else return false;
			if (itemInfo.delivery() == null) return false;
			if (itemInfo.delivery().city() == null) return false;
			if (itemInfo.delivery().courier() == null && getOrdinal(itemInfo.state()) >= 10) return false;
			if (itemInfo.delivery().courier() != null) {
				statement = userConnection.prepareStatement(checkStaffCity);
				statement.setString(1, itemInfo.delivery().courier());
				statement.setString(2, itemInfo.delivery().city());
				rs = statement.executeQuery();
				if (rs.next()) {
					if (rs.getBoolean(1) == false) return false;
				} else return false;
			}

			if (itemInfo.export() == null) return false;
			if (itemInfo.export().city() == null) return false;
			if (itemInfo.export().officer() == null && getOrdinal(itemInfo.state()) >= 2) return false;
			if (itemInfo.export().officer() != null) {
				statement = userConnection.prepareStatement(checkStaffCity);
				statement.setString(1, itemInfo.export().officer());
				statement.setString(2, itemInfo.export().city());
				rs = statement.executeQuery();
				if (rs.next()) {
					if (rs.getBoolean(1) == false) return false;
				} else return false;
			}

			if (itemInfo.$import() == null) return false;
			if (itemInfo.$import().city() == null) return false;
			if (itemInfo.$import().officer() == null && getOrdinal(itemInfo.state()) >= 8) return false;
			if (itemInfo.$import().officer() != null) {
				statement = userConnection.prepareStatement(checkStaffCity);
				statement.setString(1, itemInfo.$import().officer());
				statement.setString(2, itemInfo.$import().city());
				rs = statement.executeQuery();
				if (rs.next()) {
					if (rs.getBoolean(1) == false) return false;
				} else return false;
			}

			if (itemInfo.$import().city().equals(itemInfo.export().city())) return false;

			double exportTaxRate = itemInfo.export().tax() / itemInfo.price();
			double importTaxRate = itemInfo.$import().tax() / itemInfo.price();

			statement = userConnection.prepareStatement(getImportTaxRateSQL);
			statement.setString(1, itemInfo.$class());
			statement.setString(2, itemInfo.$import().city());
			rs = statement.executeQuery();
			if (rs.next()) {
				double RealImportTaxRate = rs.getDouble(1);
				if (Math.abs(RealImportTaxRate + 1) > 1e-7 && Math.abs(RealImportTaxRate - importTaxRate) > 1e-7) return false;
			} else {
				return false;
			}
			statement = userConnection.prepareStatement(getExportTaxRateSQL);
			statement.setString(1, itemInfo.$class());
			statement.setString(2, itemInfo.export().city());
			rs = statement.executeQuery();
			if (rs.next()) {
				double RealExportTaxRate = rs.getDouble(1);
				if (Math.abs(RealExportTaxRate + 1) > 1e-7 && Math.abs(RealExportTaxRate - exportTaxRate) > 1e-7) return false;
			} else {
				return false;
			}
//
//			String containerCode = null, containerType = null;
//			statement = userConnection.prepareStatement(checkContainerSQL);
//			statement.setString(1, itemInfo.name());
//			rs = statement.executeQuery();
//			if (rs.next()) {
//				containerCode = rs.getString(2);
//				containerType = rs.getString(3);
//				if (containerCode == null && containerType != null) return false;
//				if (containerCode != null && containerType == null) return false;
//				if (containerCode == null && containerType == null && getOrdinal(itemInfo.state()) >= 4) return false;
//			} else {
//				if (getOrdinal(itemInfo.state()) >= 4) return false;
//			}
//			String shipName = null, company = null;
//			statement = userConnection.prepareStatement(checkShipSQL);
//			statement.setString(1, itemInfo.name());
//			rs = statement.executeQuery();
//			if (rs.next()) {
//				shipName = rs.getString(2);
//				company = rs.getString(3);
//				if (company == null) return false;
//				if (shipName == null && getOrdinal(itemInfo.state()) >= 6) return false;
//			} else {
//				if (getOrdinal(itemInfo.state()) >= 6) return false;
//			}
		} catch (SQLException e) {
			feedbackThrowable(e);
			return false;
		}
		return true;
	}
	private static final String insertNewItemSQL2 = "insert into delivery_information (item_name, city, staff_name) values (?, ?, ?)";
	private static final String insertNewItemSQL3 = "insert into export_information (item_name, city, tax, staff_name) values (?, ?, ?, ?)";
	private static final String insertNewItemSQL4 = "insert into import_information (item_name, city, tax, staff_name) values (?, ?, ?, ?)";
	private static final String insertNewItemSQL5 = "insert into item (name, type, price, state) values (?, ?, ?, ?)";
	private static final String insertNewItemSQL6 = "insert into retrieval_information (item_name, city, staff_name) values (?, ?, ?)";

	@Override
	public boolean newItem(LogInfo logInfo, ItemInfo itemInfo) {
		try {
			if (!checkUser(logInfo)) {
				return false;
			}
			Connection userConnection = getUserConnection(logInfo.type());
			if (checkItem(itemInfo, logInfo.name(), userConnection) == false) {
				return false;
			}
			if (getOrdinal(itemInfo.state()) > 0) {
				return false;
			}
			PreparedStatement statement;
			statement = userConnection.prepareStatement(insertNewItemSQL5);
			statement.setString(1, itemInfo.name());
			statement.setString(2, itemInfo.$class());
			statement.setDouble(3, itemInfo.price());
			statement.setString(4, "Picking-up");
			statement.execute();

			statement.close();
			statement = userConnection.prepareStatement(insertNewItemSQL2);
			statement.setString(1, itemInfo.name());
			if (itemInfo.delivery().city() != null)
				statement.setString(2, itemInfo.delivery().city());
			else
				statement.setNull(2, Types.VARCHAR);
			if (itemInfo.delivery().courier() != null)
				statement.setString(3, itemInfo.delivery().courier());
			else
				statement.setNull(3, Types.VARCHAR);
			statement.execute();

			statement = userConnection.prepareStatement(insertNewItemSQL3);
			statement.setString(1, itemInfo.name());

			if (itemInfo.export().city() != null) statement.setString(2, itemInfo.export().city());
			else statement.setNull(2, Types.VARCHAR);
			statement.setDouble(3, itemInfo.export().tax());
			if (itemInfo.export().officer() != null) statement.setString(4, itemInfo.export().officer());
			else statement.setNull(4, Types.VARCHAR);
			statement.execute();


			statement = userConnection.prepareStatement(insertNewItemSQL4);
			statement.setString(1, itemInfo.name());

			statement.setString(2, itemInfo.$import().city());
			statement.setDouble(3, itemInfo.$import().tax());
			if (itemInfo.$import().officer() != null)
				statement.setString(4, itemInfo.$import().officer());
			else
				statement.setNull(4, Types.VARCHAR);
			statement.execute();



			statement = userConnection.prepareStatement(insertNewItemSQL6);
			statement.setString(1, itemInfo.name());
			statement.setString(2, itemInfo.retrieval().city());
			statement.setString(3, logInfo.name());
			statement.execute();

			return true;
		} catch (SQLException e) {
			feedbackThrowable(e);
		}

		return false;
	}

	//Courier User
	
	private static final String getItemStateSQL = "select state from item where name = ?";
	private static final String getItemRetrievalCourier = "select staff_name from retrieval_information where item_name = ?";
	private static final String getItemDeliveryCourierAndCity = "select staff_name, city from delivery_information where item_name = ?";
	private static final String updateItemState = "update item set state = ? where name = ?";
	private static final String updateItemDeliveryCourier = "update delivery_information set staff_name = ? where item_name = ?";
	private static final String checkItemDeliveryCourier = "select case " +
			"(select count(*) from staff where name = ? and city = ?) when 0 then false" +
			" else true end";
	@Override
	public boolean setItemState(LogInfo logInfo, String name, ItemState s) {
		try {
			if (!checkUser(logInfo)) {
				return false;
			}
			Connection userConnection = getUserConnection(logInfo.type());
			PreparedStatement statement = userConnection.prepareStatement(checkItemSQL);
			statement.setString(1, name);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				if (rs.getBoolean(1) == true) return false;
			} else {
				return false;
			}
			statement.close();
			statement = userConnection.prepareStatement(getItemStateSQL);
			statement.setString(1, name);
			rs = statement.executeQuery();
			String state = null;
			if (rs.next()) {
				state = rs.getString(1);
			} else {
				return false;
			}
			if (state.equals(getDescriptionByItemState(ItemState.PickingUp))) {
				String RetrievalCourier = null;
				statement.close();
				statement = userConnection.prepareStatement(getItemRetrievalCourier);
				statement.setString(1, name);
				rs = statement.executeQuery();
				if (rs.next()) {
					RetrievalCourier = rs.getString(1);
					if (RetrievalCourier == null) return false;
					if (!RetrievalCourier.equals(logInfo.name())) return false;
					if (s != ItemState.ToExportTransporting) return false;
					statement = userConnection.prepareStatement(updateItemState);
					statement.setString(1, "To-Export Transporting");
					statement.setString(2, name);
					statement.executeUpdate();
				} else {
					return false;
				}
			} else if (state.equals(getDescriptionByItemState(ItemState.ToExportTransporting))) {
				String RetrievalCourier = null;
				statement.close();
				statement = userConnection.prepareStatement(getItemRetrievalCourier);
				statement.setString(1, name);
				rs.close();
				rs = statement.executeQuery();
				if (rs.next()) {
					RetrievalCourier = rs.getString(1);
					if (RetrievalCourier == null) return false;
					if (!RetrievalCourier.equals(logInfo.name())) return false;
					if (s != ItemState.ExportChecking) return false;
					statement = userConnection.prepareStatement(updateItemState);
					statement.setString(1, "Export Checking");
					statement.setString(2, name);
					statement.executeUpdate();
				} else {
					return false;
				}
			} else if (state.equals(getDescriptionByItemState(ItemState.FromImportTransporting))) {
				String deliveryCourier = null, deliveryCity = null;
				statement.close();
				statement = userConnection.prepareStatement(getItemDeliveryCourierAndCity);
				statement.setString(1, name);
				rs.close();
				rs = statement.executeQuery();
				if (rs.next()) {
					deliveryCourier = rs.getString(1);
					deliveryCity = rs.getString(2);
					if (deliveryCourier == null) {
						statement = userConnection.prepareStatement(checkItemDeliveryCourier);
						statement.setString(1, logInfo.name());
						statement.setString(2, deliveryCity);
						rs = statement.executeQuery();
						if (rs.next()) {
							if (rs.getBoolean(1) == false) return false;
							statement = userConnection.prepareStatement(updateItemDeliveryCourier);
							statement.setString(1, logInfo.name());
							statement.setString(2, name);
							statement.executeUpdate();
						} else {
							return false;
						}
					} else {
						if (!logInfo.name().equals(deliveryCourier)) return false;
						if (s != ItemState.Delivering) return false;
						statement.close();
						statement = userConnection.prepareStatement(updateItemState);
						statement.setString(1, "Delivering");
						statement.setString(2, name);
						statement.executeUpdate();
					}
				} else {
					return false;
				}
			} else if (state.equals(getDescriptionByItemState(ItemState.Delivering))) {
				String deliveryCourier = null;
				statement.close();
				statement = userConnection.prepareStatement(getItemDeliveryCourierAndCity);
				statement.setString(1, name);
				rs.close();
				rs = statement.executeQuery();
				if (rs.next()) {
					deliveryCourier = rs.getString(1);
					if (deliveryCourier == null) return false;
					if (!deliveryCourier.equals(logInfo.name())) return false;
					if (s != ItemState.Finish) return false;
					statement = userConnection.prepareStatement(updateItemState);
					statement.setString(1, getDescriptionByItemState(ItemState.Finish));
					statement.setString(2, name);
					statement.executeUpdate();
				} else {
					return false;
				}
			} else {
				return false;
			}
			return true;
		} catch (SQLException e) {
			feedbackThrowable(e);
		}

		return false;
	}
	
	//Seaport Officer User
	private static final String getStaffCity = "select city from staff where name = ?";
	private static final String getItemAtPort = "(select name from item where state = 'Import Checking'" +
			"INTERSECT (select item_name from import_information where city = ?))" +
			"union" +
			"(select name from item where state = 'Export Checking'" +
			"INTERSECT (select item_name from export_information where city = ?))";
	@Override
	public String[] getAllItemsAtPort(LogInfo logInfo) {
		try {
			if (!checkUser(logInfo)) {
				return new String[0];
			}
			Connection userConnection = getUserConnection(logInfo.type());
			List<String> res = new ArrayList<>();

			PreparedStatement statement; ResultSet rs;
			statement = userConnection.prepareStatement(getStaffCity);
			statement.setString(1, logInfo.name());
			rs = statement.executeQuery();

			String staffCity = "";

			if (rs.next()) {
				staffCity = rs.getString(1);
			} else {
				return new String[0];
			}

			statement = userConnection.prepareStatement(getItemAtPort);
			statement.setString(1, staffCity);
			statement.setString(2, staffCity);
			rs = statement.executeQuery();
			if (rs.next()) {
				do {
					res.add(rs.getString(1));
				} while (rs.next());
			} else {
				return new String[0];
			}

			String[] ans = new String[res.size()];
			for (int i = 0; i < res.size(); i++) {
				ans[i] = res.get(i);
			}
			return ans;
		} catch (SQLException e) {
			feedbackThrowable(e);
		}
		return new String[0];
	}
	
	//Seaport Officer User
	private static final String getItemExportStaff = "select staff_name from export_information where item_name = ?";
	private static final String updateItemExportStaff = "update export_information set staff_name = ? where item_name = ?";
	private static final String getItemImportStaff = "select staff_name from import_information where item_name = ?";
	private static final String updateItemImportStaff = "update import_information set staff_name = ? where item_name = ?";
	@Override
	public boolean setItemCheckState(LogInfo logInfo, String itemName, boolean success) {
		try {
			if (!checkUser(logInfo)) {
				return false;
			}
			Connection userConnection = getUserConnection(logInfo.type());
			PreparedStatement statement = userConnection.prepareStatement(getItemStateSQL);
			statement.setString(1, itemName);
			ResultSet rs = statement.executeQuery();
			String currentItemState = null;
			if (rs.next()) {
				currentItemState = rs.getString(1);
			} else {
				return false;
			}
			if (currentItemState.equals(getDescriptionByItemState(ItemState.ExportChecking))) {
				PreparedStatement staffStatement = userConnection.prepareStatement(getItemExportStaff);
				staffStatement.setString(1, itemName);
				String itemStaff = null;
				rs = staffStatement.executeQuery();
				if (rs.next()) {
					itemStaff = rs.getString(1);
				} else {
					return false;
				}

				if (itemStaff != null && !itemStaff.equals(logInfo.name())) return false;

				if (success) {
					PreparedStatement updateStatement = userConnection.prepareStatement(updateItemState);
					updateStatement.setString(1, getDescriptionByItemState(ItemState.PackingToContainer));
					updateStatement.setString(2, itemName);
					updateStatement.execute();
				} else {
					PreparedStatement updateStatement = userConnection.prepareStatement(updateItemState);
					updateStatement.setString(1, getDescriptionByItemState(ItemState.ExportCheckFailed));
					updateStatement.setString(2, itemName);
					updateStatement.execute();
				}

				if (itemStaff == null) {
					PreparedStatement updateStatement = userConnection.prepareStatement(updateItemExportStaff);
					updateStatement.setString(1, logInfo.name());
					updateStatement.setString(2, itemName);
					updateStatement.execute();
				}

			} else if (currentItemState.equals(getDescriptionByItemState(ItemState.ImportChecking))) {
				PreparedStatement importStatement = userConnection.prepareStatement(getItemImportStaff);
				importStatement.setString(1, itemName);
				String itemStaff = null;
				rs.close();
				rs = importStatement.executeQuery();
				if (rs.next()) {
					itemStaff = rs.getString(1);
				} else {
					return false;
				}

				if (itemStaff != null && !itemStaff.equals(logInfo.name())) return false;

				if (success) {
					PreparedStatement updateStatement = userConnection.prepareStatement(updateItemState);
					updateStatement.setString(1, getDescriptionByItemState(ItemState.FromImportTransporting));
					updateStatement.setString(2, itemName);
					updateStatement.execute();
				} else {
					PreparedStatement updateStatement = userConnection.prepareStatement(updateItemState);
					updateStatement.setString(1, getDescriptionByItemState(ItemState.ImportCheckFailed));
					updateStatement.setString(2, itemName);
					updateStatement.execute();
				}

				if (itemStaff == null) {
					PreparedStatement updateStatement = userConnection.prepareStatement(updateItemImportStaff);
					updateStatement.setString(1, logInfo.name());
					updateStatement.setString(2, itemName);
					updateStatement.execute();
				}
			} else {
				return false;
			}
			return true;
		} catch (SQLException e) {
			feedbackThrowable(e);
		}
		return false;
	}
	
	
	private static final String getCompanyCountSQL = "SELECT count(*) FROM (SELECT DISTINCT company FROM staff WHERE company IS NOT NULL) tb;";
	//SUSTC Department Manager User
	@Override
	public int getCompanyCount(LogInfo logInfo) {
		try {
			if (!checkUser(logInfo)) {
				return -1;
			}
			Connection userConnection = getUserConnection(logInfo.type());
			PreparedStatement statement = userConnection.prepareStatement(getCompanyCountSQL);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return 0;
			}
		} catch (SQLException e) {
			feedbackThrowable(e);
		}
		return -1;
	}
	
	private static final String getCityCountSQL = "SELECT count(*) from ("
			+ "    SELECT DISTINCT city FROM ("
			+ "        SELECT DISTINCT city FROM retrieval_information UNION DISTINCT"
			+ "        (SELECT DISTINCT city FROM delivery_information) UNION DISTINCT"
			+ "        (SELECT DISTINCT city FROM export_information) UNION DISTINCT"
			+ "        (SELECT DISTINCT city from import_information)"
			+ "        )t1)t0;";
	//SUSTC Department Manager User
	@Override
	public int getCityCount(LogInfo logInfo) {
		try {
			if (!checkUser(logInfo)) {
				return -1;
			}
			Connection userConnection = getUserConnection(logInfo.type());
			PreparedStatement statement = userConnection.prepareStatement(getCityCountSQL);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return 0;
			}
		} catch (SQLException e) {
			feedbackThrowable(e);
		}
		return -1;
	}
	
	private static final String getCourierCountSQL = "SELECT count(*) FROM staff WHERE type = 'Courier';";
	//SUSTC Department Manager User
	@Override
	public int getCourierCount(LogInfo logInfo) {
		try {
			if (!checkUser(logInfo)) {
				return -1;
			}
			Connection userConnection = getUserConnection(logInfo.type());
			PreparedStatement statement = userConnection.prepareStatement(getCourierCountSQL);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return 0;
			}
		} catch (SQLException e) {
			feedbackThrowable(e);
		}
		return -1;
	}
	
	private static final String getShipCountSQL = "SELECT count(*) FROM (SELECT DISTINCT ship_name FROM ship WHERE ship_name IS NOT NULl)t1;";
	//SUSTC Department Manager User
	@Override
	public int getShipCount(LogInfo logInfo) {
		try {
			if (!checkUser(logInfo)) {
				return -1;
			}
			Connection userConnection = getUserConnection(logInfo.type());
			PreparedStatement statement = userConnection.prepareStatement(getShipCountSQL);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return 0;
			}
		} catch (SQLException e) {
			feedbackThrowable(e);
		}
		return -1;
	}
	
	private static final Map<String, ItemState> stringStateMap = new HashMap<>();
	private static final Map<ItemState, String> stateStringMap = new HashMap<>();
	
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
		
	}
	
	private static String getDescriptionByItemState(ItemState state) {
		return stateStringMap.get(state);
	}
	
	private static ItemState getItemStateByDescription(String message) {
		return stringStateMap.get(message);
	}
	
	private static final String getItemSQL = "SELECT * FROM item WHERE name = ?;";
	private static final String getImportSQL = "SELECT city, staff_name, tax FROM import_information WHERE item_name = ?;";
	private static final String getExportSQL = "SELECT city, staff_name, tax FROM export_information WHERE item_name = ?;";
	private static final String getRetrievalSQL = "SELECT city, staff_name FROM retrieval_information WHERE item_name = ?;";
	private static final String getDeliverySQL = "SELECT city, staff_name FROM delivery_information WHERE item_name = ?;";
	//SUSTC Department Manager User
	@Override
	public ItemInfo getItemInfo(LogInfo logInfo, String s) {
		try {
			if (!checkUser(logInfo)) {
				return null;
			}
			Connection userConnection = getUserConnection(logInfo.type());
			PreparedStatement itemStatement = userConnection.prepareStatement(getItemSQL);
			itemStatement.setString(1, s);
			ResultSet itemRs = itemStatement.executeQuery();
			if (itemRs.next()) {
				PreparedStatement importStatement = userConnection.prepareStatement(getImportSQL);
				importStatement.setString(1, s);
				ResultSet importRs = importStatement.executeQuery();
				importRs.next();
				ImportExportInfo importInfo = new ImportExportInfo(importRs.getString(1), importRs.getString(2), importRs.getDouble(3));
				
				PreparedStatement exportStatement = userConnection.prepareStatement(getExportSQL);
				exportStatement.setString(1, s);
				ResultSet exportRs = exportStatement.executeQuery();
				exportRs.next();
				ImportExportInfo exportInfo = new ImportExportInfo(exportRs.getString(1), exportRs.getString(2), exportRs.getDouble(3));
				
				PreparedStatement retrievalStatement = userConnection.prepareStatement(getRetrievalSQL);
				retrievalStatement.setString(1, s);
				ResultSet retrievalRs = retrievalStatement.executeQuery();
				retrievalRs.next();
				RetrievalDeliveryInfo retrievalInfo = new RetrievalDeliveryInfo(retrievalRs.getString(1), retrievalRs.getString(2));
				
				PreparedStatement deliveryStatement = userConnection.prepareStatement(getDeliverySQL);
				deliveryStatement.setString(1, s);
				ResultSet deliveryRs = deliveryStatement.executeQuery();
				deliveryRs.next();
				RetrievalDeliveryInfo deliveryInfo = new RetrievalDeliveryInfo(deliveryRs.getString(1), deliveryRs.getString(2));

				ItemInfo itemInfo = new ItemInfo(itemRs.getString(1), itemRs.getString(2), itemRs.getDouble(3), getItemStateByDescription(itemRs.getString(4)), retrievalInfo, deliveryInfo, importInfo, exportInfo);
				return itemInfo;
			} else {
				return null;
			}
		} catch (SQLException e) {
			feedbackThrowable(e);
		}
		return null;
	}
	
	private static final String getShipInfoSQL = "SELECT item_name, company FROM ship WHERE ship_name = ?";
	//SUSTC Department Manager User
	@Override
	public ShipInfo getShipInfo(LogInfo logInfo, String s) {
		try {
			if (!checkUser(logInfo)) {
				return null;
			}
			Connection userConnection = getUserConnection(logInfo.type());
			PreparedStatement shipStatement = userConnection.prepareStatement(getShipInfoSQL);
			shipStatement.setString(1, s);
			ResultSet shipRs = shipStatement.executeQuery();
			boolean isSailing = false;
			String company = null;
			while (shipRs.next()) {
				if (company == null) {
					company = shipRs.getString(2);
				}
				PreparedStatement itemStatement = userConnection.prepareStatement(getItemSQL);
				itemStatement.setString(1, shipRs.getString(1));
				ResultSet itemRs = itemStatement.executeQuery();
				itemRs.next();
				isSailing |= getItemStateByDescription(itemRs.getString(4)) == ItemState.Shipping;
				if (isSailing) {
					break;
				}
			}
			if (company == null) {
				return null;
			}
			ShipInfo shipInfo = new ShipInfo(s, company, isSailing);
			return shipInfo;
		} catch (SQLException e) {
			feedbackThrowable(e);
		}
		return null;
	}
	
	private static final Map<String, Type> stringContainerTypeMap = new HashMap<>();
	private static final Map<Type, String> containerTypeStringMap = new HashMap<>();
	static {
		stringContainerTypeMap.put("Dry Container", Type.Dry);
		stringContainerTypeMap.put("Flat Rack Container", Type.FlatRack);
		stringContainerTypeMap.put("Open Top Container", Type.OpenTop);
		stringContainerTypeMap.put("ISO Tank Container", Type.ISOTank);
		stringContainerTypeMap.put("Reefer Container", Type.Reefer);
		
		for (Map.Entry<String, Type> entry : stringContainerTypeMap.entrySet()) {
			containerTypeStringMap.put(entry.getValue(), entry.getKey());
		}
	}
	
	private static Type getContainerTypeByDescription(String description) {
		return stringContainerTypeMap.get(description);
	}
	
	private static final String getContainerInfoSQL = "SELECT item_name, type FROM container WHERE code = ?;";
	//SUSTC Department Manager User
	@Override
	public ContainerInfo getContainerInfo(LogInfo logInfo, String s) {
		try {
			if (!checkUser(logInfo)) {
				return null;
			}
			Connection userConnection = getUserConnection(logInfo.type());
			PreparedStatement containerStatement = userConnection.prepareStatement(getContainerInfoSQL);
			containerStatement.setString(1, s);
			ResultSet containerRs = containerStatement.executeQuery();
			boolean isUsing = false;
			String type = null;
			while (containerRs.next()) {
				if (type == null) {
					type = containerRs.getString(2);
				}
				PreparedStatement itemStatement = userConnection.prepareStatement(getItemSQL);
				itemStatement.setString(1, containerRs.getString(1));
				ResultSet itemRs = itemStatement.executeQuery();
				itemRs.next();
				ItemState state = getItemStateByDescription(itemRs.getString(4));
				isUsing |= (state == ItemState.WaitingForShipping) || (state == ItemState.Shipping) || (state == ItemState.UnpackingFromContainer) || (state == ItemState.PackingToContainer);
				if (isUsing) {
					break;
				}
			}
			if (type == null) {
				return null;
			}
			ContainerInfo containerInfo = new ContainerInfo(getContainerTypeByDescription(type), s, isUsing);
			return containerInfo;
		} catch (SQLException e) {
			feedbackThrowable(e);
		}
		return null;
	}
	
	private static final Map<String, StaffType> stringStaffTypeMap = new HashMap<>();
	private static final Map<StaffType, String> staffTypeStringMap = new HashMap<>();
	
	static {
		stringStaffTypeMap.put("Courier", StaffType.Courier);
		stringStaffTypeMap.put("SUSTC Department Manager", StaffType.SustcManager);
		stringStaffTypeMap.put("Company Manager", StaffType.CompanyManager);
		stringStaffTypeMap.put("Seaport Officer", StaffType.SeaportOfficer);
		
		for (Map.Entry<String, StaffType> entry : stringStaffTypeMap.entrySet()) {
			staffTypeStringMap.put(entry.getValue(), entry.getKey());
		}
		
	}
	
	private static StaffType getStaffTypeByDescription(String description) {
		return stringStaffTypeMap.get(description);
	}
	
	private static final String getStaffInfoSQL = "SELECT * FROM staff WHERE name = ?";
	//SUSTC Department Manager User
	@Override
	public StaffInfo getStaffInfo(LogInfo logInfo, String s) {
		try {
			if (!checkUser(logInfo)) {
				return null;
			}
			Connection userConnection = getUserConnection(logInfo.type());
			PreparedStatement statement = userConnection.prepareStatement(getStaffInfoSQL);
			statement.setString(1, s);
			ResultSet rs = statement.executeQuery();
			if (!rs.next()) {
				return null;
			}
			int birthYear = rs.getInt(7);
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);
			return new StaffInfo(new LogInfo(s, getStaffTypeByDescription(rs.getString(3)), rs.getString(2)), rs.getString(8), rs.getString(4), rs.getBoolean(5), currentYear - birthYear, rs.getString(6));
		} catch (SQLException e) {
			feedbackThrowable(e);
		}
		return null;
	}
	
	protected StaffInfo getStaffInfoByRoot(String user) {
		try {
			Connection userConnection = this.rootConn;
			PreparedStatement statement = userConnection.prepareStatement(getStaffInfoSQL);
			statement.setString(1, user);
			ResultSet rs = statement.executeQuery();
			if (!rs.next()) {
				return null;
			}
			int birthYear = rs.getInt(7);
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);
			return new StaffInfo(new LogInfo(user, getStaffTypeByDescription(rs.getString(3)), rs.getString(2)), rs.getString(8), rs.getString(4), rs.getBoolean(5), currentYear - birthYear, rs.getString(6));
		} catch (SQLException e) {
			feedbackThrowable(e);
		}
		return null;
	}

}
