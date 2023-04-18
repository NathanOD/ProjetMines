package fr.sos.projetmines.inputsimulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InputSimulatorDatabaseConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputSimulatorDatabaseConnection.class);
    private static final InputSimulatorDatabaseConnection INSTANCE = new InputSimulatorDatabaseConnection();

    private Connection connection;

    private String address;
    private String username, password;

    public InputSimulatorDatabaseConnection() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.error("H2 driver not found!");
        }
    }

    public static InputSimulatorDatabaseConnection getInstance() {
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
            if (!isConnected()) {
                connection = DriverManager.getConnection(address, username, password);
                LOGGER.info("Connection to the database established");
            }
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
    public void insertData(StripDataEntry entry, boolean saveStrip) {
        if (!isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return;
        }
        try {
            if (saveStrip) {
                String exists = "SELECT stand_id FROM STRIPS WHERE strip_id = ?";
                PreparedStatement statement = connection.prepareStatement(exists);
                statement.setInt(1, entry.getStrip().getStripId());
                if (!statement.executeQuery().next()) {
                    String stripInsertion = "INSERT INTO STRIPS (strip_id, stand_id, work_roll_diameter, rolled_length, " +
                            "young_modulus, backup_roll_diameter, backup_rolled_length) VALUES (?,?,?,?,?,?,?)";
                    Strip strip = entry.getStrip();

                    PreparedStatement stripCreation = connection.prepareStatement(stripInsertion);
                    stripCreation.setInt(1, strip.getStripId());
                    stripCreation.setInt(2, strip.getStandId());
                    stripCreation.setFloat(3, strip.getWorkRollDiameter());
                    stripCreation.setFloat(4, strip.getRolledLength());
                    stripCreation.setFloat(5, strip.getYoungModulus());
                    stripCreation.setFloat(6, strip.getBackupRollDiameter());
                    stripCreation.setFloat(7, strip.getBackupRolledLength());
                    stripCreation.executeUpdate();
                    LOGGER.debug("Saved a new strip into the database!");
                }
            }

            String dataEntry = "INSERT INTO INPUT_OROWAN (input_error, lp, mat_id, x_time, x_loc, entry_thickness, exit_thickness, " +
                    "entry_tension, exit_tension, roll_force, forward_slip, mu, torque, average_sigma, " +
                    "water_flow_rate_top, water_flow_rate_bottom, oil_flow_rate_top, oil_flow_rate_down, roll_speed) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement dataEntryStatement = connection.prepareStatement(dataEntry);
            dataEntryStatement.setFloat(1, entry.getInputError());
            dataEntryStatement.setInt(2, entry.getLp());
            dataEntryStatement.setInt(3, entry.getMatId());
            dataEntryStatement.setFloat(4, entry.getXTime());
            dataEntryStatement.setFloat(5, entry.getXLoc());
            dataEntryStatement.setFloat(6, entry.getEntryThickness());
            dataEntryStatement.setFloat(7, entry.getExitThickness());
            dataEntryStatement.setFloat(8, entry.getEntryTension());
            dataEntryStatement.setFloat(9, entry.getExitTension());
            dataEntryStatement.setFloat(10, entry.getRollForce());
            dataEntryStatement.setFloat(11, entry.getForwardSlip());
            dataEntryStatement.setFloat(12, entry.getMu());
            dataEntryStatement.setFloat(13, entry.getTorque());
            dataEntryStatement.setFloat(14, entry.getAverageSigma());
            dataEntryStatement.setFloat(15, entry.getWaterFlowRateTop());
            dataEntryStatement.setFloat(16, entry.getWaterFlowRateBottom());
            dataEntryStatement.setFloat(17, entry.getOilFlowRateTop());
            dataEntryStatement.setFloat(18, entry.getOilFlowRateBottom());
            dataEntryStatement.setFloat(19, entry.getRollSpeed());

            dataEntryStatement.executeUpdate();
            dataEntryStatement.close();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
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
}