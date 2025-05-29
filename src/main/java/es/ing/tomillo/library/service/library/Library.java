package es.ing.tomillo.library.service.library;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import es.ing.tomillo.library.database.BookDAO;
import es.ing.tomillo.library.database.LoanDAO;
import es.ing.tomillo.library.database.ReservationDAO;
import es.ing.tomillo.library.database.UserDAO;
import es.ing.tomillo.library.model.Book;
import es.ing.tomillo.library.model.User;

public class Library {
    private Map<Integer, Book> books = new HashMap<>();
    private Map<Integer, User> users = new HashMap<>();
    private BookManager bookManager;
    private BookSearcher bookSearcher;
    private UserManager userManager;

    public Library() {
        // do NOT load books/users here anymore
    }

    public void initializeData() {
        try {
            this.books = BookDAO.searchAllBooks().stream()
                    .collect(Collectors.toMap(Book::getBookID, b -> b));

            this.users = UserDAO.searchAllUsers().stream()
                    .collect(Collectors.toMap(User::getUserID, u -> u));

            // Create managers now that maps are ready
            this.bookManager = new BookManager(books, users);
            this.bookSearcher = new BookSearcher(books, users);
            this.userManager = new UserManager(users);

            System.out.println("📘 Library data loaded.");
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load library data: " + e.getMessage(), e);
        }
    }

    /// BookManager
    public Book addBook(Book book) {
        return bookManager.addBook(book);
    }

    public boolean borrowBook(int userID, int bookID) {
        return bookManager.borrowBook(userID, bookID);
    }

    public boolean reserveBook(int userID, int bookID) {
        return bookManager.reserveBook(userID, bookID);
    }

    public boolean returnBook(int userID, int bookID) {
        return bookManager.returnBook(userID, bookID);
    }

    /// BookSearcher
    public Book searchBookByID(int bookID) {
        return BookDAO.searchBooksByID(bookID);
    }

    public Book searchBookByIsbn(String isbn) {
        return BookDAO.searchBooksByIsbn(isbn);
    }

    public List<Book> searchBooksByTitle(String title) {
        return BookDAO.searchBooksByTitle(title);
    }

    public List<Book> searchBooksByAuthor(String author) {
        return BookDAO.searchBooksByAuthor(author);
    }

    public List<Book> searchBooksByPublisher(String publisher) {
        return BookDAO.searchBooksByPublisher(publisher);
    }

    public List<Book> searchAvailableBooks() {
        return BookDAO.searchAvailableBooks();
    }

    public List<Book> searchAllBooks() {
        return BookDAO.searchAllBooks();
    }

    /// 2. user management
    /// 2.1 Añadir usuario
    public User addUser(User user) {
        return userManager.addUser(user);
    }

    /// 7. User Searches
    public User searchUserByID(int userID) {
        return UserDAO.searchUserByID(userID);
    }
   public User searchUserByDNI(String dni) {
        return UserDAO.searchUserByDni(dni);
   }
    public List<User> searchUsersByName(String name) {
        return UserDAO.searchUsersByName(name);
    }
    public List<User> searchAllUsers() {
        return UserDAO.searchAllUsers();
    }
}


/// get user by id
// Debe mostrar por pantalla todos los usuarios registrados en la biblioteca

