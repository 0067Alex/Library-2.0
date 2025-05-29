package es.ing.tomillo.library.database;

import es.ing.tomillo.library.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static es.ing.tomillo.library.database.DatabaseConnection.getConnection;

public class ReservationDAO extends BaseDAO {
    private static final String INSERT_RESERVATION =
            "INSERT INTO reservations (user_id, book_id, reservation_date) VALUES (?, ?, CURRENT_TIMESTAMP)";

    private static final String CANCEL_RESERVATION =
            "DELETE FROM reservations WHERE user_id = ? AND book_id = ?";

    public static void insertReservation(int userId, int bookId) {
        try {
            executeUpdate(INSERT_RESERVATION, userId, bookId);
        } catch (Exception e) {
            throw new RuntimeException("❌ Error inserting reservation: " + e.getMessage(), e);
        }
    }

    public static void cancelReservation(int userId, int bookId) {
        try {
            executeUpdate(CANCEL_RESERVATION, userId, bookId);
        } catch (Exception e) {
            throw new RuntimeException("❌ Error cancelling reservation: " + e.getMessage(), e);
        }
    }

    // 🔒 Atomic reservation (for future-safe logic)
    public static boolean reserveBookAtomic(int userId, int bookId) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // Step 1: Insert reservation
            try (PreparedStatement insertStmt = conn.prepareStatement(INSERT_RESERVATION)) {
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, bookId);
                insertStmt.executeUpdate();
            }

            // Step 2: (optional) add logic here e.g. update book table or log action

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Reservation transaction failed: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("⚠️ Rollback failed: " + rollbackEx.getMessage());
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) {
                System.err.println("⚠️ Connection cleanup failed: " + ex.getMessage());
            }
        }
    }
    public static User getReserverOfBook(int bookID) {
        final String query = "SELECT u.* FROM users u " +
                "JOIN reservations r ON u.user_id = r.user_id " +
                "WHERE r.book_id = ?";

        return executeQuerySingle(query, UserDAO::createUserFromResultSet, bookID);
    }

    public static int countBooksReservedByUser(int userId) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to count reserved books: " + e.getMessage());
        }
        return 0;
    }
}

