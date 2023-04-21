package fr.sos.projetmines.gui.controller;

import fr.sos.projetmines.CurvePoint;
import fr.sos.projetmines.FrictionCoefficient;
import fr.sos.projetmines.Job;
import fr.sos.projetmines.gui.util.LiveDataTask;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.shape.Rectangle;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

public class WorkerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    /**
     * The shape to be used for displaying curve points
     */
    private static final Node POINT_SHAPE = new Rectangle(0, 0, 0, 0);
    /**
     * A StringConverter object for formatting the x-axis values of the charts
     */
    private final StringConverter<Number> xAxisConverter;

    /**
     * The task responsible for retrieving and displaying live data
     */
    private final LiveDataTask liveDataTask;
    /**
     * The ResourceBundle containing localized strings for the UI
     */
    @FXML
    private ResourceBundle resources;
    /**
     * The LineCharts used to display the data
     */
    @FXML
    private LineChart<Float, Float> frictionChart, rollSpeedChart, sigmaChart;
    /**
     * The x-axes for the charts
     */
    @FXML
    private NumberAxis xAxisFriction, yAxisFriction, xAxisRollSpeed, yAxisRollSpeed, xAxisSigma, yAxisSigma;
    /**
     * The labels displaying computed values
     */

    @FXML
    private Label computationTimeLabel, frictionFactorLabel, averageFrictionFactorLabel, sigmaLabel, rollSpeedLabel;
    /**
     * The button used to switch views in the Orowan controller
     */
    @FXML
    private MenuItem switchViewButton;
    /**
     * A flag indicating whether the charts have been set up
     */
    private boolean chartsSetup;

    /**
     * Constructs a new WorkerController object
     */
    public WorkerController() {
        liveDataTask = new LiveDataTask();
        liveDataTask.setController(this);
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

    /**
     * Initializes the WorkerController. Sets the labels to their default values, starts the live data task, and shows the switch
     * <p>
     * view button if the user is a process engineer.
     */
    @FXML
    public void initialize() {
        computationTimeLabel.setText(String.format(resources.getString("ui.worker.computationtime"), (long) 0));
        frictionFactorLabel.setText(String.format(resources.getString("ui.worker.frictionfactor"), 0.0));
        averageFrictionFactorLabel.setText(String.format(resources.getString("ui.worker.frictionfactor.average"), 0.0));
        rollSpeedLabel.setText(String.format(resources.getString("ui.worker.rollspeed"), 0.0));
        sigmaLabel.setText(String.format(resources.getString("ui.worker.sigma"), 0.0));

        liveDataTask.start();

        if (OrowanController.getInstance().getUserJob() == Job.PROCESS_ENGINEER) {
            switchViewButton.setVisible(true);
        }
    }

    /**
     * Cancels the live data task when the scene is closed.
     */
    public void closeScene() {
        liveDataTask.cancel();
    }

    /**
     * Sets up the charts with the given lower bound if they haven't been set up already.
     *
     * @param lowerBound the lower bound for the x-axes of the charts
     */

    private void setupCharts(float lowerBound) {
        Platform.runLater(() -> {
            configureXAxis(xAxisFriction, lowerBound);
            yAxisFriction.setForceZeroInRange(false);

            configureXAxis(xAxisRollSpeed, lowerBound);
            yAxisRollSpeed.setForceZeroInRange(false);

            configureXAxis(xAxisSigma, lowerBound);
            yAxisSigma.setForceZeroInRange(false);

            XYChart.Series<Float, Float> frictionSerie = new XYChart.Series<>();
            XYChart.Series<Float, Float> averageFrictionSerie = new XYChart.Series<>();
            frictionChart.getData().add(frictionSerie);
            frictionChart.getData().add(averageFrictionSerie);

            XYChart.Series<Float, Float> rollSpeedSerie = new XYChart.Series<>();
            rollSpeedChart.getData().add(rollSpeedSerie);

            XYChart.Series<Float, Float> sigmaSerie = new XYChart.Series<>();
            sigmaChart.getData().add(sigmaSerie);

            frictionSerie.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: red;");
            averageFrictionSerie.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: royalblue; -fx-stroke-dash-array: 1 6 1; -fx-stroke-width: 3;");
            rollSpeedSerie.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: green;");
            sigmaSerie.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: blue;");

            chartsSetup = true;
        });
    }

    private void configureXAxis(NumberAxis axis, float lowerBound) {
        axis.setLowerBound(lowerBound);
        axis.setForceZeroInRange(false);
        axis.setTickLabelFormatter(xAxisConverter);
    }

    public void plotNormalPoint(CurvePoint curvePoint) {
        if (!chartsSetup) {
            setupCharts(curvePoint.getXTime());
        }

        float realTime = curvePoint.getXTime();
        Platform.runLater(() -> {
            // set the data for each LineChart
            addDataToSerie(frictionChart, realTime, curvePoint.getFrictionCoefficient());
            addDataToSerie(rollSpeedChart, realTime, curvePoint.getRollSpeed());
            addDataToSerie(sigmaChart, realTime, curvePoint.getSigma());

            computationTimeLabel.setText(String.format(resources.getString("ui.worker.computationtime"),
                    curvePoint.getComputationTime()));
            frictionFactorLabel.setText(String.format(resources.getString("ui.worker.frictionfactor"),
                    curvePoint.getFrictionCoefficient()));
            rollSpeedLabel.setText(String.format(resources.getString("ui.worker.rollspeed"), curvePoint.getRollSpeed()));
            sigmaLabel.setText(String.format(resources.getString("ui.worker.sigma"), curvePoint.getSigma()));
        });
    }

    public void plotAveragePoint(FrictionCoefficient frictionCoefficient) {
        if (!chartsSetup) {
            setupCharts(frictionCoefficient.getXTime());
        }
        Platform.runLater(() -> {
            addAverageFrictionPoint(frictionCoefficient.getXTime(), frictionCoefficient.getFrictionCoefficient());

            averageFrictionFactorLabel.setText(String.format(resources.getString("ui.worker.frictionfactor.average"),
                    frictionCoefficient.getFrictionCoefficient()));
        });
    }

    /**
     * @param lineChart LineChart to plot
     * @param x         x value of the data
     * @param y         y value of the data
     */
    private void addDataToSerie(LineChart<Float, Float> lineChart, float x, float y) {
        XYChart.Data<Float, Float> data = new XYChart.Data<>(x, y);
        data.setNode(POINT_SHAPE);
        lineChart.getData().get(0).getData().add(data);
    }

    /**
     * @param x x value of the data
     * @param y y value of the data
     */
    private void addAverageFrictionPoint(float x, float y) {
        XYChart.Data<Float, Float> data = new XYChart.Data<>(x, y);
        data.setNode(POINT_SHAPE);
        frictionChart.getData().get(1).getData().add(data);
    }

    @FXML
    private void switchView() {
        closeScene();
        OrowanController.getInstance().showProcessEngineerScene();
    }

    @FXML
    private void disconnect() {
        closeScene();
        OrowanController.getInstance().showLoginScene();
    }

    @FXML
    private void close() {
        closeScene();
        Platform.exit();
    }
}
