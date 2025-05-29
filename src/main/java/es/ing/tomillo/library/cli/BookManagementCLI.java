package es.ing.tomillo.library.cli;

import es.ing.tomillo.library.model.Book;
import es.ing.tomillo.library.service.library.Library;

import java.time.LocalDate;
import java.time.Year;
import java.util.Scanner;

public class BookManagementCLI {
    private final Library library;
    private final Scanner scanner;
    private final BookSearchesCLI bookSearchesCLI;

    public BookManagementCLI(Library library, Scanner scanner) {
        this.library = library;
        this.scanner = scanner;
        this.bookSearchesCLI = new BookSearchesCLI(library, scanner);
    }

    public void showBookMenu() {
        boolean backToMain = false;

        while (!backToMain) {
            System.out.println("\n📚 Book Management Menu:");
            System.out.println("1. ➕ Add Book");
            System.out.println("2. Search Book");
            System.out.println("3. Borrow Book");
            System.out.println("4. Return Book");
            System.out.println("5. Reserve Book");
            System.out.println("0. 🔙 Back to Main Menu");
            System.out.print("👉 Option: ");

            int option = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (option) {
                case 1:
                    addBookCLI();
                    break;
                case 2:
                    bookSearchesCLI.showBookSearchesMenu();
                    break;
                case 3:
                    borrowBookCLI();
                    break;
                case 4:
                    returnBookCLI(); // ✅ New method
                    break;
                case 5:
                    reserveBookCLI();
                    break;
                case 0:
                    backToMain = true;
                    break;
                default:
                    System.out.println("⚠️ Invalid option. Try again.");
            }
        }
    }

    /// 1 Addbook
    private void addBookCLI() {
        System.out.print("Option 1 selected. Add a new book:\n");
        System.out.print("🔢 ISBN: ");
        String isbn = scanner.nextLine();
        if (!(isbn.matches("\\d{10}") || isbn.matches("\\d{13}"))) {
            System.out.println("❌ Wrong ISBN. Must be 10 or 13 numeric digits.");
            System.out.println("Returning to the book menu...");
            return;
        }

        // Title input
        System.out.print("📘 Title: ");
        String title = scanner.nextLine();

        // Author input
        System.out.print("✍️ Author: ");
        String author = scanner.nextLine();

        // Publisher input
        System.out.print("🏢 Publisher: ");
        String publisher = scanner.nextLine();

        // Year input and validation
        System.out.print("📅 Publication Year (e.g., 2021): ");
        String pubYearInput = scanner.nextLine();
        if (!pubYearInput.matches("\\d{4}")) {
            System.out.println("❌ Invalid year. Please enter a 4-digit year (yyyy).");
            System.out.println("Returning to the book menu...");
            return;
        }

        Year pubYear = Year.parse(pubYearInput);

        // Build and add the book
        Book newBook = new Book.BookBuilder()
                .isbn(isbn)
                .title(title)
                .author(author)
                .publisher(publisher)
                .publicationYear(pubYear)
                .addedToLibraryDate(LocalDate.now())
                .build();

        Book addedBook = library.addBook(newBook);
        if (addedBook != null) {
            System.out.println("✅ " + addedBook.getTitle() + " by " + addedBook.getAuthor() + " added successfully:");
            System.out.println("Assigned Book ID = " + addedBook.getBookID());
        } else {
            System.out.println("❌ Failed to add book.");
        }
    }

    /// 3
    private void borrowBookCLI() {
        System.out.println("Option 3 selected. Borrow a book:");

        System.out.print("📘 Book ID: ");
        int bookID = scanner.nextInt();
        scanner.nextLine(); // consume newline

        System.out.print("👤 User ID: ");
        int userID = scanner.nextInt();
        scanner.nextLine(); // consume newline

        boolean success = library.borrowBook(userID, bookID); // ✅ correct argument order

        if (success) {
            System.out.println("✅ Book ID " + bookID + " successfully borrowed by User ID " + userID + ".");
        } else {
            System.out.println("❌ Borrowing failed. Either book not available or user not found.");
        }
    }

    private void returnBookCLI() {
        System.out.println("Option 4 selected. Return a book:");

        System.out.print("📘 Enter Book ID: ");
        int bookID = scanner.nextInt();
        scanner.nextLine();

        System.out.print("🧑 Enter User ID: ");
        int userID = scanner.nextInt();
        scanner.nextLine();

        boolean success = library.returnBook(userID, bookID);

        if (success) {
            System.out.println("✅ Book ID " + bookID + " successfully returned by User ID " + userID + ".");
        } else {
            System.out.println("❌ Returning failed. Either book not borrowed, not available, or user not found.");
        }
    }

    /// 5 reserve books
    private void reserveBookCLI() {
        System.out.println("Option 5 selected. Reserve a book:");
        System.out.print("Enter Book ID: ");
        int bookID = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter User ID: ");
        int userID = scanner.nextInt();
        scanner.nextLine();
        boolean success = library.reserveBook(userID, bookID);
        if (success) {
            System.out.println(" Book ID " + bookID + " successfully reserved by User ID " + userID + ".");
        } else {
            System.out.println("Reserve failed. Either book not available or user not found.");
        }
    }

}
