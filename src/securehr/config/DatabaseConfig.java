package securehr.config;

public final class DatabaseConfig {
    public static final String SERVER = "localhost";
    public static final int PORT = 1433;

    // Demo credentials. Change these values for your SQL Server environment.
    public static final String DB_USER = "sa";
    public static final String DB_PASSWORD = "toideptrai";

    public static final String DB1_NAME = "SecureHR_DB1";
    public static final String DB2_NAME = "SecureHR_DB2";

    private DatabaseConfig() {
    }

    public static String dbUrl(String dbName) {
        return "jdbc:sqlserver://" + SERVER + ":" + PORT
                + ";databaseName=" + dbName
                + ";encrypt=false;trustServerCertificate=true;loginTimeout=30;";
    }
}
