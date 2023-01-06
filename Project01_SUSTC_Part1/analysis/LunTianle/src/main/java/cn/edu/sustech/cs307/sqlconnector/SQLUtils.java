package cn.edu.sustech.cs307.sqlconnector;

import cn.edu.sustech.cs307.Main;

public final class SQLUtils {
	
	private SQLUtils() {}
	
	public static PostgreSQLConnector newPostgreSQLConnector() {
		String host = Main.getProperty("postgresql-host");
		int port = Integer.parseInt(Main.getProperty("postgresql-port"));
		String user = Main.getProperty("postgresql-user");
		String password = Main.getProperty("postgresql-password");
		String database = Main.getProperty("postgresql-database");
		return new PostgreSQLConnector(host, port, database, user, password);
	}
	
	public static MySQLConnector newMySQLConnector() {
		String host = Main.getProperty("mysql-host");
		int port = Integer.parseInt(Main.getProperty("mysql-port"));
		String user = Main.getProperty("mysql-user");
		String password = Main.getProperty("mysql-password");
		String database = Main.getProperty("mysql-database");
		return new MySQLConnector(host, port, database, user, password);
	}
	
}
