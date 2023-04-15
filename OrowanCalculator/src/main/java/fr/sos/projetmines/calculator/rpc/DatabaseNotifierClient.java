package fr.sos.projetmines.calculator.rpc;

import fr.sos.projetmines.*;
import fr.sos.projetmines.calculator.util.DataFormatter;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class DatabaseNotifierClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseNotifierClient.class);

    private final EventNotifierGrpc.EventNotifierStub client; //asynchronous
    private final Channel channel;

    public DatabaseNotifierClient(Channel channel) {
        this.channel = channel;
        client = EventNotifierGrpc.newStub(channel);
    }

    public void startListeningForUpdates(Path orowanPath) {
        SubscribingRequest subscribingRequest = SubscribingRequest.newBuilder().build(); //TODO: Change stand id
        try {
            client.rowInserted(subscribingRequest, new StreamObserver<EventNotification>() {
                @Override
                public void onNext(EventNotification value) {
                    DataFormatter dFormatter = new DataFormatter();
                    dFormatter.formatToFile();
                    dFormatter.runOrowan(orowanPath);
                    Path outputPath = Path.of(System.getProperty("user.dir"), "output.txt");
                    try {
                        dFormatter.outputToDatabase(outputPath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onCompleted() {

                }
            });
        } catch (StatusRuntimeException e) {
            LOGGER.warn("RPC Failed: {}", e.getStatus());
        }
    }
}
