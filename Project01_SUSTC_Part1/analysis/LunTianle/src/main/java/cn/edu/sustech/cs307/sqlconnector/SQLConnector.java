package cn.edu.sustech.cs307.sqlconnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class SQLConnector {
	
	public abstract void setAutoCommit(boolean autoCommit) throws SQLException;
	public abstract void commit() throws SQLException;
	public abstract boolean connect() throws SQLException;
	public abstract boolean isConnected();
	public abstract PreparedStatement prepareStatement(String s) throws SQLException;
	public abstract void close() throws SQLException;
	public abstract Connection getConnection();
	
	
}
