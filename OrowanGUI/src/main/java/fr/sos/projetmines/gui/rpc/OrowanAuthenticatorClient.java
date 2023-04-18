package fr.sos.projetmines.gui.rpc;

import fr.sos.projetmines.OrowanAuthenticatorGrpc;
import fr.sos.projetmines.OrowanConnectionResult;
import fr.sos.projetmines.OrowanUserCredentials;
import io.grpc.Channel;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class OrowanAuthenticatorClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanAuthenticatorClient.class);

    private final OrowanAuthenticatorGrpc.OrowanAuthenticatorBlockingStub client; //synchronous
    private final Channel channel;

    public OrowanAuthenticatorClient(Channel channel) {
        this.channel = channel;
        client = OrowanAuthenticatorGrpc.newBlockingStub(channel);
    }

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
