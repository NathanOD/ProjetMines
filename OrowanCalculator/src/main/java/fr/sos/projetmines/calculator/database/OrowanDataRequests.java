package fr.sos.projetmines.calculator.database;

import fr.sos.projetmines.calculator.model.OrowanDataOutput;
import fr.sos.projetmines.calculator.model.OrowanSensorData;
import fr.sos.projetmines.commonutils.database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

class OrowanDataRequests {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticatorRequests.class);

    private final DatabaseConnection database;

    public OrowanDataRequests(DatabaseConnection connection) {
        this.database = connection;
    }

    /**
     * @return the sensors data used for Orowan calculation linked to the given entry id
     */
    public Optional<OrowanSensorData> retrieveSensorData(int entryId) {
        if (!database.isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return Optional.empty();
        }
        try {
            String dataQuery = "SELECT S.stand_id, IO.lp, IO.entry_thickness, IO.exit_thickness, IO.entry_tension, IO.exit_tension, " +
                    "S.work_roll_diameter, S.young_modulus, IO.average_sigma, IO.mu, IO.roll_force, IO.forward_slip " +
                    "FROM INPUT_OROWAN IO " +
                    "JOIN STRIPS S ON IO.mat_id = S.strip_id\n" +
                    "WHERE IO.ENTRY_ID = ?";
            PreparedStatement dataQueryStatement = database.getConnection().prepareStatement(dataQuery);
            dataQueryStatement.setInt(1, entryId);
            ResultSet rs = dataQueryStatement.executeQuery();
            rs.first();

            OrowanSensorData data = new OrowanSensorData(rs.getInt("stand_id"), rs.getInt("lp"),
                    rs.getFloat("entry_thickness"), rs.getFloat("exit_thickness"),
                    rs.getFloat("entry_tension"), rs.getFloat("exit_tension"),
                    rs.getFloat("work_roll_diameter"), rs.getFloat("young_modulus"),
                    rs.getFloat("average_sigma"), rs.getFloat("mu"),
                    rs.getFloat("roll_force"), rs.getFloat("forward_slip"));

            rs.close();
            dataQueryStatement.close();
            return Optional.of(data);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Saves the Orowan output data into the database
     */
    public void saveOrowanOutput(OrowanDataOutput data) {
        if (!database.isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return;
        }
        try {
            String dataEntry = "INSERT INTO OUTPUT_OROWAN (\"CASE\", errors, offset_yield, friction, rolling_torque, " +
                    "sigma_moy, sigma_ini, sigma_out, sigma_max, force_error, slip_error, has_converged)" +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement dataEntryStatement = database.getConnection().prepareStatement(dataEntry);
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
}
