package fr.sos.projetmines.gui.rpc;

import fr.sos.projetmines.*;
import fr.sos.projetmines.gui.controller.OrowanController;
import fr.sos.projetmines.gui.util.UserManagementTask;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public class UserManagementClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserManagementClient.class);

    private UserManagementGrpc.UserManagementBlockingStub client; //synchronous
    /**
     * Constructs a new UserManagementClient with the given channel.
     *
     * @param channel the channel to use for communication with the user management service
     */
    public UserManagementClient(Channel channel){
        this.client = UserManagementGrpc.newBlockingStub(channel);
    }

    /**
     * Creates a new user with the given credentials.
     *
     * @param username the username of the new user
     * @param password the password of the new user
     * @param job the job of the new user
     * @return an Optional containing an OrowanOperationResult if the user was successfully created, or empty if creation failed
     */
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

    /**
     * Deletes the user with the given username.
     *
     * @param username the username of the user to delete
     * @return an Optional containing an OrowanOperationResult if the user was successfully deleted, or empty if deletion failed
     */
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

    /**
     * Updates the job of the user with the given username.
     *
     * @param username the username of the user to update
     * @param newJob the new job for the user
     * @return an Optional containing an OrowanOperationResult if the user job was successfully updated, or empty if update failed
     */
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
    /**
     * Lists all users in the system.
     *
     * @return a Set of strings containing the usernames of all users in the system
     */
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
}
