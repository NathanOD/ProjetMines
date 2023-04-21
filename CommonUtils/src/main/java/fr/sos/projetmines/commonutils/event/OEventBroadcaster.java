package fr.sos.projetmines.commonutils.event;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OEventBroadcaster {

    /**
     * The set of registered event listeners.
     */
    private final Set<OEventListener> listeners;

    /**
     * Constructs an OEventBroadcaster with an empty set of listeners.
     */
    public OEventBroadcaster(){
        this.listeners = new HashSet<>();
    }

    /**
     * Broadcasts an event to all the registered listeners.
     * @param eventData the data associated with the event to be broadcasted.
     */
    public void broadcast(Map<String, ?> eventData){
        listeners.forEach(listener -> listener.onEvent(eventData));
    }

    /**
     * Registers an event listener with this broadcaster.
     * @param listener the event listener to be registered.
     */
    public void registerListener(OEventListener listener){
        listeners.add(listener);
    }

    /**
     * Removes a registered event listener from this broadcaster.
     * @param listener the event listener to be removed.
     */
    public void removeListener(OEventListener listener){
        listeners.removeIf(lstnr -> listener == lstnr);
    }
}
