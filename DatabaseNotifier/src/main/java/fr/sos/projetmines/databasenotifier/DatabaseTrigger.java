package fr.sos.projetmines.databasenotifier;

import org.h2.api.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseTrigger implements Trigger {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseTrigger.class);


    /**

     Initializes the trigger with the given parameters.
     @param conn the connection to the database
     @param schemaName the name of the schema
     @param triggerName the name of the trigger
     @param tableName the name of the table
     @param before true if the trigger is before, false otherwise
     @param type the type of the trigger
     @throws SQLException if there is an error with the SQL
     */
    @Override
    public void init(Connection conn, String schemaName, String triggerName, String tableName,
                     boolean before, int type) throws SQLException {
        //Call the call if never called
        DatabaseNotifier.getInstance();
    }

    /**

     Fires the trigger when necessary and broadcasts data if the DatabaseNotifier is up.
     @param conn the connection to the database
     @param oldRow the old row of the database table
     @param newRow the new row of the database table
     */

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) {
        if (DatabaseNotifier.getInstance().isUp()) {
            Map<String, Object> data = new HashMap<>();
            data.put("entryId", newRow[0]);
            DatabaseNotifier.getInstance().getBroadcaster().broadcast(data);
        }
    }
    /**

     Closes the trigger.
     @throws SQLException if there is an error with the SQL
     */
    @Override
    public void close() throws SQLException {
        Trigger.super.close();
    }
    /**

     Removes the trigger.
     @throws SQLException if there is an error with the SQL
     */
    @Override
    public void remove() throws SQLException {
        Trigger.super.remove();
    }
}
