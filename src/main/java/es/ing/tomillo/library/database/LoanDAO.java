package es.ing.tomillo.library.database;

import es.ing.tomillo.library.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static es.ing.tomillo.library.database.DatabaseConnection.getConnection;

public class LoanDAO extends BaseDAO {
    private static final String INSERT_LOAN =
            "INSERT INTO loans (user_id, book_id, loan_date) VALUES (?, ?, CURRENT_TIMESTAMP)";

    private static final String RETURN_LOAN =
            "UPDATE loans SET return_date = CURRENT_TIMESTAMP, returned = TRUE WHERE user_id = ? AND book_id = ? AND return_date IS NULL";

    private static final String DELETE_RESERVATION =
            "DELETE FROM reservations WHERE user_id = ? AND book_id = ?";

    // 📌 Insert loan (non-atomic)
    public static void insertLoan(int userId, int bookId) {
        try {
            executeUpdate(INSERT_LOAN, userId, bookId);
            executeUpdate(DELETE_RESERVATION, userId, bookId);
        } catch (Exception e) {
            throw new RuntimeException("❌ Error inserting loan: " + e.getMessage(), e);
        }
    }

    public static boolean isBookBorrowedByUser(int userID, int bookID) {
        String sql = "SELECT 1 FROM LOANS WHERE USER_ID = ? AND BOOK_ID = ? AND RETURN_DATE IS NULL";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userID);
            stmt.setInt(2, bookID);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // true if there's at least one matching loan
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 🔒 Atomic loan + reservation deletion (used when borrowing a reserved book)
    public static boolean borrowReservedBookAtomic(int userId, int bookId) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Step 1: Delete reservation
            try (PreparedStatement deleteStmt = conn.prepareStatement(DELETE_RESERVATION)) {
                deleteStmt.setInt(1, userId);
                deleteStmt.setInt(2, bookId);
                deleteStmt.executeUpdate();
            }

            // Step 2: Insert loan
            try (PreparedStatement loanStmt = conn.prepareStatement(INSERT_LOAN)) {
                loanStmt.setInt(1, userId);
                loanStmt.setInt(2, bookId);
                loanStmt.executeUpdate();
            }

            conn.commit(); // Everything succeeded
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Transaction failed: " + e.getMessage());
            try {
                if (conn != null) conn.rollback(); // Undo all if anything fails
            } catch (SQLException rollbackEx) {
                System.err.println("⚠️ Rollback failed: " + rollbackEx.getMessage());
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Restore default behavior
                    conn.close();
                }
            } catch (SQLException ex) {
                System.err.println("⚠️ Connection cleanup failed: " + ex.getMessage());
            }
        }
    }

    // 🔒 Atomic return operation (used in returnBook logic)
    public static boolean returnBookAtomic(int userId, int bookId) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Step: Update the return date
            try (PreparedStatement returnStmt = conn.prepareStatement(RETURN_LOAN)) {
                returnStmt.setInt(1, userId);
                returnStmt.setInt(2, bookId);
                int rowsUpdated = returnStmt.executeUpdate();

                if (rowsUpdated == 0) {
                    conn.rollback(); // No matching row to update
                    return false;
                }
            }

            conn.commit(); // Success
            return true;

        } catch (SQLException e) {
            System.err.println("❌ returnBookAtomic failed: " + e.getMessage());
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
    public static User getBorrowerOfBook(int bookID) {
        final String query = "SELECT u.* FROM users u " +
                "JOIN loans l ON u.user_id = l.user_id " +
                "WHERE l.book_id = ? AND l.returned = FALSE";

        return executeQuerySingle(query, UserDAO::createUserFromResultSet, bookID);
    }

    public int countBooksBorrowedByUser(int userId) {
        int count = 0;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM BORROWED_BOOKS WHERE USER_ID = ?"
             )) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

}

