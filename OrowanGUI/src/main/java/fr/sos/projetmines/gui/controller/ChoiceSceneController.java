package fr.sos.projetmines.gui.controller;

import fr.sos.projetmines.Job;
import fr.sos.projetmines.Stand;
import fr.sos.projetmines.gui.util.ListStandsTask;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

import java.util.Set;
import java.util.stream.Collectors;

public class ChoiceSceneController {

    private final ListStandsTask listStandsTask;
    /**

     The ChoiceBox component that displays available Stand IDs.
     */
    @FXML
    private ChoiceBox<Integer> choiceStandIdBox;
    /**

     The Button component that leads to the Worker scene.
     */
    @FXML
    private Button choiceEngineerButton;

    public ChoiceSceneController() {
        listStandsTask = new ListStandsTask();
    }
    /**

     Initializes the controller after its root element has been completely processed.
     Starts the listStandsTask and sets the event handler for its success event.
     If the user is a PROCESS_ENGINEER, the choiceEngineerButton is made visible.
     */
    @FXML
    protected void initialize() {
        listStandsTask.setOnSucceeded(event -> {
            choiceStandIdBox.getItems().clear();
            Set<Stand> stands = listStandsTask.getValue();
            Set<Integer> enabled = stands.stream()
                    .filter(Stand::getEnabled)
                    .map(Stand::getStandId)
                    .collect(Collectors.toSet());
            choiceStandIdBox.getItems().addAll(enabled);
            if (enabled.size() > 0) {
                choiceStandIdBox.setValue(choiceStandIdBox.getItems().get(0));
            }
        });
        listStandsTask.start();
        if (OrowanController.getInstance().getUserJob() == Job.PROCESS_ENGINEER) {
            choiceEngineerButton.setVisible(true);
        }
    }
    /**

     Closes the application.
     */
    @FXML
    private void close() {
        Platform.exit();
    }
    /**

     Shows the Worker scene with the selected Stand ID.
     */
    @FXML
    private void showWorkerScene() {
        OrowanController.getInstance().setStandId(choiceStandIdBox.getValue());
        OrowanController.getInstance().showWorkerScene();
    }
    /**

     Shows the Process Engineer scene.
     */
    @FXML
    private void showEngineerScene() {
        OrowanController.getInstance().showProcessEngineerScene();
    }
    /**

     Restarts the listStandsTask to refresh the available Stand IDs.
     */
    @FXML
    private void refresh() {
        listStandsTask.restart();
    }

    /**

     Cancels the listStandsTask and shows the Login scene.
     */
    @FXML
    private void disconnect() {
        listStandsTask.cancel();
        OrowanController.getInstance().showLoginScene();
    }
}
