package es.ing.tomillo.library.service.library;
/// outdated
import es.ing.tomillo.library.database.UserDAO;
import es.ing.tomillo.library.model.User;

import java.util.List;

public class UserSearcher {

    public User searchUserByID(int userID) {
        try {
            return UserDAO.searchUserByID(userID);
        } catch (Exception e) {
            System.out.println("❌ Failed to find user by ID: " + e.getMessage());
            return null;
        }
    }

    public List<User> searchUsersByName(String name) {
        try {
            return UserDAO.searchUsersByName(name);
        } catch (Exception e) {
            System.out.println("❌ Failed to search users by name: " + e.getMessage());
            return List.of();
        }
    }

    public List<User> listAllUsers() {
        try {
            List<User> users = UserDAO.searchAllUsers();
            if (users.isEmpty()) {
                System.out.println("ℹ️ No users found.");
            } else {
                System.out.println("👥 Registered Users:");
                for (User user : users) {
                    System.out.println(" - " + user.getUserName() + " (ID: " + user.getUserID() + ")");
                }
            }
            return users;
        } catch (Exception e) {
            System.out.println("❌ Failed to list users: " + e.getMessage());
            return List.of();
        }
    }
}
