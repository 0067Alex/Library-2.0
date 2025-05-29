package es.ing.tomillo.library.service.library;

import java.util.List;
import java.util.Map;

import es.ing.tomillo.library.database.BookDAO;
import es.ing.tomillo.library.database.LoanDAO;
import es.ing.tomillo.library.database.ReservationDAO;
import es.ing.tomillo.library.database.UserDAO;
import es.ing.tomillo.library.model.Book;
import es.ing.tomillo.library.model.User;

public class BookManager {
    private final Map<Integer, Book> books;
    private final Map<Integer, User> users;

    public BookManager(Map<Integer, Book> books, Map<Integer, User> users) {
        this.books = books;
        this.users = users;
    }

    /// refreshdatabase
    private boolean refreshUserAndBookDatabase(int userID, int bookID) {
        Book book = BookDAO.searchBooksByID(bookID);
        User user = UserDAO.searchUserByID(userID);

        if (book == null || user == null) return false;

        books.put(bookID, book);
        users.put(userID, user);
        return true;
    }

    /// Método de interacción user and book
    private boolean validateUserAndBookExist(int userID, int bookID) {
        boolean userExists = users.containsKey(userID);
        boolean bookExists = books.containsKey(bookID);

        if (!userExists) {
            System.out.println("❌ User with ID " + userID + " does not exist.");
        }
        if (!bookExists) {
            System.out.println("❌ Book with ID " + bookID + " does not exist.");
        }

        return userExists && bookExists;
    }

    /// 1.book management
    /// 1.1 Añadir book
    public Book addBook(Book book) {
        try {
            Book createdBook = BookDAO.insertBook(book);

            if (createdBook == null) {
                System.err.println("❌ Book insertion returned null.");
                return null;
            }

            books.put(createdBook.getBookID(), createdBook);

            System.out.println("✅ Book '" + createdBook.getTitle() + "' added with ID " + createdBook.getBookID());
            return createdBook;

        } catch (RuntimeException e) {
            System.err.println("❌ Error saving book to database: " + e.getMessage());
            return null;
        }
    }

    /// 1.2. Préstamo de libros
    public boolean borrowBook(int userID, int bookID) {
        if (!validateUserAndBookExist(userID, bookID)) return false;

        if (!refreshUserAndBookDatabase(userID, bookID)) {
            System.out.println("❌ Failed to refresh user or book from database.");
            return false;
        }

        User user = users.get(userID);
        Book book = books.get(bookID);

        // 🔁 Sync borrowed and reserved state
        book.setBorrowedBy(LoanDAO.getBorrowerOfBook(bookID));
        book.setReservedBy(ReservationDAO.getReserverOfBook(bookID));

        // 🚫 Check if user already borrowed this book (DB-safe)
        if (LoanDAO.isBookBorrowedByUser(userID, bookID)) {
            System.out.println("⚠️ User has already borrowed this book.");
            return false;
        }

        // 🚫 Check if someone else has borrowed it
        if (book.isBorrowed()) {
            System.out.println("❌ Book is currently borrowed by another user.");
            return false;
        }

        int currentBorrowedCount = new LoanDAO().countBooksBorrowedByUser(userID);
        if (currentBorrowedCount >= user.getMaxBorrowedBooks()) {
            System.out.println("⚠️ User has reached the maximum number of borrowed books (" + currentBorrowedCount + "/" + user.getMaxBorrowedBooks() + ").");
            return false;
        }

        boolean dbSuccess;

        if (book.getReservedBy().isPresent() && book.getReservedBy().get().getUserID() == userID) {
            dbSuccess = LoanDAO.borrowReservedBookAtomic(userID, bookID);
            if (!dbSuccess) {
                System.out.println("❌ Failed to borrow reserved book.");
                return false;
            }
        } else {
            try {
                LoanDAO.insertLoan(userID, bookID);
                dbSuccess = true;
            } catch (Exception e) {
                System.out.println("❌ Failed to insert loan: " + e.getMessage());
                return false;
            }
        }

        // ✅ Update in-memory state
        book.setBorrowedBy(user);
        user.getBorrowedBooksList().add(book);

        System.out.println("✅ Book '" + book.getTitle() + "' successfully borrowed by " + user.getUserName());
        return true;
    }


    /// 1.3. Devolución de libros
    public boolean returnBook(int userID, int bookID) {
        if (!validateUserAndBookExist(userID, bookID)) return false;

        if (!refreshUserAndBookDatabase(userID, bookID)) {
            System.out.println("❌ Failed to refresh user or book from database.");
            return false;
        }
        User user = users.get(userID);
        Book book = books.get(bookID);


        if (!LoanDAO.isBookBorrowedByUser(userID, bookID)) {
            System.out.println("❌ This user did not borrow this book so it cannot be returned.");
            return false;
        }

        boolean dbSuccess = LoanDAO.returnBookAtomic(userID, bookID);
        if (!dbSuccess) {
            System.out.println("❌ Failed to return book in database.");
            return false;
        }

        /// ✅ Sync in-memory state
        user.getBorrowedBooksList().remove(book);
        book.setBorrowedBy(null);

        System.out.println("✅ Book '" + book.getTitle() + "' successfully returned by " + user.getUserName());
        return true;
    }


    /// 1.4. Reserva de libros
    public boolean reserveBook(int userID, int bookID) {
        if (!validateUserAndBookExist(userID, bookID)) return false;

        if (!refreshUserAndBookDatabase(userID, bookID)) {
            System.out.println("❌ Failed to refresh user or book from database.");
            return false;
        }

        User user = users.get(userID);
        Book book = books.get(bookID);

        book.setBorrowedBy(LoanDAO.getBorrowerOfBook(bookID));
        book.setReservedBy(ReservationDAO.getReserverOfBook(bookID));

        if (user.getReservedBooksList().contains(book)) {
            System.out.println("⚠️ User has already reserved this book.");
            return false;
        }

        if (LoanDAO.isBookBorrowedByUser(userID, bookID)) {
            System.out.println("❌ User already has this book borrowed and cannot reserve it.");
            return false;
        }

        int reservedCount = ReservationDAO.countBooksReservedByUser(userID);
        if (reservedCount >= user.getMaxReservedBooks()) {
            System.out.println("⚠️ User has reached the maximum number of reserved books (DB checked).");
            return false;
        }

        if (!book.isBorrowed()) {
            System.out.println("❌ Book is currently available. Cannot reserve.");
            return false;
        }

        if (book.isReserved()) {
            System.out.println("❌ Book is already reserved by another user.");
            return false;
        }

        boolean reservedInDB = ReservationDAO.reserveBookAtomic(userID, bookID);
        if (!reservedInDB) {
            System.out.println("❌ Failed to reserve book in database.");
            return false;
        }

        book.setReservedBy(user);
        user.getReservedBooksList().add(book);

        System.out.println("✅ Book '" + book.getTitle() + "' successfully reserved by " + user.getUserName());
        return true;
    }
}
