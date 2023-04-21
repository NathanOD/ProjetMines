package fr.sos.projetmines.gui.rpc;

import fr.sos.projetmines.*;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public class StandManagementClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandManagementClient.class);
    private StandManagementGrpc.StandManagementBlockingStub client; //synchronous
    /**

     Constructs a new StandManagementClient given a gRPC channel.
     @param channel the gRPC channel
     */
    public StandManagementClient(Channel channel) {
        client = StandManagementGrpc.newBlockingStub(channel);
    }
    /**

     Lists all the stands currently registered in the Stand Management service.
     @return a set of all stands
     */
    public Set<Stand> listStands() {
        StandListRequest request = StandListRequest.newBuilder().build();
        Set<Stand> stands = new HashSet<>();
        try {
            Iterator<Stand> standIterator = client.listStands(request);
            while (standIterator.hasNext()) {
                stands.add(standIterator.next());
            }
            return stands;
        } catch (StatusRuntimeException e) {
            LOGGER.warn("RPC Failed: {}", e.getStatus());
        }
        return stands;
    }
    /**

     Enables the specified stand identified by its ID.
     @param standId the ID of the stand to enable
     @return an optional OrowanOperationResult indicating the result of the operation
     */
    public Optional<OrowanOperationResult> enableStand(int standId) {
        StandEnablingRequest request = StandEnablingRequest.newBuilder().setStandId(standId).build();
        try {
            return Optional.of(client.enableStand(request));
        } catch (StatusRuntimeException e) {
            LOGGER.warn("RPC Failed: {}", e.getStatus());
        }
        return Optional.empty();
    }
    /**

     Disables the specified stand identified by its ID.
     @param standId the ID of the stand to disable
     @return an optional OrowanOperationResult indicating the result of the operation
     */
    public Optional<OrowanOperationResult> disableStand(int standId) {
        StandDisablingRequest request = StandDisablingRequest.newBuilder().setStandId(standId).build();
        try {
            return Optional.of(client.disableStand(request));
        } catch (StatusRuntimeException e) {
            LOGGER.warn("RPC Failed: {}", e.getStatus());
        }
        return Optional.empty();
    }
}
