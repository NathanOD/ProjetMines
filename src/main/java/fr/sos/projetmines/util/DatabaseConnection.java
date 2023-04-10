package fr.sos.projetmines.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DatabaseConnection {

    private static final DatabaseConnection INSTANCE = new DatabaseConnection();

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnection.class);
    private Connection connection;

    public DatabaseConnection() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }

    /**
     * Tries to connect to the Database
     *
     * @return whether the connection is successful
     */
    public boolean connect(String databaseName, String identifier, String password) {
        try {
            connection = DriverManager.getConnection("jdbc:h2:~/" + databaseName, identifier, password);
            LOGGER.info("Connection to the database established");
            return isConnected();
        } catch (SQLException e) {
            LOGGER.error("Connection to the database cannot be established! ({})", e.getMessage());
            return false;
        }
    }

    public void closeConnection() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Creates the table into the database if it does not exist.
     * Does nothing if the connection to the database is not initialized
     */
    public void checkTable() {
        String query = "CREATE TABLE IF NOT EXISTS XX (YY)";

        if (!isConnected()) {
            LOGGER.info("Could not check the table existance! The database is not connected!");
            return;
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @return whether the connection is alive
     */
    private boolean isConnected() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(0);
        } catch (SQLException e) {
            return false;
        }
    }
}
