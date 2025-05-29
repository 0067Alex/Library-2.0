package es.ing.tomillo.library.cli;

import es.ing.tomillo.library.service.library.Library;

import java.util.Scanner;

public class UserSearchesCLI {
    private final Library library;
    private final Scanner scanner;

    public UserSearchesCLI(Library library, Scanner scanner) {
        this.library = library;
        this.scanner = scanner;
    }

    public void showUserSearchesMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n🔎 User Search Menu:");
            System.out.println("1. Search by ID");
            System.out.println("2. Search by DNI");
            System.out.println("3. Search by Name");
            System.out.println("4. List all users");
            System.out.println("0. Back");
            System.out.print("👉 Option: ");

            int option = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (option) {
                case 1 -> searchByUserID();
                case 2 -> searchByDNI();
                case 3 -> searchByName();
                case 4 -> listAllUsers();
                case 0 -> back = true;
                default -> System.out.println("⚠️ Invalid option.");
            }
        }
    }
    private void searchByUserID() {
        System.out.print("🔎 Enter user ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        var user = library.searchUserByID(id);
        if (user != null) {
            System.out.println("✅ Found user: " + user);
        } else {
            System.out.println("❌ No user found with ID: " + id);
        }
    }
    private void searchByDNI() {
        System.out.print("🔎 Enter DNI: ");
        String dni = scanner.nextLine();

        var user = library.searchUserByDNI(dni); // you need to implement this
        if (user != null) {
            System.out.println("✅ Found user: " + user);
        } else {
            System.out.println("❌ No user found with DNI: " + dni);
        }
    }

    private void searchByName() {
        System.out.print("🔎 Enter name: ");
        String name = scanner.nextLine();

        var users = library.searchUsersByName(name); // implement this too
        if (users.isEmpty()) {
            System.out.println("❌ No users found with that name.");
        } else {
            users.forEach(System.out::println);
        }
    }
    private void listAllUsers() {
        var users = library.searchAllUsers(); // implement this in Library
        if (users.isEmpty()) {
            System.out.println("ℹ️ No users registered.");
        } else {
            System.out.println("📋 Registered users:");
            users.forEach(System.out::println);
        }
    }

}