package fr.sos.projetmines.calculator.database;

import fr.sos.projetmines.Stand;
import fr.sos.projetmines.commonutils.database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
/**
 * Class that handles requests to the "stands" table in the database.
 */
public class StandRequests {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandRequests.class);
    /**
     * Constructor that initializes the database connection.
     *
     * @param connection A DatabaseConnection object representing the connection to the database.
     */
    private final DatabaseConnection database;

    public StandRequests(DatabaseConnection connection) {
        this.database = connection;
    }


    /**
     * Retrieves a set of all stands in the database.
     *
     * @return An Optional containing a Set of Stand objects, or an empty Optional if an error occurred.
     */
    public Optional<Set<Stand>> getStands() {
        if (!database.isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return Optional.empty();
        }
        try {
            String addUserQuery = "SELECT stand_id, enabled FROM stands";
            PreparedStatement addUserStatement = database.getConnection().prepareStatement(addUserQuery);
            ResultSet resultSet = addUserStatement.executeQuery();
            Set<Stand> stands = new HashSet<>();

            while (resultSet.next()) {
                Stand stand = Stand.newBuilder()
                        .setStandId(resultSet.getInt("stand_id"))
                        .setEnabled(resultSet.getBoolean("enabled"))
                        .build();
                stands.add(stand);
            }

            addUserStatement.close();
            return Optional.of(stands);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Sets the enabled state of a stand with the given ID.
     *
     * @param id The ID of the stand to update.
     * @param state The new enabled state of the stand.
     * @return true if the update was successful, false otherwise.
     */
    public boolean setStandState(int id, boolean state) {
        if (!database.isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return false;
        }
        try {
            String jobQuery = "UPDATE stands SET enabled = ? WHERE stand_id = ?";
            PreparedStatement statement = database.getConnection().prepareStatement(jobQuery);
            statement.setBoolean(1, state);
            statement.setInt(2, id);
            int i = statement.executeUpdate();
            statement.close();
            return i > 0;
        } catch (SQLException exception) {
            LOGGER.error(exception.getMessage());
        }
        return false;
    }
}
