package Mod5.Mod5.Dao;

import Mod5.Mod5.model.Picture;
import org.apache.commons.codec.binary.Hex;
import Mod5.Mod5.model.User;
import Mod5.Mod5.settings.DatabaseInitialiser;
import java.util.Date;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public enum UserDao {

    instance;

    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;

        // read bytes from the input stream and store them in buffer
        while ((len = in.read(buffer)) != -1) {
            // write bytes from the buffer into output stream
            os.write(buffer, 0, len);
        }

        return os.toByteArray();
    }

//    public static void main(String[] args) {
//        UserDao dao = UserDao.instance;
//        dao.register("Chris", "Cristian", "Trusin", "c.trusin@stundent.utwente.nl", "qwerty");
//    }

    public String getUserDetailsWithEmail(String email) {
        try {
            String query = "SELECT * FROM general_user " +
                    "WHERE email = ?";

            PreparedStatement statement = DatabaseInitialiser.getCon().prepareStatement(query);
            statement.setString(1, email);

            ResultSet resultSet = statement.executeQuery();
            // should be only one row
            if (resultSet.next()) {

                return resultSet.getString("username");
            } else {
                return null;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return null;
    }

    public User getUserDetails(String username, User user) {
        try {
            String query = "SELECT * FROM general_user AS u" +
                    "        WHERE u.username = ?";

            PreparedStatement statement = DatabaseInitialiser.getCon().prepareStatement(query);
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            // should be only one row
            if (resultSet.next()) {

                user.setUsername(username);
                user.setFirstName(resultSet.getString("first_name"));
                user.setLastName(resultSet.getString("last_name"));
                user.setEmail(resultSet.getString("email"));
                return user;
            } else {
                return null;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return null;
    }

    public byte[] getLogImage(String log_id, int room_id) {
        try {
            String query = "SELECT picture FROM log AS l" +
                    "        WHERE l.id_milisec = ? " +
                    "       AND l.room_id = ? ";


            PreparedStatement statement = DatabaseInitialiser.getCon().prepareStatement(query);
            statement.setLong(1, Long.parseLong(log_id));
            statement.setInt(2, room_id);

            ResultSet resultSet = statement.executeQuery();

            // should be only one row
            if (resultSet.next()) {
                return resultSet.getBytes("picture");
            } else {
                return null;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return null;
    }

    public long getCurrentImageID(int room_id) {
        try {
            String query = "SELECT picture_log_id FROM room AS r" +
                    "        WHERE r.room_id = ?";


            PreparedStatement statement = DatabaseInitialiser.getCon().prepareStatement(query);
            statement.setInt(1, room_id);

            ResultSet resultSet = statement.executeQuery();

            // should be only one row
            if (resultSet.next()) {
                return resultSet.getLong("picture_log_id");
            } else {
                return 0;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return 0;
    }

    public Picture getLastestImage(int room_id) {
        long cur = getCurrentImageID(room_id);
        try {
            String query = "SELECT picture, picture_status FROM log AS l" +
                    "        WHERE l.id_milisec = ? " +
                    "       AND l.room_id = ? ";


            PreparedStatement statement = DatabaseInitialiser.getCon().prepareStatement(query);
            statement.setLong(1, cur);
            statement.setInt(2, room_id);

            ResultSet resultSet = statement.executeQuery();

            // should be only one row
            if (resultSet.next()) {
                return new Picture(resultSet.getBytes("picture"), resultSet.getString("picture_status"));
            } else {
                return null;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return null;
    }



    public User getUserDetails(String username) {
        return getUserDetails(username, new User());
    }

    public String getUsersPassword(String username) {
        try {
            String query = "SELECT u.password FROM general_user AS u" +
                    "        WHERE u.username = ?";


            PreparedStatement statement = DatabaseInitialiser.getCon().prepareStatement(query);
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            // should be only one row
            if (resultSet.next()) {
                return resultSet.getString("password");
            } else {
                return null;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return null;
    }

    public String getSalt(String username) {
        try {
            PreparedStatement statement;
            String query = "SELECT u.salt FROM general_user AS u " +
                            " WHERE u.username = ?";


            statement = DatabaseInitialiser.getCon().prepareStatement(query);
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            // should be the first entry if exists
            if (resultSet.next()) {
                return resultSet.getString("salt");
            } else {
                return null;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return null;
    }

    public User getUser(String username, String password, boolean checkPassword) {
        try {
            String query;
            PreparedStatement statement;

            if (checkPassword) {
                String salt = getSalt(username);
                query = "SELECT u.username " +
                        "FROM general_user AS u " +
                        "WHERE u.username = ? AND u.password = ?";

                statement = DatabaseInitialiser.getCon().prepareStatement(query);
                statement.setString(1, username);
                statement.setString(2, getSHA256(getSHA256(password) + salt));
            } else {
                query = "SELECT u.username " +
                        "FROM general_user AS u " +
                        "WHERE u.username = ? ";

                statement = DatabaseInitialiser.getCon().prepareStatement(query);
                statement.setString(1, username);
            }

            ResultSet resultSet = statement.executeQuery();

            // should be the first entry if exists
            if (resultSet.next()) {
                User user = new User();
                user.setUsername(resultSet.getString("username"));
                return user;
            } else {
                return null;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return null;
    }

    public User getUserWithPassword(String username, String password) {
        return getUser(username, password, true);
    }

    public boolean isUsersEmail(String username, String email) {
        try {
            String query = "SELECT p.username FROM general_user AS p" +
                    "        WHERE p.username = ?" +
                    "          AND p.email = ?";

            PreparedStatement statement = DatabaseInitialiser.getCon().prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, email);

            ResultSet resultSet = statement.executeQuery();

            return !(!resultSet.isBeforeFirst() && resultSet.getRow() == 0);
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return false;
    }

    public boolean emailExist( String email) {
        try {
            String query = "SELECT * FROM general_user " +
                    "WHERE email = ?";

            PreparedStatement statement = DatabaseInitialiser.getCon().prepareStatement(query);
            statement.setString(1, email);

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return false;
    }

//    public boolean isActivated(String username) {
//        try {
//            String queryForActivation = "SELECT * FROM isActivated(?)";
//
//            PreparedStatement statementForActivation = DatabaseInitialiser.getCon().prepareStatement(queryForActivation);
//            statementForActivation.setString(1, username);
//
//            ResultSet resultSetForActivation = statementForActivation.executeQuery();
//
//            if (!resultSetForActivation.next()) {
//                return false;
//            }
//
//            return resultSetForActivation.getBoolean("r_is_activated");
//        } catch (SQLException se) {
//            se.printStackTrace();
//        }
//
//        return false;
//    }

    public boolean updateProfile(String username, String firstName, String lastName) {
        int totalRowsAffected = 0;

        try {
            String updateName = "UPDATE general_user " +
                    "SET first_name = ?, last_name = ? " +
                    "WHERE username = " + "'" + username + "'";

            if (!firstName.equals("") && !lastName.equals("")) {
                PreparedStatement statementForUpdate = DatabaseInitialiser.getCon().prepareStatement(updateName);
                statementForUpdate.setString(1, firstName);
                statementForUpdate.setString(2, lastName);

                int rowsAffected = statementForUpdate.executeUpdate();
                totalRowsAffected += rowsAffected;
            }

        } catch (SQLException se) {
            se.printStackTrace();
        }
        return totalRowsAffected > 0;
    }

//    public boolean activateAccount(String username) {
//        int totalRowsAffected = 0;
//
//        try {
//            String query = "UPDATE general_user " +
//                    "SET is_activated = ? "
//                    + "WHERE username = ? ";
//
//            PreparedStatement statementForUpdate = DatabaseInitialiser.getCon().prepareStatement(query);
//
//            statementForUpdate.setString(2, username);
//            statementForUpdate.setBoolean(1, true);
//            int rowsAffected = statementForUpdate.executeUpdate();
//
//            return rowsAffected > 0;
//
//        } catch (SQLException se) {
//            se.printStackTrace();
//        }
//        return totalRowsAffected > 0;
//    }


    public String getAlphaNumericString(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            int index = (int) (AlphaNumericString.length() * Math.random());

            sb.append(AlphaNumericString.charAt(index));
        }

        return sb.toString();
    }

    public boolean updatePassword(String username, String password) {
        int totalRowsAffected = 0;

        String salt = getAlphaNumericString(50);

        try {
            String updatePassword = "UPDATE general_user " +
                    "SET password = ? " +
                    ", salt = ? " +
                    "WHERE username = " + "'" + username + "'";

            if (!password.equals("")) {
                PreparedStatement statementForUpdate = DatabaseInitialiser.getCon().prepareStatement(updatePassword);
                statementForUpdate.setString(1, getSHA256(getSHA256(password) + salt));
                statementForUpdate.setString(2, salt);

                int rowsAffected = statementForUpdate.executeUpdate();
                totalRowsAffected += rowsAffected;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return totalRowsAffected > 0;
    }


    public boolean register(String username, String firstName, String lastName, String email, String password) {
        try {
            String salt = getAlphaNumericString(50);

            String query = "INSERT INTO general_user (username, first_name, last_name, email, password, salt) VALUES (?, ?, ?, ?, ?, ?) ";

            PreparedStatement statement = DatabaseInitialiser.getCon().prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, email);
            statement.setString(5, getSHA256(getSHA256(password) + salt));
            statement.setString(6, salt);


            int resultSet = statement.executeUpdate();

            // should be only one row
            if (resultSet > 0) {
                System.out.println("Successfully registered a new user!");
                return true;
            } else {
                System.out.println("Failed to register a new user!");
                return false;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }

        System.out.println("Failed to register a new user!");
        return false;
    }

    public String getSHA256(String password) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean updateProfileImage(String username, InputStream image) {
        if (!hasImage(username)) {
            return insertProfileImage(username, image);
        }

        try {
            String query = "UPDATE user_picture " +
                    "SET picture = ?"
                    + "WHERE username = ? ";

            PreparedStatement statementForUpdate = DatabaseInitialiser.getCon().prepareStatement(query);

            statementForUpdate.setString(2, username);
            statementForUpdate.setBytes(1, toByteArray(image));
            int rowsAffected = statementForUpdate.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean addLogFile(InputStream image, String status, int room_id) {
        Date date = new Date();
        long cur = date.getTime();
        try {
            String query = "INSERT INTO log(picture, picture_status, id_milisec, room_id) " +
                    "VALUES (?,?,?,?)";
            System.out.println(image);
            PreparedStatement statementForUpdate = DatabaseInitialiser.getCon().prepareStatement(query);

            statementForUpdate.setBytes(1, toByteArray(image));
            statementForUpdate.setString(2, status);

            statementForUpdate.setLong(3,cur);
            statementForUpdate.setInt(4,room_id);

            int rowsAffected = statementForUpdate.executeUpdate();
            System.out.println(statementForUpdate);
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateLatestImage(cur, room_id);
        return false;
    }

    public boolean updateLatestImage(long picture_log_id, int room_id) {
        try {
            String query = "UPDATE room " +
                    "SET picture_log_id = ? "
                    + " WHERE room_id = ? ";

            PreparedStatement statementForUpdate = DatabaseInitialiser.getCon().prepareStatement(query);

            statementForUpdate.setLong(1, picture_log_id);
            statementForUpdate.setInt(2, room_id);

            int rowsAffected = statementForUpdate.executeUpdate();
            System.out.println(statementForUpdate);
            return rowsAffected > 0;

        } catch (SQLException se) {
            se.printStackTrace();
        }

        return false;
    }

    public boolean updateStatus(String status, int room_id) {
        try {
            String query = "UPDATE room " +
                    " SET status = ? " +
                    " WHERE room_id = ? ";

            PreparedStatement statementForUpdate = DatabaseInitialiser.getCon().prepareStatement(query);

            statementForUpdate.setString(1, status);
            statementForUpdate.setInt(2, room_id);

            int rowsAffected = statementForUpdate.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException se) {
            se.printStackTrace();
        }

        return false;
    }


    public boolean insertProfileImage(String username, InputStream image) {
        try {
            String query = "INSERT INTO user_picture(username, picname, picture) " +
                    "VALUES (?,?,?)";

            PreparedStatement statement = DatabaseInitialiser.getCon().prepareStatement(query);

            statement.setString(1, username);
            statement.setString(2, "profile");
            statement.setBytes(3, toByteArray(image));
            boolean rowsAffected = statement.execute();

            return rowsAffected;

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasImage(String username) {
        try {
            String query = "SELECT u.username" +
                    "        FROM user_picture as u" +
                    "        WHERE username = ?";

            PreparedStatement statement = DatabaseInitialiser.getCon().prepareStatement(query);

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();

        } catch (SQLException se) {
            se.printStackTrace();
        }
        return false;
    }
}
