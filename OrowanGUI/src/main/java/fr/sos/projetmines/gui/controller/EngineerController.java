package fr.sos.projetmines.gui.controller;


import fr.sos.projetmines.Job;
import fr.sos.projetmines.OperationResult;
import fr.sos.projetmines.Stand;
import fr.sos.projetmines.gui.util.*;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class EngineerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EngineerController.class);
    private final CreateUserTask creationTask;
    private final DeleteUserTask deletionTask;
    private final UpdateUserJobTask updateTask;
    private final ListStandsTask listStandsTask;
    private final ListUsersTask listUsersTask;
    private final UpdateStandStateTask updateStandStateTask;

    private Set<Stand> stands;
    @FXML
    private ChoiceBox<String> engineerUserManagementChoice, engineerAddUserJob;

    @FXML
    private ChoiceBox<Integer> engineerStandListChoice;
    @FXML
    private TextField engineerAddUserUsername, engineerAddUserPassword;

    @FXML
    private Button engineerEnableStand, engineerDisableStand;
    /**
     * Constructor for EngineerController.
     * Initializes tasks and sets up event handlers.
     */
    public EngineerController() {
        this.creationTask = new CreateUserTask();
        this.deletionTask = new DeleteUserTask();
        this.updateTask = new UpdateUserJobTask();
        this.updateStandStateTask = new UpdateStandStateTask();
        this.listStandsTask = new ListStandsTask();
        this.listUsersTask = new ListUsersTask();
        EventHandler<WorkerStateEvent> fail = event -> {
            LOGGER.info("Task {} failed.", event.getSource().getClass().getSimpleName());
        };

        creationTask.setOnCancelled(fail);
        creationTask.setOnFailed(fail);
        deletionTask.setOnCancelled(fail);
        deletionTask.setOnFailed(fail);
        updateTask.setOnCancelled(fail);
        updateTask.setOnFailed(fail);
        updateStandStateTask.setOnCancelled(fail);
        updateStandStateTask.setOnFailed(fail);
    }
    /**
     * Initializes the controller.
     * Sets up the event handlers for the choice boxes and buttons.
     */
    @FXML
    private void initialize() {
        engineerAddUserJob.getItems().add(Job.PROCESS_ENGINEER.name());
        engineerAddUserJob.getItems().add(Job.WORKER.name());
        creationTask.setOnSucceeded(event -> {
            engineerUserManagementChoice.getItems().add(creationTask.getUsername());
            engineerUserManagementChoice.setValue(creationTask.getUsername());
        });
        deletionTask.setOnSucceeded(event -> {
            engineerUserManagementChoice.getItems().remove(deletionTask.getUsername());
            if (engineerUserManagementChoice.getItems().size() > 0)
                engineerUserManagementChoice.setValue(engineerUserManagementChoice.getItems().get(0));
        });
        listUsersTask.setOnSucceeded(event -> Platform.runLater(() -> {
            engineerUserManagementChoice.getItems().clear();
            engineerUserManagementChoice.getItems().addAll(listUsersTask.getValue());
            if (engineerUserManagementChoice.getItems().size() > 0)
                engineerUserManagementChoice.setValue(engineerUserManagementChoice.getItems().get(0));
        }));
        listStandsTask.setOnSucceeded(event -> Platform.runLater(() -> {
            engineerStandListChoice.getItems().clear();
            stands = listStandsTask.getValue();
            engineerStandListChoice.getItems().addAll(stands.stream().map(Stand::getStandId).collect(Collectors.toSet()));
            if (engineerStandListChoice.getItems().size() > 0)
                engineerStandListChoice.setValue(engineerStandListChoice.getItems().get(0));
        }));
        updateStandStateTask.setOnSucceeded(event -> {
            OperationResult result = updateStandStateTask.getValue();
            if (result == OperationResult.SUCCESSFUL) {
                Iterator<Stand> standsIterator = stands.iterator();
                while (standsIterator.hasNext()) {
                    Stand stand = standsIterator.next();
                    if (stand.getStandId() == updateStandStateTask.getStandId()) {
                        standsIterator.remove();
                        break;
                    }
                }
                Stand stand = Stand.newBuilder()
                        .setStandId(updateStandStateTask.getStandId())
                        .setEnabled(updateStandStateTask.isEnabling())
                        .build();
                stands.add(stand);
                Platform.runLater(() -> {
                    engineerEnableStand.setDisable(stand.getEnabled());
                    engineerDisableStand.setDisable(!stand.getEnabled());
                });
            } else {
                if (updateStandStateTask.getStandId() == engineerStandListChoice.getValue()) {
                    Platform.runLater(() -> {
                        engineerEnableStand.setDisable(!engineerEnableStand.isDisable());
                        engineerDisableStand.setDisable(!engineerDisableStand.isDisable());
                    });
                }
            }
        });
        engineerStandListChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Optional<Stand> standOpt = stands.stream().filter(stand -> stand.getStandId() == newValue).findFirst();
                if (standOpt.isPresent()) {
                    Stand stand = standOpt.get();
                    engineerEnableStand.setDisable(stand.getEnabled());
                    engineerDisableStand.setDisable(!stand.getEnabled());
                } else {
                    engineerEnableStand.setDisable(true);
                    engineerDisableStand.setDisable(true);
                }
            }
        });

        listStandsTask.start();
        listUsersTask.start();
    }


    /**
     * Cancels all running tasks and closes the scene.
     */
    private void closeScene() {
        creationTask.cancel();
        deletionTask.cancel();
        updateTask.cancel();
        listUsersTask.cancel();
    }

    /**
     * Deletes the currently selected user.
     */
    @FXML
    private void deleteUser() {
        String username = engineerUserManagementChoice.getValue();
        if (username != null) {
            deletionTask.setUsername(username);
            deletionTask.restart();
        } else {
            LOGGER.warn("No username selected.");
        }
    }
    /**
     * Creates a new user with the specified username, password, and job.
     */
    @FXML
    private void createUser() {
        String username = engineerAddUserUsername.getText();
        String password = engineerAddUserPassword.getText();
        Job job = Job.valueOf(engineerAddUserJob.getValue());

        creationTask.setUsername(username);
        creationTask.setPassword(password);
        creationTask.setJob(job);
        creationTask.restart();
    }
    /**
     * Updates the job of the currently selected user.
     */
    @FXML
    private void promoteUser() {
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
    private void demoteUser() {
        String username = engineerUserManagementChoice.getValue();
        if (username != null) {
            updateTask.setUsername(username);
            updateTask.setJob(Job.WORKER);
            updateTask.restart();
        } else {
            LOGGER.warn("No username selected.");
        }
    }

    @FXML
    private void refresh() {
        listStandsTask.restart();
        listUsersTask.restart();
    }

    @FXML
    private void switchView() {
        closeScene();
        OrowanController.getInstance().showChoiceScene();
    }

    @FXML
    private void close() {
        closeScene();
        Platform.exit();
    }

    @FXML
    private void disconnect() {
        closeScene();
        OrowanController.getInstance().showLoginScene();
    }
    /**
     * Enables the currently selected stand.
     */
    @FXML
    private void enableStand() {
        updateStandStateTask.setEnabling(true);
        updateStandStateTask.setStandId(engineerStandListChoice.getValue());
        updateStandStateTask.restart();
    }
    /**
     * Disables the currently selected stand.
     */
    @FXML
    private void disableStand() {
        updateStandStateTask.setEnabling(false);
        updateStandStateTask.setStandId(engineerStandListChoice.getValue());
        updateStandStateTask.restart();
    }
}
