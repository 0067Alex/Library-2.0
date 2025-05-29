package es.ing.tomillo.library.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:h2:tcp://localhost:9092/C:/Users/1SMR/Library/database/librarydb";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void initializeDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            createTables(conn);
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // USERS
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS users (
                            user_id INT AUTO_INCREMENT PRIMARY KEY,
                            user_dni VARCHAR(10) UNIQUE NOT NULL,
                            user_name VARCHAR(100) NOT NULL,
                            user_surname VARCHAR(100) NOT NULL,
                            user_email VARCHAR(100),
                            user_phone_number VARCHAR(15))
                    """);

            // BOOKS
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS books (
                            book_id INT AUTO_INCREMENT PRIMARY KEY,
                            isbn VARCHAR(20) NOT NULL,
                            title VARCHAR(100) NOT NULL,
                            author VARCHAR(100) NOT NULL,
                            publisher VARCHAR(100),
                            publication_year INT,
                            added_to_library_date DATE)
                    """);

            // LOANS
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS loans (
                            loan_id INT AUTO_INCREMENT PRIMARY KEY,
                            user_id INT NOT NULL,
                            book_id INT NOT NULL,
                            loan_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            return_date TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES users(user_id),
                            FOREIGN KEY (book_id) REFERENCES books(book_id))
                    """);

            // Drop and create VIEW for borrowed_books
            stmt.execute("DROP VIEW IF EXISTS borrowed_books");
            stmt.execute("""
                        CREATE VIEW borrowed_books AS
                        SELECT
                            l.user_id,
                            u.user_name,
                            u.user_surname,
                            u.user_dni AS user_dni,
                            b.isbn AS book_isbn,
                            b.book_id AS book_id,
                            b.title AS book_title,
                            b.author AS book_author,
                            l.loan_date AS borrowed_date
                        FROM loans l
                        JOIN users u ON l.user_id = u.user_id
                        JOIN books b ON l.book_id = b.book_id
                        WHERE l.return_date IS NULL
                    """);

            // RESERVATIONS
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS reservations (
                            reservation_id INT AUTO_INCREMENT PRIMARY KEY,
                            user_id INT NOT NULL,
                            book_id INT NOT NULL,
                            reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES users(user_id),
                            FOREIGN KEY (book_id) REFERENCES books(book_id)
                        )
                    """);

            // Drop and create VIEW for reserved_books
            stmt.execute("DROP VIEW IF EXISTS reserved_books");
            stmt.execute("""
                        CREATE VIEW reserved_books AS
                        SELECT
                            r.user_id,
                            u.user_name,
                            u.user_surname,
                            u.user_dni AS user_dni,
                            b.isbn AS book_isbn,
                            b.book_id AS book_id,
                            b.title AS book_title,
                            b.author AS book_author,
                            r.reservation_date
                        FROM reservations r
                        JOIN users u ON r.user_id = u.user_id
                        JOIN books b ON r.book_id = b.book_id
                    """);
        }
    }

}