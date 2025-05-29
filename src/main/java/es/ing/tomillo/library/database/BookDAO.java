package es.ing.tomillo.library.database;

import es.ing.tomillo.library.model.Book;
import es.ing.tomillo.library.model.User;
import es.ing.tomillo.library.model.enums.BookAvailability;
import es.ing.tomillo.library.model.enums.BookReservation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static es.ing.tomillo.library.database.DatabaseConnection.getConnection;

public class BookDAO extends BaseDAO {
    private static final String INSERT_BOOK =
            "INSERT INTO books (isbn, title, author, publisher, publication_year, added_to_library_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SEARCH_BY_BOOK_ID =
            "SELECT * FROM books WHERE book_id = ?";

    private static final String SEARCH_BY_ISBN =
            "SELECT * FROM books WHERE isbn = ?";

    private static final String SEARCH_BY_TITLE =
            "SELECT * FROM books WHERE title LIKE ?";

    private static final String SEARCH_BY_AUTHOR =
            "SELECT * FROM books WHERE author LIKE ?";

    private static final String SEARCH_BY_PUBLISHER =
            "SELECT * FROM books WHERE publisher LIKE ?";

    private static final String SEARCH_AVAILABLE =
            "SELECT * FROM books WHERE book_availability = 'AVAILABLE'";

    private static final String SEARCH_ALL_BOOKS =
            "SELECT * FROM books";

    private static final String UPDATE_AVAILABILITY =
            "UPDATE books SET book_availability = ? WHERE book_ID = ?";

    private static final String DELETE_BOOK =
            "DELETE FROM books WHERE book_ID = ?";

    public static Book insertBook(Book book) {
        try (var conn = getConnection();
             var stmt = conn.prepareStatement(INSERT_BOOK, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getPublisher());
            stmt.setObject(5, book.getPublicationYear() != null ? book.getPublicationYear().getValue() : null);
            stmt.setObject(6, book.getAddedToLibraryDate());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Inserting book failed, no rows affected.");
            }

            try (var generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    return searchBooksByID(generatedId);
                } else {
                    throw new SQLException("Inserting book failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Book searchBooksByID(int bookID) {
        return executeQuerySingle(SEARCH_BY_BOOK_ID, BookDAO::createBookFromResultSet, bookID);
    }

    public static Book searchBooksByIsbn(String isbn) {
        return executeQuerySingle(SEARCH_BY_ISBN, BookDAO::createBookFromResultSet, "%" + isbn + "%");
    }

    public static List<Book> searchBooksByTitle(String title) {
        return executeQuery(SEARCH_BY_TITLE, BookDAO::createBookFromResultSet, "%" + title + "%");
    }

    public static List<Book> searchBooksByAuthor(String author) {
        return executeQuery(SEARCH_BY_AUTHOR, BookDAO::createBookFromResultSet, "%" + author + "%");
    }

    public static List<Book> searchBooksByPublisher(String publisher) {
        return executeQuery(SEARCH_BY_PUBLISHER, BookDAO::createBookFromResultSet, "%" + publisher + "%");
    }

    public static List<Book> searchAllBooks() {
        return executeQuery(SEARCH_ALL_BOOKS, BookDAO::createBookFromResultSet);
    }

    public static List<Book> searchAvailableBooks() {
        String sql = """
        SELECT * FROM books
        WHERE book_id NOT IN (
            SELECT book_id FROM loans WHERE return_date IS NULL
        )
    """;

        return executeQuery(sql, BookDAO::createBookFromResultSet);
    }

    public static void deleteBook(int BookID) {
        executeUpdate(DELETE_BOOK, BookID);
    }

    protected static Book createBookFromResultSet(ResultSet rs) throws SQLException {
        int bookId = rs.getInt("book_id");
        String isbn = rs.getString("isbn");
        String title = rs.getString("title");
        String author = rs.getString("author");
        String publisher = rs.getString("publisher");
        Year publicationYear = Year.of(rs.getInt("publication_year"));

        LocalDate addedToLibraryDate = null;
        java.sql.Date sqlDate = rs.getDate("added_to_library_date");
        if (sqlDate != null) {
            addedToLibraryDate = sqlDate.toLocalDate();
        }

        return new Book.BookBuilder()
                .bookID(bookId)
                .isbn(isbn)
                .title(title)
                .author(author)
                .publisher(publisher)
                .publicationYear(publicationYear)
                .addedToLibraryDate(addedToLibraryDate)
                .build();
    }

}