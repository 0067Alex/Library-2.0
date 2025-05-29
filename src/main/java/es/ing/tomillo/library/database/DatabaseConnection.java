package es.ing.tomillo.library.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:h2:tcp://localhost:9092/C:/Users/1SMR/Library/database/librarydb";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    private static Connection connection;
    private static boolean initialized = false;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            if (!initialized) {
                DatabaseManager.initializeDatabase();
                initialized = true;
            }
        }
        return connection;
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public static void executeInTransaction(DatabaseOperation operation) throws SQLException {
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);
            operation.execute(conn);
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    @FunctionalInterface
    public interface DatabaseOperation {
        void execute(Connection conn) throws SQLException;
    }
} 