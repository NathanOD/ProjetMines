package fr.sos.projetmines.calculator.rpc;

import fr.sos.projetmines.commonutils.rpc.OrowanRPCServer;

import java.io.IOException;

/**
 * CalculatorServers is responsible for starting and stopping OrowanRPCServer instances for various services.
 * It initializes and starts five OrowanRPCServer instances for the following services:
 * - Authenticator
 * - LiveData
 * - StandManagement
 * - UserManagement
 * - InputRangeEditor
 */

public class CalculatorServers {

    private final OrowanRPCServer[] servers;

    public CalculatorServers(int port) {
        servers = new OrowanRPCServer[5];
        servers[0] = new OrowanRPCServer(port, "Authenticator", new OrowanAuthenticatorService());
        servers[1] = new OrowanRPCServer(port + 1, "LiveData", new OrowanLiveDataService());
        servers[2] = new OrowanRPCServer(port + 2, "StandManagement", new StandManagementService());
        servers[3] = new OrowanRPCServer(port + 3, "UserManagement", new UserManagementService());
        servers[4] = new OrowanRPCServer(port + 4, "InputRangeEditor", new InputRangeEditorService());
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
