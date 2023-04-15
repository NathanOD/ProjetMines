package fr.sos.projetmines.calculator.rpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CalculatorServers {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalculatorServers.class);

    private final int port;
    private final Server[] servers;

    public CalculatorServers(int port) throws IOException {
        this.port = port;
        servers = new Server[4];
        servers[0] = ServerBuilder.forPort(port).addService(new OrowanAuthenticatorService()).build();
        servers[1] = ServerBuilder.forPort(port+1).addService(new OrowanLivedataService()).build();
    }

    public void startServers() throws IOException {
        for (Server server : servers) {
            if (server != null) {
                server.start();
                LOGGER.info("Server on port {} successfully started", server.getPort());
            }
        }
    }


    /**
     * Stop serving requests and shutdown resources.
     */
    public void stopServers() throws InterruptedException {
        for (Server server : servers) {
            if (server != null) {
                server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
                LOGGER.info("Server on port {} is shutting down", port);
            }
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    public void blockUntilShutdown() throws InterruptedException {
        for (Server server : servers) {
            if (server != null) {
                server.awaitTermination();
            }
        }
    }
}
