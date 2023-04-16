package fr.sos.projetmines.calculator.rpc;

import fr.sos.projetmines.EventNotification;
import fr.sos.projetmines.EventNotifierGrpc;
import fr.sos.projetmines.SubscribingRequest;
import fr.sos.projetmines.calculator.OrowanCalculator;
import fr.sos.projetmines.calculator.model.OrowanDataOutput;
import fr.sos.projetmines.calculator.model.OrowanSensorData;
import fr.sos.projetmines.calculator.util.DataFormatter;
import fr.sos.projetmines.commonutils.config.Configuration;
import fr.sos.projetmines.commonutils.event.OEventBroadcaster;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeSupport;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DatabaseNotifierClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseNotifierClient.class);

    private final EventNotifierGrpc.EventNotifierStub client; //asynchronous

    public DatabaseNotifierClient() {
        Configuration config = OrowanCalculator.getInstance().getConfig();
        ManagedChannel channel = Grpc.newChannelBuilderForAddress(config.getStringValue("rpc-db-url"),
                config.getIntValue("rpc-db-port"), InsecureChannelCredentials.create()).build();
        client = EventNotifierGrpc.newStub(channel);
    }

    public void startListeningForUpdates() {
        SubscribingRequest subscribingRequest = SubscribingRequest.newBuilder().build();
        OrowanCalculator calculator = OrowanCalculator.getInstance();
        try {
            client.rowInserted(subscribingRequest, new StreamObserver<>() {
                @Override
                public void onNext(EventNotification value) {
                    DataFormatter dFormatter = new DataFormatter();
                    Optional<OrowanSensorData> sensorData = dFormatter.sensorDataToFile(value.getEntryId());
                    if(sensorData.isEmpty()){
                        return;
                    }

                    Path orowanPath = Path.of(calculator.getConfig().getStringValue("orowan-exe"));
                    dFormatter.runOrowan(orowanPath);

                    Path outputPath = Path.of(System.getProperty("user.dir"), "output.txt");
                    Optional<OrowanDataOutput> output = dFormatter.saveOrowanOutputToDatabase(outputPath);
                    if(output.isPresent()){
                        Map<String, Object> data = new HashMap<>();
                        data.put("standId", sensorData.get().getStandId());
                        data.put("output", output.get());
                        OrowanCalculator.getInstance().getDataInputBroadcaster().broadcast(data);
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
