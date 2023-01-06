import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PostgreSQLConnector {
    private String host;
    private int port;
    private String user;
    private String password;
    private String database;
    private Connection conn;
    private static boolean driverFound;

    static {
        try {
            Class.forName("org.postgresql.Driver");
            driverFound = true;
        } catch (Exception e) {
            driverFound = false;
            System.err.println("Cannot found the driver of PostgreSQL");
        }
    }
    public PostgreSQLConnector(String host, int port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public boolean connect() throws SQLException {
        if (!driverFound) {
            return false;
        }
        String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.database;
        conn = DriverManager.getConnection(url, this.user, this.password);
        return conn != null;
    }

    public boolean isConnected() {
        return conn != null;
    }

    public PreparedStatement prepareStatement(String s) throws SQLException {
        return conn.prepareStatement(s);
    }
}
