package es.ing.tomillo.library.service.library;

import es.ing.tomillo.library.database.UserDAO;
import es.ing.tomillo.library.model.Book;
import es.ing.tomillo.library.model.User;

import java.util.Map;

public class UserManager {
    private final Map<Integer, User> users;

    public UserManager(Map<Integer, User> users) {
        this.users = users;
    }

    public User addUser(User user) {
        try {
            User createdUser = UserDAO.insertUser(user); // Save to database
            users.put(createdUser.getUserID(), createdUser); // Correctly store by real ID
            return createdUser;
        } catch (RuntimeException e) {
            System.err.println("❌ Error saving user to database: " + e.getMessage());
            return null;
        }
    }
}
