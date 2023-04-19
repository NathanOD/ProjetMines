package fr.sos.projetmines.gui.controller;

import fr.sos.projetmines.CurvePoint;
import fr.sos.projetmines.gui.util.LiveDataTask;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Rectangle;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    private final LiveDataTask service;
    private final Node shape = new Rectangle(0,0,0,0);
    private final StringConverter<Number> xAxisConverter;

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
        //service.setOnFailed(event -> service.restart());
        service.start();

        xAxisConverter = new StringConverter<>() {
            @Override
            public String toString(Number value) {
                return String.format("%.1f s", value.floatValue());
            }

            @Override
            public Float fromString(String string) {
                return null;
            }
        };
    }

    public void closeScene() {
        service.cancel();
    }

    public void addPointToPlot(CurvePoint curvePoint) {
        float realTime = curvePoint.getXTime();
        if (frictionChart.getData().size() == 0) {
            Platform.runLater(() -> {
                configureXAxis(xAxisFriction, realTime);
                yAxisFriction.setForceZeroInRange(false);

                configureXAxis(xAxisRollSpeed, realTime);
                yAxisRollSpeed.setForceZeroInRange(false);

                configureXAxis(xAxisSigma, realTime);
                yAxisSigma.setForceZeroInRange(false);

                frictionChart.getData().add(new XYChart.Series<>());
                rollSpeedChart.getData().add(new XYChart.Series<>());
                sigmaChart.getData().add(new XYChart.Series<>());
            });
        }

        Platform.runLater(() -> {
            // set the data for each LineChart
            addDataToChart(frictionChart, realTime, curvePoint.getFrictionCoefficient(), "orange");
            addDataToChart(rollSpeedChart, realTime, curvePoint.getRollSpeed(), "green");
            addDataToChart(sigmaChart, realTime, curvePoint.getSigma(), "blue");
        });
    }
    private void addDataToChart(LineChart<Float, Float> lineChart, float x, float y, String color){
        XYChart.Data<Float, Float> data = new XYChart.Data<>(x, y);
        data.setNode(shape);
        lineChart.getData().get(0).getNode().lookup(".chart-series-line").setStyle("-fx-stroke: " + color + ";");
        lineChart.getData().get(0).getData().add(data);
    }

    private void configureXAxis(NumberAxis axis, float lowerBound){
        axis.setLowerBound(lowerBound);
        axis.setForceZeroInRange(false);
        axis.setTickLabelFormatter(xAxisConverter);
    }

}
