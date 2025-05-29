package es.ing.tomillo.library.service.user;

import es.ing.tomillo.library.model.fields.Email;
import es.ing.tomillo.library.model.fields.PhoneNumber;

import java.util.Objects;

public class UserValidator {

    public static void validateUserFields(String userDNI, String userName, String userSurname,
                                          Email userEmail, PhoneNumber userPhoneNumber) {

        // Null checks
        Objects.requireNonNull(userDNI, "User DNI cannot be null");
        Objects.requireNonNull(userName, "User name cannot be null");
        Objects.requireNonNull(userSurname, "User surname cannot be null");

        // Blank checks
        if (userDNI.isBlank())
            throw new IllegalArgumentException("User DNI cannot be blank");
        if (userName.isBlank())
            throw new IllegalArgumentException("User name cannot be blank");
        if (userSurname.isBlank())
            throw new IllegalArgumentException("User surname cannot be blank");

        // DNI format: (example for Spanish format: 8 digits + 1 uppercase letter)
        if (!userDNI.matches("\\d{8}[A-Z]"))
            throw new IllegalArgumentException("User DNI must be 8 digits followed by an uppercase letter (e.g. 12345678Z)");
    }
}