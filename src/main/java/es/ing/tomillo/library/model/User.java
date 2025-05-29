package es.ing.tomillo.library.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import es.ing.tomillo.library.model.fields.Email;
import es.ing.tomillo.library.model.fields.PhoneNumber;
import es.ing.tomillo.library.service.user.UserValidator;

public class User {
    private static final AtomicInteger nextUserID = new AtomicInteger(0);
    private final int userID;
    private String userDNI;
    private String userName;
    private String userSurname;
    private Email userEmail;
    private PhoneNumber userPhoneNumber;
    private boolean isBorrower;
    private boolean isReserver;
    private int borrowedBooksCount;
    private final List<Book> borrowedBooksList;
    private static final int MAX_BORROWED_BOOKS = 5;
    private final List<Book> reservedBooksList;
    private static final int MAX_RESERVED_BOOKS = 1;

    private User(UserBuilder builder) {
        this.userID = builder.userID != null ? builder.userID : nextUserID.getAndIncrement();
        this.userDNI = builder.userDNI;
        this.userName = builder.userName;
        this.userSurname = builder.userSurname;
        this.userEmail = builder.userEmail;
        this.userPhoneNumber = builder.userPhoneNumber;
        this.isBorrower = false;
        this.isReserver = false;
        this.borrowedBooksList = new ArrayList<>();
        this.reservedBooksList = new ArrayList<>();
    }

    /// Getters and setters
    /// /// userID
    public int getUserID() {
        return userID;
    }

    /// /// DNI
    public String getUserDNI() {
        return userDNI;
    }

    /// /// userName
    public String getUserName() {
        return userName;
    }

    /// /// surName
    public String getUserSurname() {
        return userSurname;
    }

    /// /// email
    public Email getUserEmail() {
        return userEmail;
    }

    /// /// userPhoneNumber
    public PhoneNumber getUserPhoneNumber() {
        return userPhoneNumber;
    }

    /// /// get UserFullName (una utilidad que dejé para algún println)
    public String getUFN() {
        return userName + " " + userSurname;
    }

    /// /// borrowedBooksList
    public List<Book> getBorrowedBooksList() {
        return borrowedBooksList;
    }

    /// MaxBorrowed

    public int getMaxBorrowedBooks() {
        return MAX_BORROWED_BOOKS;
    }

    /// MaxReserved

    public int getMaxReservedBooks() {
        return MAX_RESERVED_BOOKS;
    }

    /// /// reservedBookList
    public List<Book> getReservedBooksList() {
        return reservedBooksList;
    }

    /// ///
    /// TODO: Implementar método mostrar Informacion según el ejercicio 2
    @Override
    public String toString() {
        return "ID: " + userID + '\'' +
                "DNI: " + userDNI + '\'' +
                ", Name: " + userName + '\'' +
                ", Surname: " + userSurname + '\'' +
                ", Email: " + userEmail + '\'' +
                ", Phone number: " + userPhoneNumber;}

    /// TODO: Implementar método equals para comparar usuarios por ID
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userID == user.userID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID);
    }

    /// Builder
    public static class UserBuilder {
        private Integer userID;
        private String userDNI;
        private String userName;
        private String userSurname;
        private Email userEmail = null;
        private PhoneNumber userPhoneNumber = null;

        public UserBuilder userID (int userID) {/// Desgraciada línea de código que me hizo perder 2 horas jajaja
            this.userID = userID;
            return this;
        }

        public UserBuilder userDNI(String userDNI) {
            this.userDNI = userDNI;
            return this;
        }

        public UserBuilder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public UserBuilder userSurname(String userSurname) {
            this.userSurname = userSurname;
            return this;
        }

        public UserBuilder userEmail(Email userEmail) {
            this.userEmail = userEmail;
            return this;
        }

        public UserBuilder userPhoneNumber(PhoneNumber userPhoneNumber) {
            this.userPhoneNumber = userPhoneNumber;
            return this;
        }

        public User build() {
            UserValidator.validateUserFields(userDNI, userName, userSurname, userEmail, userPhoneNumber);
            return new User(this);
        }
    }
}