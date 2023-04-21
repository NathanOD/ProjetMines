package fr.sos.projetmines.gui.util;

import fr.sos.projetmines.OperationResult;
import fr.sos.projetmines.OrowanOperationResult;
import fr.sos.projetmines.gui.controller.OrowanController;
import fr.sos.projetmines.gui.rpc.StandManagementClient;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class UpdateStandStateTask extends Service<OperationResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateStandStateTask.class);


    private final IntegerProperty standId = new SimpleIntegerProperty();
    /**
     * Gets the ID of the stand to update.
     *
     * @return the ID of the stand to update.
     */
    public int getStandId() {
        return standId.getValue();
    }
    /**
     * Sets the ID of the stand to update.
     *
     * @param newStandId the new ID of the stand to update.
     */
    public void setStandId(int newStandId) {
        this.standId.set(newStandId);
    }
    /**
     * Gets the integer property for the stand ID.
     *
     * @return the integer property for the stand ID.
     */
    public IntegerProperty standId() {
        return standId;
    }


    private final BooleanProperty enabling = new SimpleBooleanProperty();
    /**
     * Determines if the stand will be enabled or disabled.
     *
     * @return true if the stand will be enabled, false if it will be disabled.
     */
    public boolean isEnabling(){ return enabling.get(); }
    /**
     * Sets the value indicating if the stand will be enabled or disabled.
     *
     * @param newEnablingValue true if the stand will be enabled, false if it will be disabled.
     */
    public void setEnabling(boolean newEnablingValue){ this.enabling.set(newEnablingValue);}
    /**
     * Gets the boolean property for the enabling value.
     *
     * @return the boolean property for the enabling value.
     */
    public BooleanProperty enabling(){ return enabling; }

    /**
     * Creates a task to update the state (enabling or disabling) of a stand using gRPC.
     *
     * @return the task to update the state (enabling or disabling) of a stand using gRPC.
     */
    @Override
    protected Task<OperationResult> createTask() {
        return new Task<>() {
            @Override
            protected OperationResult call() throws Exception {
                ManagedChannel channel = Grpc.newChannelBuilderForAddress(OrowanController.getInstance().getHost(),
                        OrowanController.getInstance().getPort() + 2, InsecureChannelCredentials.create()).build();
                try {
                    StandManagementClient client = new StandManagementClient(channel);
                    Optional<OrowanOperationResult> resultOpt = isEnabling() ? client.enableStand(getStandId()) :
                            client.disableStand(getStandId());
                    if (resultOpt.isPresent()) {
                        LOGGER.info("Update stand {} to {} : {}", getStandId(), isEnabling(), resultOpt.get().getResult().name());
                        return resultOpt.get().getResult();
                    } else {
                        fireEvent(new WorkerStateEvent(this, WorkerStateEvent.WORKER_STATE_FAILED));
                        return OperationResult.UNRECOGNIZED;
                    }
                } finally {
                    try {
                        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        LOGGER.error(e.getMessage());
                    }
                }
            }
        };
    }
}
