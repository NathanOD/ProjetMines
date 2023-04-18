package fr.sos.projetmines.commonutils.event;

import java.util.Map;

public interface OEventListener {

    void onEvent(Map<String, ?> eventData);
}
