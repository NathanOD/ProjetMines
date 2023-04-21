package fr.sos.projetmines.commonutils.event;

import java.util.Map;

public interface OEventListener {

    /**
     * Handles an event by processing the associated event data.
     * @param eventData the data associated with the event.
     */
    void onEvent(Map<String, ?> eventData);
}
