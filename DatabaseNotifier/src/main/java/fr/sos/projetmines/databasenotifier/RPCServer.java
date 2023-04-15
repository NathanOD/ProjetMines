package fr.sos.projetmines.databasenotifier;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RPCServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RPCServer.class);

    private final int port;
    private final Server server;


    public RPCServer(int port) throws IOException {
        this.port = port;
        server = ServerBuilder.forPort(port).addService(new EventNotifierService()).build();
    }

    public void startServer() throws IOException {
        if (server != null) {
            server.start();
            LOGGER.info("Database Event notifier RPC Server successfully started on port {}.", server.getPort());
        }
    }

    /**
     * Stop serving requests and shutdown resources.
     */
    public void stopServer() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
            LOGGER.info("Database Event notifier RPC Server is shutting down");
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
