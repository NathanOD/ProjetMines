package fr.sos.projetmines.calculator.rpc;

import fr.sos.projetmines.EventNotification;
import fr.sos.projetmines.EventNotifierGrpc;
import fr.sos.projetmines.SubscribingRequest;
import fr.sos.projetmines.calculator.OrowanCalculator;
import fr.sos.projetmines.calculator.model.OrowanDataOutput;
import fr.sos.projetmines.calculator.model.OrowanSensorData;
import fr.sos.projetmines.calculator.util.DataFormatter;
import fr.sos.projetmines.commonutils.config.Configuration;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DatabaseNotifierClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseNotifierClient.class);

    private final EventNotifierGrpc.EventNotifierStub client; //asynchronous
    private final int port;
    /**
     * Instantiates a new DatabaseNotifierClient.
     */
    public DatabaseNotifierClient() {
        Configuration config = OrowanCalculator.getInstance().getConfig();
        this.port = config.getIntValue("rpc-db-port");
        ManagedChannel channel = Grpc.newChannelBuilderForAddress(config.getStringValue("rpc-db-url"),
                port, InsecureChannelCredentials.create()).build();

        client = EventNotifierGrpc.newStub(channel);
    }
    /**
     * Starts listening for updates from the database notifier service.
     * When an update is received, it converts the sensor data to an input file, runs the Orowan executable,
     * saves the output to the database, and broadcasts the updated data to the data input broadcaster.
     */
    public void startListeningForUpdates() {
        SubscribingRequest subscribingRequest = SubscribingRequest.newBuilder().build();
        OrowanCalculator calculator = OrowanCalculator.getInstance();
        DataFormatter dFormatter = new DataFormatter();
        Path orowanPath = Path.of(calculator.getConfig().getStringValue("orowan-exe"));
        Path outputPath = Path.of(System.getProperty("user.dir"), "output.txt");
        Map<String, Object> data = new HashMap<>();

        try {
            LOGGER.debug("Connecting to database notifier on port {}...", port);
            client.rowInserted(subscribingRequest, new StreamObserver<>() {
                @Override
                public void onNext(EventNotification value) {
                    long start = System.currentTimeMillis();
                    Optional<OrowanSensorData> sensorDataOpt = dFormatter.sensorDataToFile(value.getEntryId());
                    if (sensorDataOpt.isEmpty()) {
                        LOGGER.warn("Sensor data could not be converted to input file!");
                        return;
                    }
                    OrowanSensorData sensorData = sensorDataOpt.get();
                    if (!sensorData.respectsConstraints()) {
                        LOGGER.warn("Sensor data does not respect the constraints.");
                        return;
                    }

                    dFormatter.runOrowan(orowanPath);

                    Optional<OrowanDataOutput> outputOpt = dFormatter.saveOrowanOutputToDatabase(outputPath);
                    long end = System.currentTimeMillis();

                    if (outputOpt.isPresent()) {
                        OrowanDataOutput output = outputOpt.get();
                        output.setRollSpeed(sensorData.getRollSpeed());
                        output.setXTime(sensorData.getXTime());
                        output.setComputationTime(end - start);
                        data.put("standId", sensorData.getStandId());
                        data.put("output", output);
                        OrowanCalculator.getInstance().getDataInputBroadcaster().broadcast(data);
                    } else {
                        LOGGER.warn("Orowan output data could not be saved into database!");
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
