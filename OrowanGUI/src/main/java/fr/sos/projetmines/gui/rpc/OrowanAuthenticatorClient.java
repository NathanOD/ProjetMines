package fr.sos.projetmines.gui.rpc;

import fr.sos.projetmines.OrowanAuthenticatorGrpc;
import fr.sos.projetmines.OrowanConnectionResult;
import fr.sos.projetmines.OrowanUserCredentials;
import fr.sos.projetmines.gui.controller.OrowanController;
import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class OrowanAuthenticatorClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanAuthenticatorClient.class);
    /**

     The gRPC blocking stub used for making synchronous calls to the Orowan authentication service.
     */
    private final OrowanAuthenticatorGrpc.OrowanAuthenticatorBlockingStub client; //synchronous
    /**

     Constructs an OrowanAuthenticatorClient with the provided gRPC channel.
     @param channel the gRPC channel to use for communication with the Orowan authentication service
     */
    public OrowanAuthenticatorClient(Channel channel) {
        client = OrowanAuthenticatorGrpc.newBlockingStub(channel);
    }
    /**

     Sends a connection request to the Orowan authentication service with the provided credentials.
     @param username the username to authenticate with
     @param password the password to authenticate with
     @return an Optional containing the OrowanConnectionResult if authentication was successful, otherwise empty
     */
    public Optional<OrowanConnectionResult> sendConnectionRequest(String username, String password) {
        OrowanUserCredentials creds = OrowanUserCredentials.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();
        OrowanConnectionResult result = null;
        try {
            result = client.authenticateUser(creds);
        } catch (StatusRuntimeException e) {
            LOGGER.warn("RPC Failed: {}", e.getLocalizedMessage());
        }
        return Optional.ofNullable(result);
    }
}
