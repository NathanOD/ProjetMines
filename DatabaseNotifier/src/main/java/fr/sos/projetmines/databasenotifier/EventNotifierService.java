package fr.sos.projetmines.databasenotifier;

import fr.sos.projetmines.EventNotification;
import fr.sos.projetmines.EventNotifierGrpc;
import fr.sos.projetmines.SubscribingRequest;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class EventNotifierService extends EventNotifierGrpc.EventNotifierImplBase implements PropertyChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventNotifierService.class);

    private final Set<StreamObserver<EventNotification>> responses;

    public EventNotifierService() {
        this.responses = new HashSet<>();
        DataUpdateHolder.getInstance().addPropertyChangeListener(this);
    }

    @Override
    public void rowInserted(SubscribingRequest request, StreamObserver<EventNotification> responseObserver) {
        LOGGER.info("New event notifier client!");
        responses.add(responseObserver);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        EventNotification data = EventNotification.newBuilder().build();
        responses.stream().filter(Objects::nonNull).forEach(response -> response.onNext(data));
    }
}

