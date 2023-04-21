package fr.sos.projetmines.commonutils.rpc;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OrowanRPCServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanRPCServer.class);

    private final Server server;
    private final String serverName;

    /**
     *Constructs an OrowanRPCServer instance with the specified port number, server name and gRPC service implementation.
     * @param port the port number to listen on.
     * @param serverName the name of the server.
     * @param service the gRPC service implementation.
     */
    public OrowanRPCServer(int port, String serverName, BindableService service) {
        this.serverName = serverName;
        server = ServerBuilder.forPort(port).addService(service).build();
    }

    /**
     * Starts the server on the parametrized port
     *
     * @throws IOException if the server cannot be started
     */
    public void startServer() throws IOException {
        if (server != null) {
            server.start();
            LOGGER.info("{} RPC Server successfully started on port {}.", serverName, server.getPort());
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
