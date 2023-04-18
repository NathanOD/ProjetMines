package fr.sos.projetmines.gui.rpc;

import fr.sos.projetmines.*;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UserManagementClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserManagementClient.class);

    private final UserManagementGrpc.UserManagementBlockingStub client; //synchronous
    private final ManagedChannel channel;

    public UserManagementClient(ManagedChannel channel) {
        this.channel = channel;
        client = UserManagementGrpc.newBlockingStub(channel);
    }

    public Optional<UserOperationResult> createUser(String username, String password, Job job) {
        UserCreationRequest request = UserCreationRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .setJob(job)
                .build();
        UserOperationResult result = null;
        try {
            result = client.createUser(request);
        } catch (StatusRuntimeException e) {
            LOGGER.warn("RPC Failed: {}", e.getStatus());
        }
        return Optional.ofNullable(result);
    }

    public Optional<UserOperationResult> deleteUser(String username) {
        UserDeletionRequest request = UserDeletionRequest.newBuilder()
                .setUsername(username)
                .build();
        UserOperationResult result = null;
        try {
            result = client.deleteUser(request);
        } catch (StatusRuntimeException e) {
            LOGGER.warn("RPC Failed: {}", e.getStatus());
        }
        return Optional.ofNullable(result);
    }


    public Optional<UserOperationResult> updateUserJob(String username, Job newJob) {
        UserJobUpdateRequest request = UserJobUpdateRequest.newBuilder()
                .setUsername(username)
                .setJob(newJob)
                .build();
        UserOperationResult result = null;
        try {
            result = client.setUserJob(request);
        } catch (StatusRuntimeException e) {
            LOGGER.warn("RPC Failed: {}", e.getStatus());
        }
        return Optional.ofNullable(result);
    }

    public ManagedChannel getChannel() {
        return channel;
    }
}
