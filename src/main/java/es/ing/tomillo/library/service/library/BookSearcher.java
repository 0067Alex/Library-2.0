package es.ing.tomillo.library.service.library;
import es.ing.tomillo.library.database.BookDAO;
import es.ing.tomillo.library.database.LoanDAO;
import es.ing.tomillo.library.database.ReservationDAO;
import es.ing.tomillo.library.model.Book;
import es.ing.tomillo.library.model.User;

import java.util.List;
import java.util.Map;
/// outdated///
public class BookSearcher {
    private final Map<Integer, Book> books;
    private final Map<Integer, User> users;

    public BookSearcher(Map<Integer, Book> books, Map<Integer, User> users) {
        this.books = books;
        this.users = users;
    }

    /// Método busqueda por ID
    public Book searchBookByID(int bookID) {
        try {
            return BookDAO.searchBooksByID(bookID);
        } catch (Exception e) {
            System.out.println("❌ Failed to search book by ID: " + e.getMessage());
            return null;
        }
    }

    ///  Método search por ISBN
    public Book searchBookByIsbn(String isbn) {
        try {
            return BookDAO.searchBooksByIsbn(isbn);
        } catch (Exception e) {
            System.out.println("❌ Failed to search book by ISBN: " + e.getMessage());
            return null;
        }
    }

    /// Método search por Título
    public List<Book> searchBooksByTitle(String title) {
        try {
            return BookDAO.searchBooksByTitle(title);
        } catch (Exception e) {
            System.out.println("❌ Failed to search books by title: " + e.getMessage());
            return List.of();
        }
    }

    ///  Método search por Autor
    public List<Book> searchBooksByAuthor(String author) {
        try {
            return BookDAO.searchBooksByAuthor(author);
        } catch (Exception e) {
            System.out.println("❌ Failed to search books by author: " + e.getMessage());
            return List.of();
        }
    }

    /// Search publisher
    public List<Book> searchbooksByPublisher(String publisher) {
        try {
            return BookDAO.searchBooksByPublisher(publisher);
        } catch (Exception e) {
            System.out.println("❌ Failed to search books by publisher " + e.getMessage());
            return List.of();
        }
    }

    ///  Método Search Libros disponibles
    public List<Book> searchAvailableBooks() {
        try {
            return BookDAO.searchAvailableBooks();
        } catch (Exception e) {
            System.out.println("❌ Failed to fetch available books: " + e.getMessage());
            return List.of();
        }
    }

    ///  Todos los libros
    public List<Book> listAvailableBooks() {
        try {
            List<Book> books = BookDAO.searchAvailableBooks();
            if (books.isEmpty()) {
                System.out.println("ℹ️ No available books at the moment.");
            } else {
                System.out.println("📚 Available books:");
                for (Book book : books) {
                    System.out.println(" - " + book.getTitle() + " by " + book.getAuthor());
                }
            }
            return books;
        } catch (Exception e) {
            System.err.println("❌ Error fetching available books: " + e.getMessage());
            return List.of();
        }
    }
}
