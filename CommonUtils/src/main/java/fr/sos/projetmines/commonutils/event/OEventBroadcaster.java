package fr.sos.projetmines.commonutils.event;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OEventBroadcaster {

    private final Set<OEventListener> listeners;

    public OEventBroadcaster(){
        this.listeners = new HashSet<>();
    }

    public void broadcast(Map<String, ?> eventData){
        listeners.forEach(listener -> listener.notifyEvent(eventData));
    }

    public void registerListener(OEventListener listener){
        listeners.add(listener);
    }

    public void removeListener(OEventListener listener){
        listeners.removeIf(lstnr -> listener == lstnr);
    }
}
