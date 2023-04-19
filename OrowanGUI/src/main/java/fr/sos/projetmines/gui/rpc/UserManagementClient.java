package fr.sos.projetmines.gui.rpc;

import fr.sos.projetmines.*;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public class UserManagementClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserManagementClient.class);

    private final UserManagementGrpc.UserManagementBlockingStub client; //synchronous
    private final ManagedChannel channel;

    public UserManagementClient(ManagedChannel channel) {
        this.channel = channel;
        client = UserManagementGrpc.newBlockingStub(channel);
    }

    public Optional<OrowanOperationResult> createUser(String username, String password, Job job) {
        UserCreationRequest request = UserCreationRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .setJob(job)
                .build();
        OrowanOperationResult result = null;
        try {
            result = client.createUser(request);
        } catch (StatusRuntimeException e) {
            LOGGER.warn("RPC Failed: {}", e.getStatus());
        }
        return Optional.ofNullable(result);
    }

    public Optional<OrowanOperationResult> deleteUser(String username) {
        UserDeletionRequest request = UserDeletionRequest.newBuilder()
                .setUsername(username)
                .build();
        OrowanOperationResult result = null;
        try {
            result = client.deleteUser(request);
        } catch (StatusRuntimeException e) {
            LOGGER.warn("RPC Failed: {}", e.getStatus());
        }
        return Optional.ofNullable(result);
    }


    public Optional<OrowanOperationResult> updateUserJob(String username, Job newJob) {
        UserJobUpdateRequest request = UserJobUpdateRequest.newBuilder()
                .setUsername(username)
                .setJob(newJob)
                .build();
        OrowanOperationResult result = null;
        try {
            result = client.setUserJob(request);
        } catch (StatusRuntimeException e) {
            LOGGER.warn("RPC Failed: {}", e.getStatus());
        }
        return Optional.ofNullable(result);
    }

    public Set<String> listUsers() {
        ListUsersRequest request = ListUsersRequest.newBuilder().build();
        Set<String> users = new HashSet<>();
        try {
            Iterator<OrowanUserCredentials> usersIt = client.listUsers(request);
            while(usersIt.hasNext()){
                users.add(usersIt.next().getUsername());
            }
            return users;
        } catch (StatusRuntimeException e) {
            LOGGER.warn("RPC Failed: {}", e.getStatus());
        }
        return users;
    }

    public ManagedChannel getChannel() {
        return channel;
    }
}
