package fr.sos.projetmines.gui.controller;

import fr.sos.projetmines.CurvePoint;
import fr.sos.projetmines.gui.util.LiveDataTask;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    private final LiveDataTask service;

    @FXML
    private LineChart<Float, Float> frictionChart;
    @FXML
    private LineChart<Float, Float> rollSpeedChart;
    @FXML
    private LineChart<Float, Float> sigmaChart;

    @FXML
    private NumberAxis xAxisFriction, yAxisFriction;

    @FXML
    private NumberAxis xAxisRollSpeed, yAxisRollSpeed;

    @FXML
    private NumberAxis xAxisSigma, yAxisSigma;

    public WorkerController() {
        service = new LiveDataTask();
        service.setController(this);
        service.setHost(OrowanController.getInstance().getHost());
        service.setPort(OrowanController.getInstance().getPort() + 1);
        //service.setOnFailed(event -> service.restart());
        service.start();
    }

    public void closeScene() {
        service.cancel();
    }

    public void addPointToPlot(CurvePoint curvePoint) {
        float realTime = curvePoint.getXTime() / 1000f;
        LOGGER.debug("Real time: {}", realTime);
        if (frictionChart.getData().size() == 0) {
            Platform.runLater(() -> {
                xAxisFriction.setLowerBound(realTime);
                xAxisFriction.setForceZeroInRange(false);
                yAxisFriction.setForceZeroInRange(false);
                xAxisRollSpeed.setLowerBound(realTime);
                xAxisRollSpeed.setForceZeroInRange(false);
                yAxisRollSpeed.setForceZeroInRange(false);
                xAxisSigma.setLowerBound(realTime);
                xAxisSigma.setForceZeroInRange(false);
                yAxisSigma.setForceZeroInRange(false);

                frictionChart.getData().add(new XYChart.Series<>());
                rollSpeedChart.getData().add(new XYChart.Series<>());
                sigmaChart.getData().add(new XYChart.Series<>());
            });
        }

        Platform.runLater(() -> {
            // set the data for each LineChart
            XYChart.Series<Float, Float> frictionSerie = frictionChart.getData().get(0);
            frictionSerie.getData().add(new XYChart.Data<>(realTime, curvePoint.getFrictionCoefficient()));

            XYChart.Series<Float, Float> rollSpeedSerie = rollSpeedChart.getData().get(0);
            rollSpeedSerie.getData().add(new XYChart.Data<>(realTime, curvePoint.getRollSpeed()));

            XYChart.Series<Float, Float> sigmaSerie = sigmaChart.getData().get(0);
            sigmaSerie.getData().add(new XYChart.Data<>(realTime, curvePoint.getSigma()));
        });
    }

}
