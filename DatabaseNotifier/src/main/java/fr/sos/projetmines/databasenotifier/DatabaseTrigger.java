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

    @Override
    public void init(Connection conn, String schemaName, String triggerName, String tableName,
                     boolean before, int type) throws SQLException {
        //Call the call if never called
        DatabaseNotifier.getInstance();
    }

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) {
        if (DatabaseNotifier.getInstance().isUp()) {
            Map<String, Object> data = new HashMap<>();
            data.put("entryId", newRow[0]);
            DatabaseNotifier.getInstance().getBroadcaster().broadcast(data);
        }
    }

    @Override
    public void close() throws SQLException {
        Trigger.super.close();
    }

    @Override
    public void remove() throws SQLException {
        Trigger.super.remove();
    }
}
