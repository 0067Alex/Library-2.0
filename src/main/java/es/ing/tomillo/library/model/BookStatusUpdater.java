package es.ing.tomillo.library.model;

import es.ing.tomillo.library.model.enums.BookAvailability;
import es.ing.tomillo.library.model.enums.BookReservation;

public class BookStatusUpdater {

    public static void updateBookStatus(Book book) {
        if (book == null) return;

        // Update availability
        if (book.getBorrowedBy() != null) {
            book.setBookAvailability(BookAvailability.BORROWED);
        } else {
            book.setBookAvailability(BookAvailability.AVAILABLE);
        }

        // Update reservation
        if (book.getReservedBy() != null) {
            book.setBookReservation(BookReservation.RESERVED);
        } else {
            book.setBookReservation(BookReservation.NOT_RESERVED);
        }
    }
}
