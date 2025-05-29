package es.ing.tomillo.library.cli;

import es.ing.tomillo.library.database.DatabaseConnection;
import es.ing.tomillo.library.database.DatabaseManager;
import es.ing.tomillo.library.service.library.Library;
import es.ing.tomillo.library.util.H2ServerUtil;

import java.sql.SQLException;
import java.util.Scanner;

public class LibraryCLI {
    private static Library library;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeDatabase();

        System.out.print("🔐 Answer to initiate the library:\nwhich is the best bank in the world?:   ");
        String input = scanner.nextLine();

        if (!"ING".equalsIgnoreCase(input.trim())) {
            System.out.println("❌ Incorrect input. Exiting...");
            H2ServerUtil.stopServer();
            scanner.close();
            System.exit(1);
        }

        try {
            library = new Library();
            library.initializeData();
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load library data: " + e.getMessage(), e);
        }

        System.out.print("🔒 Answer to start the menu:\nIn which year was created?:   ");
        String yearInput = scanner.nextLine();

        if (!"1991".equals(yearInput.trim())) {
            System.out.println("❌ Incorrect year. Access denied.");
            H2ServerUtil.stopServer();
            scanner.close();
            System.exit(1);
        }

        System.out.println("📖 Hallo. Welkom to Internationale Nederlanden Library!\nBrought to you by Sir Alex.");

        boolean running = true;

        while (running) {
            System.out.println("\n🏠 Main Menu:\nChoose an option:");
            System.out.println("1. 📚 Book Management");
            System.out.println("2. 👤 User Management");
            System.out.println("3. Help (coming soon)");
            System.out.println("0. ❌ Exit");
            System.out.print("👉 Option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (option) {
                case 1:
                    openBookManagement();
                    break;
                case 2:
                    openUserManagement();
                    break;
                case 0:
                    System.out.println("👋 Exiting. Goodbye!");
                    H2ServerUtil.stopServer();
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("⚠️ Invalid option. Try again.");
            }
        }
        H2ServerUtil.stopServer();
        scanner.close();
    }

    /// methods
    /// BookMenu
    private static void openBookManagement() {
        BookManagementCLI bookMenu = new BookManagementCLI(library, scanner);
        bookMenu.showBookMenu();
    }

    /// UserMenu
    private static void openUserManagement() {
        UserManagementCLI userMenu = new UserManagementCLI(library, scanner);
        userMenu.showUserMenu();
    }

    private static void initializeDatabase() {
        try {
            org.h2.tools.Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            H2ServerUtil.startServer();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            DatabaseManager.initializeDatabase();
            System.out.println("📚 Library database initialized successfully.");
            System.out.println("👉 Open in browser: http://localhost:8082");
        } catch (SQLException e) {
            System.err.println("❌ Failed to initialize the database.");
            e.printStackTrace();
        }
    }

}

