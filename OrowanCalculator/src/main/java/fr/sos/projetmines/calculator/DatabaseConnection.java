package fr.sos.projetmines.calculator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnection.class);
    private static final DatabaseConnection INSTANCE = new DatabaseConnection();

    private Connection connection;

    private String address;

    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }

    public DatabaseConnection(){
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.error("H2 driver not found!");
        }
    }

    public void setDatabaseAddress(String databaseAddress) {
        this.address = databaseAddress;
    }


    /**
     * Tries to connect to the Database
     *
     * @return whether the connection is successful
     */
    public boolean connect(String identifier, String password) {
        assert address != null;
        try {
            connection = DriverManager.getConnection(address + "?reconnect=on", identifier, password);
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
