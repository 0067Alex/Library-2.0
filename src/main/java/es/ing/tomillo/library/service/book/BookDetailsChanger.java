package es.ing.tomillo.library.service.book;

import es.ing.tomillo.library.model.Book;

import java.time.Year;

public class BookDetailsChanger {

    public static void changeBookDetails(Book book, String newIsbn, String newTitle, String newAuthor, String newPublisher, Year newYear) {
        BookValidator.validateBookFields(newIsbn, newTitle, newAuthor, newPublisher, newYear);
        book.setIsbn(newIsbn);
        book.setTitle(newTitle);
        book.setAuthor(newAuthor);
        book.setPublisher(newPublisher);
        book.setPublicationYear(newYear);
    }
}
