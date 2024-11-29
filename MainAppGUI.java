import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainAppGUI extends Application {

    private MainApp mainApp;  // Instance of MainApp for the simulation
    private TextArea eventsDisplay;  // Text area to display daily events
    private LineChart<Number, Number> lineChart;  // Line chart for trader net worth
    private Map<String, XYChart.Series<Number, Number>> traderSeriesMap;  // Map to hold chart series for each trader
    private ObservableList<Trader> traderObservableList;  // Observable list for real-time updates
    private int day = 0;  // Tracks the day for the chart
    private Timeline simulationTimeline;
    private double globalMinNetWorth = Double.MAX_VALUE;
    private double globalMaxNetWorth = Double.MIN_VALUE;
    private Scene mainMenuScene;  // Starting page scene

    @Override
    public void start(Stage primaryStage) {
        mainApp = new MainApp();
        traderSeriesMap = new HashMap<>();
        traderObservableList = FXCollections.observableArrayList(mainApp.listOfTraders);

        primaryStage.getIcons().add(new Image("LOGO.jpg"));

        // Initialize Main Menu
        initializeMainMenu(primaryStage);

        // Show the Main Menu scene initially
        primaryStage.setScene(mainMenuScene);
        primaryStage.show();
    }

    private void initializeMainMenu(Stage primaryStage) {
        Button phase1Button = new Button("Phase 1");
        phase1Button.setOnAction(e -> initializePhase1(primaryStage));
        phase1Button.setPrefWidth(200);
        phase1Button.setPrefHeight(50);

        Button phase2Button = new Button("Phase 2");
        phase2Button.setOnAction(e -> startPhase2(primaryStage));
        phase2Button.setPrefWidth(200);
        phase2Button.setPrefHeight(50);

        Button compareButton = new Button("Compare Phases");
        compareButton.setOnAction(e -> startComparison(primaryStage));
        compareButton.setPrefWidth(200);
        compareButton.setPrefHeight(50);

        ImageView imageView = new ImageView(new Image("Trading-Bot.jpg"));
        imageView.setPreserveRatio(true);

        VBox menuLayout = new VBox(20, phase1Button, phase2Button, compareButton);
        menuLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        StackPane root = new StackPane();
        root.getChildren().addAll(imageView, menuLayout);

        mainMenuScene = new Scene(root, 1200, 1000);
        primaryStage.setScene(mainMenuScene);
        primaryStage.setTitle("Market Simulator");
        primaryStage.show();
    }

    private TableView<Trader> createTraderTable() {
        TableView<Trader> table = new TableView<>(traderObservableList);

        TableColumn<Trader, String> nameColumn = new TableColumn<>("Trader Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Trader, String> initialCashColumn = new TableColumn<>("Starting Cash");
        initialCashColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().initialCash).asString() // Directly access initialCash
        );

        TableColumn<Trader, Double> cashColumn = new TableColumn<>("Current Cash");
        cashColumn.setCellValueFactory(new PropertyValueFactory<>("cash"));

        TableColumn<Trader, Double> netWorthColumn = new TableColumn<>("Net Worth");
        netWorthColumn.setCellValueFactory(new PropertyValueFactory<>("netWorth"));

        TableColumn<Trader, String> traderTypeColumn = new TableColumn<>("Trader Type");
        traderTypeColumn.setCellValueFactory(cellData -> {
            Trader trader = cellData.getValue();
            String traderType = "";

            if (trader instanceof RandomTrader) {
                traderType = "Random Trader";
            } else if (trader instanceof RSITrader) {
                traderType = "RSI Trader";
            } else if (trader instanceof MovingAverageTrader) {
                traderType = "MA Trader";
            }

            return new SimpleStringProperty(traderType);
        });

        table.getColumns().addAll(nameColumn,initialCashColumn, cashColumn, netWorthColumn, traderTypeColumn);

        return table;
    }


        private void ResetMainApp() {
        mainApp = new MainApp();
    }
    private void ResetButton(Stage primaryStage, boolean isPhase1) {
        // Stop any running simulations
        if (simulationTimeline != null && simulationTimeline.getStatus() == Timeline.Status.RUNNING) {
            simulationTimeline.stop();
        }

   
        ResetMainApp();

        // Reset UI components
        day = 0; // Reset the simulation day counter
        traderSeriesMap.clear(); // Clear chart series
        eventsDisplay.clear(); // Clear displayed events
        lineChart.getData().clear(); // Clear the line chart data
        traderObservableList.setAll(mainApp.listOfTraders); // Rebind observable list

        // Reload the current phase (Phase 1 or Phase 2)
        if (isPhase1) {
            initializePhase1(primaryStage);
        } else {
            startPhase2(primaryStage);
        }
    }


    private void initializePhase1(Stage primaryStage) {
        primaryStage.setTitle("Market Simulation Phase 1");

        eventsDisplay = new TextArea();
        eventsDisplay.setEditable(false);
        eventsDisplay.setPrefHeight(200);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Day");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Net Worth ($)");

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Trader Net Worth Over Time");

        for (Trader trader : mainApp.listOfTraders) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(trader.getName());
            traderSeriesMap.put(trader.getName(), series);
            lineChart.getData().add(series);
        }

        ToggleButton autoSimulateButton = new ToggleButton("Start Auto Simulation");
        autoSimulateButton.setOnAction(e -> toggleSimulation(autoSimulateButton));

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> ResetButton(primaryStage, true));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            if (simulationTimeline != null && simulationTimeline.getStatus() == Timeline.Status.RUNNING) {
                simulationTimeline.stop();
            }
            initializeMainMenu(primaryStage);
        });

        TableView<Trader> table = createTraderTable();

        HBox buttonLayout = new HBox(10, autoSimulateButton, backButton, restartButton);
        VBox layout = new VBox(10, eventsDisplay, buttonLayout, lineChart, table);
        layout.setPrefSize(800, 600);

        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.show();

        initializeTimeline();
    }

      private void startPhase2(Stage primaryStage) {
        primaryStage.setTitle("Market Simulation Phase 2");

        eventsDisplay = new TextArea();
        eventsDisplay.setEditable(false);
        eventsDisplay.setPrefHeight(200);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Day");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Net Worth ($)");

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Trader Net Worth Over Time");

        for (Trader trader : mainApp.listOfTraders) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(trader.getName());
            traderSeriesMap.put(trader.getName(), series);
            lineChart.getData().add(series);
        }

        ToggleButton autoSimulateButton = new ToggleButton("Start Auto Simulation");
        autoSimulateButton.setOnAction(e -> toggleSimulation(autoSimulateButton));

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> ResetButton(primaryStage, true));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            if (simulationTimeline != null && simulationTimeline.getStatus() == Timeline.Status.RUNNING) {
                simulationTimeline.stop(); // Stop the simulation timeline
            }
            initializeMainMenu(primaryStage);
        });

        TableView<Trader> table = createTraderTable();

        HBox buttonLayout = new HBox(10, autoSimulateButton, backButton, restartButton);
        VBox layout = new VBox(10, eventsDisplay, buttonLayout, lineChart, table);
        layout.setPrefSize(800, 600);

        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.show();

        initializeTimeline();
    }

    private void startComparison(Stage primaryStage) {
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> initializeMainMenu(primaryStage));

        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        TableView<Trader> table = createTraderTable();

        layout.getChildren().addAll(table, backButton);

        Scene comparisonScene = new Scene(layout, 800, 600);
        primaryStage.setScene(comparisonScene);
        primaryStage.setTitle("Market Comparison Report");
        primaryStage.show();
    }

    private void initializeTimeline() {
        simulationTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> simulateDay()));
        simulationTimeline.setCycleCount(Timeline.INDEFINITE);
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
        List<String> dailyEvents = mainApp.simulateDay();
        day++;

        eventsDisplay.appendText("--- Day " + day + " ---\n");
        eventsDisplay.appendText(String.join("\n", dailyEvents) + "\n");

        double minNetWorth = Double.MAX_VALUE;
        double maxNetWorth = Double.MIN_VALUE;

        for (Trader trader : mainApp.listOfTraders) {
            double netWorth = trader.calculateNetWorth(trader.getStockPortfolio());
            XYChart.Series<Number, Number> series = traderSeriesMap.get(trader.getName());
            if (series != null) {
                series.getData().add(new XYChart.Data<>(day, netWorth));
            }

            minNetWorth = Math.min(minNetWorth, netWorth);
            maxNetWorth = Math.max(maxNetWorth, netWorth);

            globalMinNetWorth = Math.min(globalMinNetWorth, minNetWorth);
            globalMaxNetWorth = Math.max(globalMaxNetWorth, maxNetWorth);

            double padding = (globalMaxNetWorth - globalMinNetWorth) * 0.1;
            if (padding == 0) padding = 500;
            lineChart.getYAxis().setAutoRanging(false);
            NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
            yAxis.setLowerBound(Math.max(0, globalMinNetWorth - padding));
            yAxis.setUpperBound(globalMaxNetWorth + padding);
        }

        // Update the observable list to refresh the TableView
        traderObservableList.setAll(mainApp.listOfTraders);
    }
}
