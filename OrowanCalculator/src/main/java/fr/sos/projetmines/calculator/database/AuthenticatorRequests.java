package fr.sos.projetmines.calculator.database;

import fr.sos.projetmines.commonutils.database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class AuthenticatorRequests {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticatorRequests.class);

    private final DatabaseConnection database;

    public AuthenticatorRequests(DatabaseConnection connection) {
        this.database = connection;
    }

    /**
     * @param username name of password and salt owner
     * @return the password and salt of the provided Username
     */
    public byte[][] getPasswordAndSalt(String username) {
        if (!database.isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return new byte[][]{};
        }
        try {
            String dataQuery = "SELECT password_hash, salt FROM orowan_users WHERE username=?";
            PreparedStatement statement = database.getConnection().prepareStatement(dataQuery);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                byte[] password = resultSet.getBytes("password_hash");
                byte[] salt = resultSet.getBytes("salt");
                statement.close();
                return new byte[][]{password, salt};
            }
            statement.close();
        } catch (SQLException exception) {
            LOGGER.error(exception.getMessage());
        }
        return new byte[][]{};
    }

    /**
     * @param username name of the user to check
     * @return whether a user is saved into the database
     */
    public boolean checkUserExistence(String username) {
        if (!database.isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
        }

        try {
            String dataQuery = "SELECT username FROM orowan_users WHERE username=?";
            PreparedStatement statement = database.getConnection().prepareStatement(dataQuery);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            boolean exists = false;
            if(resultSet.next()){
                exists = true;
            }
            statement.close();
            return exists;
        } catch (SQLException exception) {
            LOGGER.error(exception.getMessage());
        }
        return false;
    }
}
