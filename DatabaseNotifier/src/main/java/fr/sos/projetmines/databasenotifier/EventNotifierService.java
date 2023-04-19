package fr.sos.projetmines.databasenotifier;

import fr.sos.projetmines.EventNotification;
import fr.sos.projetmines.EventNotifierGrpc;
import fr.sos.projetmines.SubscribingRequest;
import fr.sos.projetmines.commonutils.event.OEventBroadcaster;
import fr.sos.projetmines.commonutils.event.OEventListener;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class EventNotifierService extends EventNotifierGrpc.EventNotifierImplBase implements OEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventNotifierService.class);

    private final Set<StreamObserver<EventNotification>> responses;

    public EventNotifierService(OEventBroadcaster broadcaster) {
        this.responses = new HashSet<>();
        broadcaster.registerListener(this);

    }

    @Override
    public void rowInserted(SubscribingRequest request, StreamObserver<EventNotification> responseObserver) {
        LOGGER.info("New event notifier client!");
        responses.add(responseObserver);
    }

    @Override
    public void onEvent(Map<String, ?> eventData) {
        EventNotification data = EventNotification.newBuilder()
                .setEntryId((int) eventData.get("entryId")).build();
        Iterator<StreamObserver<EventNotification>> respIter = responses.iterator();
        while(respIter.hasNext()){
            StreamObserver<EventNotification> stream = respIter.next();
            try {
                stream.onNext(data);
            } catch (StatusRuntimeException exception) {
                respIter.remove();
            }
        }
    }
}
