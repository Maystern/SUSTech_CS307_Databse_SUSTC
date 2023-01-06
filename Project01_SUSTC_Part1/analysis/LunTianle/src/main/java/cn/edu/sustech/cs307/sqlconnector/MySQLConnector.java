package cn.edu.sustech.cs307.sqlconnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class MySQLConnector extends SQLConnector {
	private String host;
	private int port;
	private String user;
	private String password;
	private String database;
	private Connection conn;
	private static boolean driverFound;
	
	public MySQLConnector(String host, int port, String database, String user, String password) {
		this.host = host;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;
	}
	
	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			driverFound = true;
		} catch (Exception e) {
			driverFound = false;
			System.err.println("Cannot found the driver of PostgreSQL");
		}
	}
	
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		this.conn.setAutoCommit(autoCommit);
	}
	
	public void commit() throws SQLException {
		this.conn.commit();
	}
	
	public boolean connect() throws SQLException {
		if (!driverFound) {
			return false;
		}
		String url = "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database;
		Properties properties = new Properties();
		properties.setProperty("user", this.user);
		properties.setProperty("password", this.password);
		properties.setProperty("useSSL", "false");
		properties.setProperty("autoReconnect", "true");
		properties.setProperty("serverTimezone", "UTC");
		properties.setProperty("allowPublicKeyRetrieval", "true");
		properties.setProperty("rewriteBatchedStatements", "true");
		conn = DriverManager.getConnection(url, properties);
		return conn != null;
	}
	
	public boolean isConnected() {
		return conn != null;
	}
	
	public PreparedStatement prepareStatement(String s) throws SQLException {
		return conn.prepareStatement(s);
	}

	@Override
	public void close() throws SQLException {
		this.conn.close();
	}

	@Override
	public Connection getConnection() {
		return this.conn;
	}
}
