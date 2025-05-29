package es.ing.tomillo.library.database;

import es.ing.tomillo.library.model.User;
import es.ing.tomillo.library.model.fields.Email;
import es.ing.tomillo.library.model.fields.PhoneNumber;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static es.ing.tomillo.library.database.DatabaseConnection.getConnection;

public class UserDAO extends BaseDAO {

    private static final String INSERT_USER =
            "INSERT INTO users (user_dni, user_name, user_surname, user_email, user_phone_number) VALUES (?, ?, ?, ?, ?)";

    private static final String SEARCH_BY_USER_ID =
            "SELECT * FROM users WHERE user_id = ?";

    private static final String SEARCH_BY_DNI =
            "SELECT * FROM users WHERE user_dni = ?";

    private static final String SEARCH_BY_NAME =
            "SELECT * FROM users WHERE user_name LIKE ?";

    private static final String SEARCH_ALL_USERS =
            "SELECT * FROM users";

    private static final String DELETE_USER =
            "DELETE FROM users WHERE user_id = ?";

    public static User insertUser(User user) {
        try (var conn = getConnection();
             var stmt = conn.prepareStatement(INSERT_USER, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUserDNI());
            stmt.setString(2, user.getUserName());
            stmt.setString(3, user.getUserSurname());
            stmt.setString(4, user.getUserEmail() != null ? user.getUserEmail().toString() : null);
            stmt.setString(5, user.getUserPhoneNumber() != null ? user.getUserPhoneNumber().toString() : null);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Inserting user failed, no rows affected.");
            }

            try (var generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    return searchUserByID(generatedId);
                } else {
                    throw new SQLException("Inserting user failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static User searchUserByID(int userID) {
        return executeQuerySingle(SEARCH_BY_USER_ID, UserDAO::createUserFromResultSet, userID);
    }

    public static User searchUserByDni(String userDNI) {
        return executeQuerySingle(SEARCH_BY_DNI, UserDAO::createUserFromResultSet, userDNI);
    }

    public static List<User> searchUsersByName(String userName) {
        return executeQuery(SEARCH_BY_NAME, UserDAO::createUserFromResultSet, "%" + userName + "%");
    }

    public static List<User> searchAllUsers() {
        return executeQuery(SEARCH_ALL_USERS, UserDAO::createUserFromResultSet);
    }

    public static void deleteUser(int userId) {
        executeUpdate(DELETE_USER, userId);
    }

    protected static User createUserFromResultSet(ResultSet rs) throws SQLException {
        String userDNI = rs.getString("user_dni");
        String userName = rs.getString("user_name");
        String userSurname = rs.getString("user_surname");
        String emailStr = rs.getString("user_email");
        String phoneStr = rs.getString("user_phone_number");

        Email email = (emailStr != null) ? new Email(emailStr) : null;
        PhoneNumber phone = (phoneStr != null) ? new PhoneNumber(phoneStr) : null;

        return new User.UserBuilder()
                .userID(rs.getInt("user_id"))
                .userDNI(userDNI)
                .userName(userName)
                .userSurname(userSurname)
                .userEmail(email)
                .userPhoneNumber(phone)
                .build();
    }
}
