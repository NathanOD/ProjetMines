package fr.sos.projetmines.inputsimulator;

import fr.sos.projetmines.commonutils.database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InputSimulatorDatabaseConnection extends DatabaseConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputSimulatorDatabaseConnection.class);


    public InputSimulatorDatabaseConnection(String address, String username, String password) {
        super(address, username, password);
    }

    /**
     * Creates the table into the database if it does not exist.
     * Does nothing if the connection to the database is not initialized
     */
    public void insertData(StripDataEntry entry) {
        if (!isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return;
        }
        try {
            String dataEntry = "INSERT INTO INPUT_OROWAN (input_error, lp, mat_id, x_time, x_loc, entry_thickness, exit_thickness, " +
                    "entry_tension, exit_tension, roll_force, forward_slip, mu, torque, average_sigma, " +
                    "water_flow_rate_top, water_flow_rate_bottom, oil_flow_rate_top, oil_flow_rate_down, roll_speed, stand_id) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

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
            dataEntryStatement.setInt(20, entry.getStandId());

            dataEntryStatement.executeUpdate();
            dataEntryStatement.close();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
    /**

     Inserts a new strip into the database.

     @param strip the Strip object to be inserted into the database.
     */
    public void insertStrip(Strip strip) {
        if (!isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return;
        }
        try {
            String exists = "SELECT strip_id FROM STRIPS WHERE strip_id = ?";
            PreparedStatement statement = connection.prepareStatement(exists);
            statement.setInt(1, strip.getStripId());
            if (!statement.executeQuery().next()) {
                String stripInsertion = "INSERT INTO STRIPS (strip_id, work_roll_diameter, rolled_length, " +
                        "young_modulus, backup_roll_diameter, backup_rolled_length) VALUES (?,?,?,?,?,?)";

                PreparedStatement stripCreation = connection.prepareStatement(stripInsertion);
                stripCreation.setInt(1, strip.getStripId());
                stripCreation.setFloat(2, strip.getWorkRollDiameter());
                stripCreation.setFloat(3, strip.getRolledLength());
                stripCreation.setFloat(4, strip.getYoungModulus());
                stripCreation.setFloat(5, strip.getBackupRollDiameter());
                stripCreation.setFloat(6, strip.getBackupRolledLength());
                stripCreation.executeUpdate();
                LOGGER.debug("Saved a new strip into the database!");
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
    /**

     Inserts a new stand into the database.
     @param standId the ID of the stand to be inserted into the database.
     */
    public void insertStand(int standId) {
        if (!isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return;
        }
        try {
            String exists = "SELECT stand_id FROM stands WHERE stand_id = ?";
            PreparedStatement statement = connection.prepareStatement(exists);
            statement.setInt(1, standId);
            if (!statement.executeQuery().next()) {
                String standInsertionQuery = "INSERT INTO stands (stand_id, enabled) VALUES (?, ?)";
                PreparedStatement standInsertion = connection.prepareStatement(standInsertionQuery);
                standInsertion.setInt(1, standId);
                standInsertion.setBoolean(2, true);
                standInsertion.executeUpdate();
                LOGGER.debug("Saved a new stand into the database!");
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
}