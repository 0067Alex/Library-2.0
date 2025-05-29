package es.ing.tomillo.library.cli;

import es.ing.tomillo.library.model.Book;
import es.ing.tomillo.library.service.library.Library;

import java.util.List;
import java.util.Scanner;

public class BookSearchesCLI {
    private final Library library;
    private final Scanner scanner;

    public BookSearchesCLI(Library library, Scanner scanner) {
        this.library = library;
        this.scanner = scanner;
    }

    public void showBookSearchesMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n🔎 Book Search Menu:");
            System.out.println("1. Search by ID");
            System.out.println("2. Search by ISBN");
            System.out.println("3. Search by Title");
            System.out.println("4. Search by Author");
            System.out.println("5. Search by Publisher");
            System.out.println("6. List Available Books");
            System.out.println("7. List All Books");
            System.out.println("0. Back");
            System.out.print("👉 Option: ");

            int option = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (option) {
                case 1 -> searchByID();
                case 2 -> searchByISBN();
                case 3 -> searchByTitle();
                case 4 -> searchByAuthor();
                case 5 -> searchByPublisher();
                case 6 -> listAvailableBooks();
                case 7 -> searchAllBooks();
                case 0 -> back = true;
                default -> System.out.println("⚠️ Invalid option.");
            }
        }
    }

    private void searchByID() {
        System.out.print("Enter Book ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Book book = library.searchBookByID(id);
        System.out.println(book != null ? book : "❌ Book not found.");
    }

    private void searchByISBN() {
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();
        Book book = library.searchBookByIsbn(isbn);
        System.out.println(book != null ? book : "❌ Book not found.");
    }

    private void searchByTitle() {
        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        List<Book> books = library.searchBooksByTitle(title);
        books.forEach(System.out::println);
    }

    private void searchByAuthor() {
        System.out.print("Enter Author: ");
        String author = scanner.nextLine();
        List<Book> books = library.searchBooksByAuthor(author);
        books.forEach(System.out::println);
    }

    private void searchByPublisher() {
        System.out.print("Enter Publisher: ");
        String publisher = scanner.nextLine();
        List<Book> books = library.searchBooksByPublisher(publisher);
        books.forEach(System.out::println);
    }

    private void listAvailableBooks() {
        List<Book> books = library.searchAvailableBooks();
        if (books.isEmpty()) {
            System.out.println("ℹ️ No available books.");
        } else {
            books.forEach(System.out::println);
        }
    }

    private void searchAllBooks() {
        List<Book> books = library.searchAllBooks();
        if (books.isEmpty()) {
            System.out.println("ℹ️ No available books.");
        } else {
            books.forEach(System.out::println);
        }
    }
}
