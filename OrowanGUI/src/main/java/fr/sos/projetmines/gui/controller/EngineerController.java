package fr.sos.projetmines.gui.controller;


import fr.sos.projetmines.Job;
import fr.sos.projetmines.gui.rpc.UserManagementClient;
import fr.sos.projetmines.gui.util.CreateUserTask;
import fr.sos.projetmines.gui.util.DeleteUserTask;
import fr.sos.projetmines.gui.util.ListUsersTask;
import fr.sos.projetmines.gui.util.UpdateUserJobTask;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    private final CreateUserTask creationTask;
    private final DeleteUserTask deletionTask;
    private final UpdateUserJobTask updateTask;
    private final ListUsersTask listUsersTask;
    @FXML
    private ChoiceBox<String> engineerUserManagementChoice, engineerAddUserJob, engineerStandListChoiceBox;
    @FXML
    private TextField engineerAddUserUsername, engineerAddUserPassword;

    @FXML
    private Button engineerAddUser;


    public EngineerController() {
        ManagedChannel channel = Grpc.newChannelBuilderForAddress(OrowanController.getInstance().getHost(),
                OrowanController.getInstance().getPort() + 3, InsecureChannelCredentials.create()).build();
        UserManagementClient client = new UserManagementClient(channel);
        this.creationTask = new CreateUserTask();
        this.deletionTask = new DeleteUserTask();
        this.updateTask = new UpdateUserJobTask();
        this.listUsersTask = new ListUsersTask();
        EventHandler<WorkerStateEvent> fail = event -> {
            LOGGER.info("Task {} failed.", event.getSource().getClass().getSimpleName());
        };

        creationTask.setClient(client);
        creationTask.setOnCancelled(fail);
        creationTask.setOnFailed(fail);

        deletionTask.setClient(client);
        deletionTask.setOnCancelled(fail);
        deletionTask.setOnFailed(fail);

        updateTask.setClient(client);
        updateTask.setOnCancelled(fail);
        updateTask.setOnFailed(fail);

        listUsersTask.setClient(client);
    }

    @FXML
    protected void initialize() {
        engineerAddUserJob.getItems().add(Job.PROCESS_ENGINEER.name());
        engineerAddUserJob.getItems().add(Job.WORKER.name());
        creationTask.setOnSucceeded(event -> {
            LOGGER.info("User {} successfully created!", creationTask.getUsername());
            engineerUserManagementChoice.getItems().add(creationTask.getUsername());
        });
        deletionTask.setOnSucceeded(event -> {
            LOGGER.info("User {} successfully deleted!", deletionTask.getUsername());
            engineerUserManagementChoice.getItems().remove(deletionTask.getUsername());
        });
        listUsersTask.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                engineerUserManagementChoice.getItems().clear();
                engineerUserManagementChoice.getItems().addAll(listUsersTask.getValue());
            });
        });

        listUsersTask.start();
    }


    public void closeScene() {
        creationTask.cancel();
        deletionTask.cancel();
        updateTask.cancel();
        listUsersTask.cancel();
    }


    @FXML
    public void deleteUser() {
        String username = engineerUserManagementChoice.getValue();
        if (username != null) {
            deletionTask.setUsername(username);
            deletionTask.restart();
        } else {
            LOGGER.warn("No username selected.");
        }
    }

    @FXML
    public void createUser() {
        String username = engineerAddUserUsername.getText();
        String password = engineerAddUserPassword.getText();
        Job job = Job.valueOf(engineerAddUserJob.getValue());

        creationTask.setUsername(username);
        creationTask.setPassword(password);
        creationTask.setJob(job);
        creationTask.restart();
    }

    @FXML
    public void promoteUser() {
        String username = engineerUserManagementChoice.getValue();
        if (username != null) {
            updateTask.setUsername(username);
            updateTask.setJob(Job.PROCESS_ENGINEER);
            updateTask.restart();
        } else {
            LOGGER.warn("No username selected.");
        }
    }

    @FXML
    public void demoteUser() {
        String username = engineerUserManagementChoice.getValue();
        if (username != null) {
            updateTask.setUsername(username);
            updateTask.setJob(Job.WORKER);
            updateTask.restart();
        } else {
            LOGGER.warn("No username selected.");
        }
    }
}
