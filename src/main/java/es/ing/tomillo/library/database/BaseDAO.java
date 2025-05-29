package es.ing.tomillo.library.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDAO {
    
    @FunctionalInterface
    protected interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
    
    protected static <T> List<T> executeQuery(String sql, ResultSetMapper<T> mapper, Object... params) {
        List<T> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapper.map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing query: " + sql, e);
        }
        return results;
    }

    protected static <T> T executeQuerySingle(String sql, ResultSetMapper<T> mapper, Object... params) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapper.map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing query: " + sql, e);
        }
        return null;
    }

    protected static <T> T executeReturningKey(String sql, java.util.function.IntFunction<T> resultHandler, Object... params) {
        try (var conn = DatabaseConnection.getConnection();
             var stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            stmt.executeUpdate();
            try (var rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return resultHandler.apply(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Insert failed: " + sql, e);
        }
        return null;
    }

    protected static void executeUpdate(String sql, Object... params) {
        try {
            DatabaseConnection.executeInTransaction(conn -> {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    for (int i = 0; i < params.length; i++) {
                        stmt.setObject(i + 1, params[i]);
                    }
                    stmt.executeUpdate();
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException("Error executing update: " + sql, e);
        }
    }
} 