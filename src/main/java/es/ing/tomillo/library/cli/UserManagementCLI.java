package es.ing.tomillo.library.cli;

import es.ing.tomillo.library.model.fields.Email;
import es.ing.tomillo.library.model.fields.PhoneNumber;
import es.ing.tomillo.library.service.library.Library;
import es.ing.tomillo.library.model.User;

import java.util.Scanner;

public class UserManagementCLI {
    private final Library library;
    private final Scanner scanner;
    private final UserSearchesCLI userSearchesCLI;

    public UserManagementCLI(Library library, Scanner scanner) {
        this.library = library;
        this.scanner = scanner;
        this.userSearchesCLI = new UserSearchesCLI(library, scanner);
    }

    public void showUserMenu() {
        System.out.println("\n👤 User Management Menu:");
        System.out.println("1. ➕ Add User");
        System.out.println("2. Search User");
        System.out.println("3. Delete User (coming soon)");
        System.out.print("👉 Option: ");
        int option = scanner.nextInt();
        scanner.nextLine();
        switch (option) {
            case 1:
                adduserCLI();
                break;
            case 2:
                userSearchesCLI.showUserSearchesMenu();
            default:
                System.out.println("⚠️ Invalid option. Try again.");
        }
    }

    private void adduserCLI() {
        System.out.print("Option 1 selected. Add a new user:\n");

        // DNI input and validation
        System.out.print("User DNI: ");
        String userDNI = scanner.nextLine().toUpperCase(); // Normalize to uppercase
        if (!userDNI.matches("\\d{8}[A-Z]")) {
            System.out.println("❌ Invalid DNI. Format must be 8 digits followed by 1 letter (e.g., 12345678A).");
            System.out.println("Returning to the user menu...");
            return;
        }

        System.out.print("👤 User Name: ");
        String userName = scanner.nextLine();

        System.out.print("User Surname: ");
        String userSurname = scanner.nextLine();

        System.out.print("User Email: ");
        Email userEmail = new Email(scanner.nextLine());

        // Phone number input and validation
        System.out.print("User phone number: ");
        String phoneNumberInput = scanner.nextLine();
        if (!phoneNumberInput.matches("[67]\\d{8}")) {
            System.out.println("❌ Invalid phone number. Must be 9 digits and start with 6 or 7 (e.g., 657835462).");
            System.out.println("Returning to the user menu...");
            return;
        }

        PhoneNumber userPhoneNumber = new PhoneNumber(phoneNumberInput);

        User newUser = new User.UserBuilder()
                .userDNI(userDNI)
                .userName(userName)
                .userSurname(userSurname)
                .userEmail(userEmail)
                .userPhoneNumber(userPhoneNumber)
                .build();

        User addedUser = library.addUser(newUser);

        if (addedUser != null) {
            System.out.println("✅ " + addedUser.getUserName() + " added successfully: ");
            System.out.println("Assigned User ID = " + addedUser.getUserID());
        } else {
            System.out.println("❌ Failed to add user.");
        }
    }
}
