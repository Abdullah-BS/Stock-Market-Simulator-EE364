import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.ToggleButton;
import javafx.util.Duration;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainAppGUI extends Application {

    private MainApp mainApp;  // Instance of MainApp for the simulation
    private TextArea eventsDisplay;  // Text area to display daily events
    private LineChart<Number, Number> lineChart;  // Line chart for trader net worth
    private Map<String, XYChart.Series<Number, Number>> traderSeriesMap;  // Map to hold chart series for each trader
    private int day = 0;  // Tracks the day for the chart
    private Timeline simulationTimeline;
    private double globalMinNetWorth = Double.MAX_VALUE;
    private double globalMaxNetWorth = Double.MIN_VALUE;
    @Override
    public void start(Stage primaryStage) {
        mainApp = new MainApp();
        traderSeriesMap = new HashMap<>();

        primaryStage.setTitle("Market Simulation");

        // TextArea to display daily events
        eventsDisplay = new TextArea();
        eventsDisplay.setEditable(false);
        eventsDisplay.setPrefHeight(200);

        // LineChart to track trader net worth over time
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Day");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Net Worth ($)");

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Trader Net Worth Over Time");

        // Create a series for each trader and add to the line chart
        for (Trader trader : mainApp.listOfTraders) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(trader.getName());
            traderSeriesMap.put(trader.getName(), series);
            lineChart.getData().add(series);
        }

        // Button to start/stop automatic simulation
        ToggleButton autoSimulateButton = new ToggleButton("Start Auto Simulation");
        autoSimulateButton.setOnAction(e -> toggleSimulation(autoSimulateButton));

        // Layout
        VBox layout = new VBox(10, eventsDisplay, autoSimulateButton, lineChart);
        layout.setPrefSize(800, 600);

        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize the Timeline for automatic simulation
        initializeTimeline();
    }

    private void initializeTimeline() {
        simulationTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> simulateDay()));
        simulationTimeline.setCycleCount(Timeline.INDEFINITE); // Run indefinitely
    }

    private void toggleSimulation(ToggleButton button) {
        if (simulationTimeline.getStatus() == Timeline.Status.RUNNING) {
            simulationTimeline.stop();
            button.setText("Start Auto Simulation");
        } else {
            simulationTimeline.play();
            button.setText("Stop Auto Simulation");
        }
    }

    private void simulateDay() {
        List<String> dailyEvents = mainApp.simulateDay();  // Get daily events from MainApp
        day++;  // Increment the day counter

        // Display daily events in the TextArea
        eventsDisplay.appendText("--- Day " + day + " ---\n");
        eventsDisplay.appendText(String.join("\n", dailyEvents) + "\n");

        // Track min and max net worth for the Y-axis
        double minNetWorth = Double.MAX_VALUE;
        double maxNetWorth = Double.MIN_VALUE;

        // Update the net worth of each trader and add to their series in the chart
        for (Trader trader : mainApp.listOfTraders) {
            double netWorth = trader.calculateNetWorth(trader.getStockPortfolio());
            XYChart.Series<Number, Number> series = traderSeriesMap.get(trader.getName());
            if (series != null) {
                series.getData().add(new XYChart.Data<>(day, netWorth));
            }

            minNetWorth = Math.min(minNetWorth, netWorth);
            maxNetWorth = Math.max(maxNetWorth, netWorth);

            // Update global min and max net worths
            globalMinNetWorth = Math.min(globalMinNetWorth, minNetWorth);
            globalMaxNetWorth = Math.max(globalMaxNetWorth, maxNetWorth);

            // Dynamically adjust Y-axis bounds
            double padding = (globalMaxNetWorth - globalMinNetWorth) * 0.1;  // Add 10% padding
            if (padding == 0) padding = 500;  // Prevent bounds from collapsing if values are the same
            lineChart.getYAxis().setAutoRanging(false);  // Disable auto-ranging
            NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
            yAxis.setLowerBound(Math.max(0, globalMinNetWorth - padding));  // Ensure lower bound is at least 0
            yAxis.setUpperBound(globalMaxNetWorth + padding);
        }
    }
}