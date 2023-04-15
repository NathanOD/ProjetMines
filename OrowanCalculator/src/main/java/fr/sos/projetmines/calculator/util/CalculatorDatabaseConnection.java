package fr.sos.projetmines.calculator.util;


import fr.sos.projetmines.Job;
import fr.sos.projetmines.calculator.model.OrowanDataOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

public class CalculatorDatabaseConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalculatorDatabaseConnection.class);
    private static final CalculatorDatabaseConnection INSTANCE = new CalculatorDatabaseConnection();

    private Connection connection;

    private String address;
    private String username, password;

    public CalculatorDatabaseConnection() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.error("H2 driver not found!");
        }
    }

    public static CalculatorDatabaseConnection getInstance() {
        return INSTANCE;
    }

    public void setDatabaseAddress(String databaseAddress) {
        this.address = databaseAddress;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Tries to connect to the Database
     *
     * @return whether the connection is successful
     */
    public boolean connect() {
        assert address != null && username != null && password != null;
        try {
            connection = DriverManager.getConnection(address, username, password);
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
     * @return whether the connection is alive
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(0);
        } catch (SQLException e) {
            return false;
        }
    }

    public double[] getData() {
        if (!isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return new double[0];
        }
        double[] results = new double[11];
        try {
            String dataQuery = "SELECT IO.lp, IO.entry_thickness, IO.exit_thickness, IO.entry_tension, IO.exit_tension,\n" +
                    "S.work_roll_diameter, S.young_modulus, IO.average_sigma, IO.mu, IO.roll_force, IO.forward_slip\n" +
                    "FROM INPUT_OROWAN IO\n" +
                    "JOIN STRIPS S ON IO.mat_id = S.strip_id\n" +
                    "ORDER BY IO.lp DESC LIMIT 1;";
            PreparedStatement dataQueryStatement = connection.prepareStatement(dataQuery);
            ResultSet resultSet = dataQueryStatement.executeQuery();
            resultSet.first();
            results[0] = resultSet.getDouble("LP");
            results[1] = resultSet.getDouble("entry_thickness");
            results[2] = resultSet.getDouble("exit_thickness");
            results[3] = resultSet.getDouble("entry_tension");
            results[4] = resultSet.getDouble("exit_tension");
            results[5] = resultSet.getDouble("work_roll_diameter");
            results[6] = resultSet.getDouble("young_modulus");
            results[7] = resultSet.getDouble("average_sigma");
            results[8] = resultSet.getDouble("mu");
            results[9] = resultSet.getDouble("roll_force");
            results[10] = resultSet.getDouble("forward_slip");
            resultSet.close();
            dataQueryStatement.close();

        } catch (SQLException e) {
            LOGGER.error(e.getLocalizedMessage());
            LOGGER.error(e.getMessage());
        }
        return results;
    }

    public void addData(OrowanDataOutput data) {
        if (!isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return;
        }
        try {
            String dataEntry = "INSERT INTO OUTPUT_OROWAN (\"CASE\", errors, offset_yield, friction, rolling_torque, " +
                    "sigma_moy, sigma_ini, sigma_out, sigma_max, force_error, slip_error, has_converged)" +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement dataEntryStatement = connection.prepareStatement(dataEntry);
            dataEntryStatement.setInt(1, data.getCaseId());
            dataEntryStatement.setString(2, data.getErrors());
            dataEntryStatement.setDouble(3, data.getOffsetYield());
            dataEntryStatement.setDouble(4, data.getFriction());
            dataEntryStatement.setDouble(5, data.getRollingTorque());
            dataEntryStatement.setDouble(6, data.getSigmaMoy());
            dataEntryStatement.setDouble(7, data.getSigmaIni());
            dataEntryStatement.setDouble(8, data.getSigmaOut());
            dataEntryStatement.setDouble(9, data.getSigmaMax());
            dataEntryStatement.setDouble(10, data.getForceError());
            dataEntryStatement.setDouble(11, data.getSlipError());
            dataEntryStatement.setString(12, data.getHasConverged());

            dataEntryStatement.executeUpdate();
            dataEntryStatement.close();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public byte[][] getPasswordAndSalt(String username) {
        if (!isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return new byte[][]{};
        }
        try {
            String dataQuery = "SELECT password_hash, salt FROM orowan_users WHERE username=?";
            PreparedStatement statement = connection.prepareStatement(dataQuery);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                byte[] password = resultSet.getBytes("password_hash");
                byte[] salt = resultSet.getBytes("salt");
                return new byte[][]{password, salt};
            }
        } catch (SQLException exception) {
            LOGGER.error(exception.getMessage());
        }
                return new byte[][]{};
    }

    public boolean checkUserExistance(String username) {
        if (!isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");

        }
        try {
            String dataQuery = "SELECT COUNT(username) FROM orowan_users WHERE username=?";
            PreparedStatement statement = connection.prepareStatement(dataQuery);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            resultSet.first();
            int result  = resultSet.getInt(0);
            return (result >= 1);
        } catch (SQLException exception) {
            LOGGER.error(exception.getMessage());
        }
        return false;
    }
    /*
    public boolean checkStandExistance(int  standId) {
        if (!isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");

        }
        try {
            String dataQuery = "SELECT COUNT(stand_id) FROM STRIPS WHERE stand_id =?";
            PreparedStatement statement = connection.prepareStatement(dataQuery);
            statement.setint(1, stand_id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.first();
            int result  = resultSet.getInt(0);
            return (result >= 1);
        } catch (SQLException exception) {
            LOGGER.error(exception.getMessage());
        }
        return false;
    }
     */

    public Optional<Job> getJob(String username) {
        if (!isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return Optional.empty();
        }
        try {
            String jobQuery = "SELECT job FROM orowan_users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(jobQuery);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(Job.valueOf(resultSet.getString("job")));
            }
        } catch (SQLException exception) {
            LOGGER.error(exception.getMessage());
        }
        return Optional.empty();
    }
    public Optional<Job> setJob(String username , Job job) {
        if (!isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return Optional.empty();
        }
        try {
            String jobQuery = "UPDATE orowan_users SET job = ? WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(jobQuery);
            statement.setString(1, job.name());
            statement.setString(2, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(Job.valueOf(resultSet.getString("job")));
            }
        } catch (SQLException exception) {
            LOGGER.error(exception.getMessage());
        }
        return Optional.empty();
    }



    public void addUser(String username, byte[] password, byte[] salt, Job job) {
        if (!isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return;
        }
        try {
            String addUserQuery = "INSERT INTO orowan_users (username, password_hash, salt, job) VALUES (?, ?, ?, ?)";
            PreparedStatement addUserStatement = connection.prepareStatement(addUserQuery);
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

    public void deleteUser(String username) {
        if (!isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return;
        }
        try {
            String addUserQuery = "DELETE FROM orowan_users WHERE username = ?";
            PreparedStatement addUserStatement = connection.prepareStatement(addUserQuery);
            addUserStatement.setString(1, username);
            addUserStatement.executeUpdate();
            addUserStatement.close();
            LOGGER.debug("deleted a  user ({}) from the database as a {}!", username);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
