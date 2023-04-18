package fr.sos.projetmines.gui.controller;


import fr.sos.projetmines.gui.rpc.OrowanLiveDataClient;
import fr.sos.projetmines.gui.rpc.UserManagementClient;
import fr.sos.projetmines.gui.util.CreateUserTask;
import fr.sos.projetmines.gui.util.DeleteUserTask;
import fr.sos.projetmines.gui.util.UpdateUserJobTask;
import fr.sos.projetmines.gui.util.UserManagementTask;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    private final UserManagementTask creationTask, deletionTask, updateTask;



    public EngineerController(){
        ManagedChannel channel = Grpc.newChannelBuilderForAddress(OrowanController.getInstance().getHost(),
                OrowanController.getInstance().getPort() + 3, InsecureChannelCredentials.create()).build();
        UserManagementClient client = new UserManagementClient(channel);
        this.creationTask = new CreateUserTask(client);
        this.deletionTask = new DeleteUserTask(client);
        this.updateTask = new UpdateUserJobTask(client);
    }

    public void closeScene() {
        creationTask.cancel();
        deletionTask.cancel();
        updateTask.cancel();
    }
}
