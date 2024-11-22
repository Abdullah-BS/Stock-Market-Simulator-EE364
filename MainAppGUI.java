import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.beans.property.SimpleStringProperty;

import java.awt.*;
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
    private Scene mainMenuScene;  // Starting page scene
    private Scene simulationScene;  // Scene for your existing simulation

    @Override
    public void start(Stage primaryStage) {
        mainApp = new MainApp();
        traderSeriesMap = new HashMap<>();

        primaryStage.getIcons().add(new Image("LOGO.jpg"));


        // Initialize Main Menu
        initializeMainMenu(primaryStage);

        // Show the Main Menu scene initially
        primaryStage.setScene(mainMenuScene);
        primaryStage.show();

    }
    private void initializeMainMenu(Stage primaryStage) {


        //Phase1Button created with specified details
        Button phase1Button = new Button("Phase 1");
        phase1Button.setOnAction(e -> initializePhase1(primaryStage)); // button1 action "Go to Phase 1"
        phase1Button.setPrefWidth(200);
        phase1Button.setPrefHeight(50);

        // button details and hover details
        phase1Button.setStyle("-fx-font-size: 16px; -fx-background-color: #007bff; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-border-radius: 2; -fx-border-color: #0056b3; -fx-border-width: 2px;");
        phase1Button.setOnMouseEntered(e -> phase1Button.setStyle("-fx-font-size: 16px; -fx-background-color: #0056b3; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5; -fx-border-color: #003f73; -fx-border-width: 2px;"));
        phase1Button.setOnMouseExited(e -> phase1Button.setStyle("-fx-font-size: 16px; -fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5; -fx-border-color: #0056b3; -fx-border-width: 2px;"));

        // Phase2Button created with specified details
        Button phase2Button = new Button("Phase 2");
        phase2Button.setOnAction(e -> startPhase2(primaryStage)); // button2 action "Go to Phase 2"
        phase2Button.setPrefWidth(200);
        phase2Button.setPrefHeight(50);

        // button details and hover details
        phase2Button.setStyle("-fx-font-size: 16px; -fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5; -fx-border-color: #218838; -fx-border-width: 2px;");  // Set font size to make text larger
        phase2Button.setOnMouseEntered(e -> phase2Button.setStyle("-fx-font-size: 16px; -fx-background-color: #218838; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5; -fx-border-color: #1c7a2d; -fx-border-width: 2px;"));
        phase2Button.setOnMouseExited(e -> phase2Button.setStyle("-fx-font-size: 16px; -fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5; -fx-border-color: #218838; -fx-border-width: 2px;"));

        // compareButton created with specified details
        Button compareButton = new Button("Compare Phases");
        compareButton.setOnAction(e -> startComparison(primaryStage)); // button3 action "Go to Compare phases"
        compareButton.setPrefWidth(200);
        compareButton.setPrefHeight(50);

        // button details and hover details
        compareButton.setStyle("-fx-font-size: 16px; -fx-background-color: #ffc107; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5; -fx-border-color: #e0a800; -fx-border-width: 2px;");  // Set font size to make text larger
        compareButton.setOnMouseEntered(e -> compareButton.setStyle("-fx-font-size: 16px; -fx-background-color: #e0a800; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5; -fx-border-color: #c57d00; -fx-border-width: 2px;"));
        compareButton.setOnMouseExited(e -> compareButton.setStyle("-fx-font-size: 16px; -fx-background-color: #ffc107; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5; -fx-border-color: #e0a800; -fx-border-width: 2px;"));

        // creating image object for the main menu
        ImageView imageView = new ImageView(new Image("Trading-Bot.jpg"));
        imageView.setPreserveRatio(true);


        // Main Menu Layout
        VBox menuLayout = new VBox(20,phase1Button, phase2Button, compareButton);
        menuLayout.setStyle("-fx-alignment: center; -fx-padding: 20; "); // Semi-transparent background for buttons


        // StackPane for layering the image and buttons
        StackPane root = new StackPane();
        root.getChildren().add(imageView); // Add the image as the background
        root.getChildren().add(menuLayout); // Place buttons on top of the image

        // Set the scene and show the primary stage
        mainMenuScene = new Scene(root, 1200, 1000);  // Set the scene width and height
        primaryStage.setScene(mainMenuScene);
        primaryStage.setTitle("Market Simulator");
        primaryStage.show();
    }


    private void initializePhase1(Stage primaryStage) {


        primaryStage.setTitle("Market Simulation Phase 1");

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

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> initializeMainMenu(primaryStage));

        // HBox to place the buttons horizontally
        HBox buttonLayout = new HBox(10, autoSimulateButton, backButton);  // 10px gap between buttons

        // Layout
        VBox layout = new VBox(10, eventsDisplay,buttonLayout , lineChart);
        layout.setPrefSize(800, 600);

        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize the Timeline for automatic simulation
        initializeTimeline();

    }
    private void startPhase2(Stage primaryStage) {

        primaryStage.setTitle("Market Simulation Phase 2");

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

        // Button to start or stop automatic simulation
        ToggleButton autoSimulateButton = new ToggleButton("Start Auto Simulation");
        autoSimulateButton.setOnAction(e -> toggleSimulation(autoSimulateButton));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> initializeMainMenu(primaryStage));

        // HBox to place the buttons horizontally
        HBox buttonLayout = new HBox(10, autoSimulateButton, backButton);  // 10px gap between buttons

        // Layout
        VBox layout = new VBox(10, eventsDisplay,buttonLayout , lineChart);
        layout.setPrefSize(800, 600);

        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize the Timeline for automatic simulation
        initializeTimeline();

    }

    private void startComparison(Stage primaryStage) {
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> initializeMainMenu(primaryStage));

        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Create TableView for comparison
        TableView<Trader> table = new TableView<>();

        // Define columns for the TableView each
        TableColumn<Trader, String> nameColumn = new TableColumn<>("Trader Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));//---> using the getName method to add values

        TableColumn<Trader, Double> cashColumn = new TableColumn<>("Starting Cash");
        cashColumn.setCellValueFactory(new PropertyValueFactory<>("cash"));//---> using the getCash method to add values

        TableColumn<Trader, Double> netWorthColumn = new TableColumn<>("Net Worth");
        netWorthColumn.setCellValueFactory(new PropertyValueFactory<>("netWorth"));//---> using the getNetWorth method to add values

        TableColumn<Trader, String> traderTypeColumn = new TableColumn<>("Trader Type");

        // vvv this could be written better vvv
        traderTypeColumn.setCellValueFactory(cellData -> {

            Trader trader = cellData.getValue();
            String traderType = "";

            // Determine trader type dynamically
            if (trader instanceof RandomTrader) {
                traderType = "Random Trader";
            } else if (trader instanceof RSITrader) {
                traderType = "RSI Trader";
            } else if (trader instanceof MovingAverageTrader) {
                traderType = "MA Trader";
            }

            return new  SimpleStringProperty(traderType);
        });
        // ^^^ this could be written better ^^^

        table.getColumns().addAll(nameColumn, cashColumn, netWorthColumn, traderTypeColumn);

        // Populate the table with traders' data
        for (Trader trader : mainApp.listOfTraders) {

            table.getItems().add(trader);

        }
            // Layout for comparison
            layout.getChildren().addAll(table, backButton);

            Scene comparisonScene = new Scene(layout, 800, 600);
            primaryStage.setScene(comparisonScene);
            primaryStage.setTitle("Market Comparison Report");
            primaryStage.show();

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