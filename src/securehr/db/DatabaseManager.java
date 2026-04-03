package securehr.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import securehr.config.DatabaseConfig;

public final class DatabaseManager {
    private DatabaseManager() {
    }

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQL Server JDBC Driver not found in lib folder.", e);
        }
    }

    public static Connection getDb1Connection() throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.dbUrl(DatabaseConfig.DB1_NAME),
                DatabaseConfig.DB_USER,
                DatabaseConfig.DB_PASSWORD);
    }

    public static Connection getDb2Connection() throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.dbUrl(DatabaseConfig.DB2_NAME),
                DatabaseConfig.DB_USER,
                DatabaseConfig.DB_PASSWORD);
    }
}
