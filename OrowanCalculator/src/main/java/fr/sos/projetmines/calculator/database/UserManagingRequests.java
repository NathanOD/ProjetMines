package fr.sos.projetmines.calculator.database;

import fr.sos.projetmines.Job;
import fr.sos.projetmines.OrowanUserCredentials;
import fr.sos.projetmines.commonutils.database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

class UserManagingRequests {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticatorRequests.class);

    private final DatabaseConnection database;

    public UserManagingRequests(DatabaseConnection connection) {
        this.database = connection;
    }

    /**
     * Gets the job of a user
     *
     * @param username Name of the user
     * @return the user Job if the user is found
     */
    public Optional<Job> getUserJob(String username) {
        if (!database.isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return Optional.empty();
        }
        try {
            String jobQuery = "SELECT job FROM orowan_users WHERE username = ?";
            PreparedStatement statement = database.getConnection().prepareStatement(jobQuery);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Job job = Job.valueOf(resultSet.getString("job"));
                statement.close();
                return Optional.of(job);
            }
        } catch (SQLException exception) {
            LOGGER.error(exception.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Updates the role of an existing user
     *
     * @param username Name of the user
     * @param job      new job value
     */
    public boolean setUserJob(String username, Job job) {
        if (!database.isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return false;
        }
        try {
            String jobQuery = "UPDATE orowan_users SET job = ? WHERE username = ?";
            PreparedStatement statement = database.getConnection().prepareStatement(jobQuery);
            statement.setString(1, job.name());
            statement.setString(2, username);
            int updatedRows = statement.executeUpdate();
            statement.close();
            return updatedRows > 0;
        } catch (SQLException exception) {
            LOGGER.error(exception.getMessage());
        }
        return false;
    }


    /**
     * Adds a user to the database
     *
     * @param username name of the new user
     * @param password password of the new user (40 bytes max: sha-256)
     * @param salt     salt of the new user (16 Bytes)
     * @param job      user job
     */
    public void addUser(String username, byte[] password, byte[] salt, Job job) {
        if (!database.isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return;
        }
        try {
            String addUserQuery = "INSERT INTO orowan_users (username, password_hash, salt, job) VALUES (?, ?, ?, ?)";
            PreparedStatement addUserStatement = database.getConnection().prepareStatement(addUserQuery);
            addUserStatement.setString(1, username);
            addUserStatement.setBytes(2, password);
            addUserStatement.setBytes(3, salt);
            addUserStatement.setString(4, job.name());
            addUserStatement.executeUpdate();
            addUserStatement.close();
            LOGGER.debug("Added a new user ({}) to the database as a {}!", username, job.name());
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Deletes a user from the database
     *
     * @param username Name of the user to delete
     */
    public boolean deleteUser(String username) {
        if (!database.isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return false;
        }
        try {
            String addUserQuery = "DELETE FROM orowan_users WHERE username = ?";
            PreparedStatement addUserStatement = database.getConnection().prepareStatement(addUserQuery);
            addUserStatement.setString(1, username);
            int resultCount = addUserStatement.executeUpdate();
            addUserStatement.close();
            LOGGER.debug("Deleted the user ({}) from the database!", username);
            return resultCount > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    public Optional<Set<String>> getUsers() {
        if (!database.isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return Optional.empty();
        }
        try {
            String addUserQuery = "SELECT username FROM orowan_users";
            PreparedStatement addUserStatement = database.getConnection().prepareStatement(addUserQuery);
            ResultSet resultSet = addUserStatement.executeQuery();
            Set<String> users = new HashSet<>();

            while (resultSet.next()) {
                users.add(resultSet.getString("username"));
            }

            addUserStatement.close();
            return Optional.of(users);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        return Optional.empty();
    }
}
