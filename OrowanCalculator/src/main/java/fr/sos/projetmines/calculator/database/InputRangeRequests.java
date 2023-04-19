package fr.sos.projetmines.calculator.database;

import fr.sos.projetmines.calculator.model.OrowanInputDataRange;
import fr.sos.projetmines.commonutils.database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;

class InputRangeRequests {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputRangeRequests.class);

    private final DatabaseConnection database;

    public InputRangeRequests(DatabaseConnection connection) {
        this.database = connection;
    }

    public Set<OrowanInputDataRange> getInputRanges() {
        Set<OrowanInputDataRange> inputRanges = new HashSet<>();
        if (!database.isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return inputRanges;
        }

        try {
            String dataQuery = "SELECT constraint_name, min_value, max_value FROM input_data_ranges";

            PreparedStatement statement = database.getConnection().prepareStatement(dataQuery);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                float minValue = resultSet.getFloat("min_value");
                if (resultSet.wasNull()) {
                    minValue = Float.MIN_VALUE;
                }
                float maxValue = resultSet.getFloat("max_value");
                if (resultSet.wasNull()) {
                    maxValue = Float.MAX_VALUE;
                }
                String constraintName = resultSet.getString("constraint_name");
                inputRanges.add(new OrowanInputDataRange(constraintName, minValue, maxValue));
            }
            statement.close();
            return inputRanges;
        } catch (SQLException exception) {
            LOGGER.error(exception.getMessage());
        }
        return inputRanges;
    }

    public void updateConstraintInputRange(OrowanInputDataRange inputDataRange) {
        if (!database.isConnected()) {
            LOGGER.warn("Impossible to query the database: the connection is not established");
            return;
        }
        try {
            String jobQuery = "UPDATE input_data_ranges SET min_value = ?, max_value = ? WHERE constaint_name = ?";
            PreparedStatement statement = database.getConnection().prepareStatement(jobQuery);
            if (inputDataRange.getMinValue() != Float.MIN_VALUE) {
                statement.setFloat(1, inputDataRange.getMinValue());
            } else {
                statement.setNull(1, Types.FLOAT);
            }
            if (inputDataRange.getMaxValue() != Float.MAX_VALUE) {
                statement.setFloat(2, inputDataRange.getMaxValue());
            } else {
                statement.setNull(2, Types.FLOAT);
            }
            statement.setString(3, inputDataRange.getConstraintName());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException exception) {
            LOGGER.error(exception.getMessage());
        }
    }
}
