package fr.sos.projetmines.calculator;

import fr.sos.projetmines.calculator.model.OrowanDataOutput;
import fr.sos.projetmines.calculator.rpc.CalculatorServers;
import fr.sos.projetmines.calculator.rpc.DatabaseNotifierClient;
import fr.sos.projetmines.calculator.util.CalculatorDatabaseConnection;
import fr.sos.projetmines.calculator.util.CalculatorConfiguration;
import fr.sos.projetmines.calculator.util.DataFormatter;
import fr.sos.projetmines.calculator.util.OutputDataReceiver;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.netty.channel.ChannelOption;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;

public class OrowanCalculator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanCalculator.class);

    public static void main(String[] args) {
        try {

            Path configPath = Path.of(System.getProperty("user.dir"), "config.properties");
            Optional<Properties> configOpt = new CalculatorConfiguration(configPath).verifyConfig();
            if(configOpt.isEmpty()){
                LOGGER.error("Stopping Orowan calculator.");
                return;
            }

            Properties config = configOpt.get();
            CalculatorDatabaseConnection database = CalculatorDatabaseConnection.getInstance();
            database.setDatabaseAddress(config.getProperty("db-prefix") + config.getProperty("db-url")
                    + config.getProperty("db-name"));
            database.setUsername(config.getProperty("db-username"));
            database.setPassword(config.getProperty("db-password"));
            database.connect();

            if (!database.isConnected()) {
                LOGGER.error("Stopping Orowan calculator.");
                return;
            }

            CalculatorServers servers = new CalculatorServers(Integer.parseInt(config.getProperty("rpc-minimal-port")));
            ManagedChannel channel = Grpc.newChannelBuilderForAddress(config.getProperty("rpc-db-url"),
                    Integer.parseInt(config.getProperty("rpc-db-port")), InsecureChannelCredentials.create())
                    .build();
            DatabaseNotifierClient client = new DatabaseNotifierClient(channel);
            client.startListeningForUpdates(Path.of(config.getProperty("orowan-exe")));


            DataFormatter dFormatter = new DataFormatter();
            dFormatter.formatToFile();
            Path orowanPath = Path.of(config.getProperty("orowan-exe"));
            dFormatter.runOrowan(orowanPath);
            Path outputPath = Path.of(System.getProperty("user.dir"), "output.txt");
            try {
                dFormatter.outputToDatabase(outputPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            servers.startServers();
            servers.blockUntilShutdown();
        } catch (IOException | InterruptedException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private static void simulateData(){
        Random random = new Random();

        new Thread(() -> {
            while (true) {
                OrowanDataOutput output = new OrowanDataOutput(0, "VOID", 0, 0, 0,
                        0, 0, 0, 0, 0, 0, "YES");
                output.setComputationTime(204);
                output.setRollSpeed(0.54f);
                OutputDataReceiver.getInstance().receiveLevelTwoData(output, 3);
                try {
                    Thread.sleep((long) (195L + random.nextInt(10)));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
