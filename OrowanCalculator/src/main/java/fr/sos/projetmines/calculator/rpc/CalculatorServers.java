package fr.sos.projetmines.calculator.rpc;

import fr.sos.projetmines.commonutils.rpc.OrowanRPCServer;

import java.io.IOException;

public class CalculatorServers {

    private final OrowanRPCServer[] servers;

    public CalculatorServers(int port) {
        servers = new OrowanRPCServer[2];
        servers[0] = new OrowanRPCServer(port, new OrowanAuthenticatorService());
        servers[1] = new OrowanRPCServer(port + 1, new OrowanLiveDataService());
    }

    public void startServers() throws IOException {
        for (OrowanRPCServer server : servers) {
            server.startServer();
        }
    }

    /**
     * Stop serving requests and shutdown resources.
     */
    public void stopServers() throws InterruptedException {
        for (OrowanRPCServer server : servers) {
            server.stopServer();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    public void blockUntilShutdown() throws InterruptedException {
        for (OrowanRPCServer server : servers) {
            server.blockUntilShutdown();
        }
    }
}
