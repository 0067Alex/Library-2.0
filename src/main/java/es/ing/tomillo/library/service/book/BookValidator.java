package es.ing.tomillo.library.service.book;

import es.ing.tomillo.library.model.User;

import java.time.Year;
import java.util.Objects;

public class BookValidator {
    /// Defense against nulls or wrong formats
    /// ///validateBookFields (Static means you can call it without creating an object of that class)
    public static void validateBookFields(String isbn, String title, String author, String publisher, Year publicationYear) {
        Objects.requireNonNull(isbn, "ISBN cannot be null");
        Objects.requireNonNull(title, "Title cannot be null");
        Objects.requireNonNull(author, "Author cannot be null");

        if (isbn.isBlank())
            throw new IllegalArgumentException("ISBN cannot be blank");
        if (!isbn.matches("\\d{10}|\\d{13}"))
            throw new IllegalArgumentException("ISBN must be exactly 10 or 13 digits and contain only numbers");
        if (title.isBlank())
            throw new IllegalArgumentException("Title cannot be blank");
        if (author.isBlank())
            throw new IllegalArgumentException("Author cannot be blank");
        if (publisher != null && publisher.isBlank())
            throw new IllegalArgumentException("Publisher cannot be blank if provided");
        if (publicationYear != null && publicationYear.isAfter(Year.now()))
            throw new IllegalArgumentException("Publication year cannot be in the future");
    }

    public static class GetSafeFullName {
        public static String getSafeFullName(User user) {
            if (user == null) return "None";
            return user.getUFN();
        }
    }
}
