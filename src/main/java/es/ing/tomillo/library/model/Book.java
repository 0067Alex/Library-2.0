package es.ing.tomillo.library.model;

import es.ing.tomillo.library.service.book.BookValidator;
import es.ing.tomillo.library.model.enums.BookAvailability;
import es.ing.tomillo.library.model.enums.BookReservation;

import java.time.LocalDate;
import java.time.Year;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class Book {
    /// Book fields
    private static final AtomicInteger nextBookID = new AtomicInteger(0);
    private final int bookID;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private Year publicationYear;
    private final LocalDate addedToLibraryDate; /// Importé LocalDate
    /// Availability tracking
    private BookAvailability bookAvailability;
    private User borrowedBy;
    private BookReservation bookReservation;
    private User reservedBy;

    /// Constructor
    private Book(BookBuilder builder) {
        this.bookID = builder.bookID != null ? builder.bookID : nextBookID.getAndIncrement();
        this.isbn = builder.isbn;
        this.title = builder.title;
        this.author = builder.author;
        this.publisher = builder.publisher;
        this.publicationYear = builder.publicationYear;

        this.addedToLibraryDate = builder.addedToLibraryDate != null
                ? builder.addedToLibraryDate
                : LocalDate.now();

        this.bookAvailability = builder.bookAvailability != null
                ? builder.bookAvailability
                : BookAvailability.AVAILABLE;

        this.bookReservation = builder.bookReservation != null
                ? builder.bookReservation
                : BookReservation.NOT_RESERVED;

        this.borrowedBy = builder.borrowedBy;
        this.reservedBy = builder.reservedBy;
    }
    /// Getters & setters
    /// bookID (solo get)
    public int getBookID() {
        return bookID;
    }

    ///  isbn
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /// title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /// author
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    /// publisher
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /// publicationYear
    public Year getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Year publicationYear) {
        this.publicationYear = publicationYear;
    }

    /// addedToLibraryDate
    public LocalDate getAddedToLibraryDate() {
        return addedToLibraryDate;
    }

    /// BookAvailability
    public BookAvailability getBookAvailability() {
        return bookAvailability;
    }

    void setBookAvailability(BookAvailability bookAvailability) {
        this.bookAvailability = bookAvailability;
    }

    /// borrowedBy (get and set)
    public Optional<User> getBorrowedBy() {
        return Optional.ofNullable(borrowedBy); ///es algo opcional, si el valor es nulo devuelve "optional"
    }

    public void setBorrowedBy(User borrowedBy) {
        this.borrowedBy = borrowedBy;
        BookStatusUpdater.updateBookStatus(this);
    }

    /// Reservation
    public BookReservation getBookReservation() {
        return bookReservation;
    }

    void setBookReservation(BookReservation bookReservation) {
        this.bookReservation = bookReservation;
    }

    /// reservedBy (get and set)
    public Optional<User> getReservedBy() {
        return Optional.ofNullable(reservedBy);
    }

    public void setReservedBy(User reservedBy) {
        this.reservedBy = reservedBy;
        BookStatusUpdater.updateBookStatus(this);
    }

    /// Convenience methods
    public boolean isBorrowed() {
        return borrowedBy != null;
    }

    public boolean isReserved() {
        return reservedBy != null;
    }
    
    /// toString
    @Override
    public String toString() {
        return "ID: " + bookID + '\'' +
                ", Title: '" + title + '\'' +
                ", Author: '" + author + '\'' +
                ", Publisher: '" + publisher + '\'' +
                ", ISBN: '" + isbn + '\'' +
                ", Year: " + publicationYear +
                ", Added to library on: " + addedToLibraryDate;
    }


    /// Equals
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Book book = (Book) obj;
        return isbn.equals(book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    /// BookBuilder Class
    public static class BookBuilder {
        private Integer bookID;
        private String isbn;
        private String title;
        private String author;
        private String publisher;
        private Year publicationYear;
        private LocalDate addedToLibraryDate;
        private BookAvailability bookAvailability;
        private BookReservation bookReservation;
        private User borrowedBy;
        private User reservedBy;

        public BookBuilder bookID(int bookID) {
            this.bookID = bookID;
        return this;
        }

        public BookBuilder isbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public BookBuilder title(String title) {
            this.title = title;
            return this;
        }

        public BookBuilder author(String author) {
            this.author = author;
            return this;
        }

        public BookBuilder publisher(String publisher) {
            this.publisher = publisher;
            return this;
        }

        public BookBuilder publicationYear(Year publicationYear) {
            this.publicationYear = publicationYear;
            return this;
        }

        public BookBuilder addedToLibraryDate(LocalDate date) {
            this.addedToLibraryDate = date;
            return this;
        }

        public BookBuilder bookAvailability(BookAvailability availability) {
            this.bookAvailability = availability;
            return this;
        }

        public BookBuilder bookReservation(BookReservation reservation) {
            this.bookReservation = reservation;
            return this;
        }

        public BookBuilder borrowedBy(Optional<User> userOpt) {
            this.borrowedBy = userOpt.orElse(null);
            return this;
        }

        public BookBuilder reservedBy(Optional<User> userOpt) {
            this.reservedBy = userOpt.orElse(null);
            return this;
        }

        public Book build() {
            // Validate required fields
            BookValidator.validateBookFields(isbn, title, author, publisher, publicationYear);

            // Set defaults if missing
            if (this.addedToLibraryDate == null) {
                this.addedToLibraryDate = LocalDate.now();
            }
            if (this.bookAvailability == null) {
                this.bookAvailability = BookAvailability.AVAILABLE;
            }
            if (this.bookReservation == null) {
                this.bookReservation = BookReservation.NOT_RESERVED;
            }

            return new Book(this);
        }
    }
}

