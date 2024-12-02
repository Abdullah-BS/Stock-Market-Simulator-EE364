import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.imageio.ImageIO;

public class MainAppGUI extends Application {

    private MainApp mainApp; // Instance of MainApp for the simulation
    private TextArea eventsDisplay; // Text area to display daily events
    private LineChart<Number, Number> lineChart; // Line chart for trader net worth
    private Map<String, XYChart.Series<Number, Number>> traderSeriesMap; // Map for chart series
    private int day = 0; // Tracks the day for the chart
    private Timeline simulationTimeline;
    private Label dayCounterLabel; // Displays the current day

    @Override
    public void start(Stage primaryStage) {
        mainApp = new MainApp();
        traderSeriesMap = new HashMap<>();
        initializeMainMenu(primaryStage);
    }

    private void initializeMainMenu(Stage primaryStage) {
        StackPane root = new StackPane();
    
        // Background image
        ImageView backgroundImage = new ImageView("Trading-Bot.jpg");
        backgroundImage.setPreserveRatio(true);
        backgroundImage.setFitWidth(1200);
        backgroundImage.setFitHeight(1200);
        backgroundImage.setStyle("-fx-opacity: 0.85;");
    
        // Menu buttons
        Label headerTitle = new Label("Market Simulator");
        headerTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
    
        Button phase1Button = createStyledButton("Phase 1", "#007bff", e -> initializePhase(primaryStage, "Market Simulation Phase 1"));
        Button phase2Button = createStyledButton("Phase 2", "#28a745", e -> initializePhase(primaryStage, "Market Simulation Phase 2"));
        Button compareButton = createStyledButton("Compare Phases", "#ffc107", e -> startComparison(primaryStage));
    
        VBox menuLayout = new VBox(20, headerTitle, phase1Button, phase2Button, compareButton);
        menuLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");
    
        root.getChildren().addAll(backgroundImage, menuLayout);
    
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Market Simulator");
        primaryStage.show();
    }
    
    private void initializePhase(Stage primaryStage, String title) {
        primaryStage.setTitle(title);

        Label headerTitle = new Label(title);
        headerTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label latestNewsLabel = new Label("Latest News");
        latestNewsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #ffcc00; -fx-font-weight: bold;");

        eventsDisplay = new TextArea();
        eventsDisplay.setEditable(false);
        eventsDisplay.setPrefHeight(100);
        eventsDisplay.setStyle("-fx-font-size: 14px; -fx-control-inner-background: #2e2e2e; -fx-text-fill: white; -fx-padding: 5px;");

        dayCounterLabel = new Label("Day: 0");
        dayCounterLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Day");
        xAxis.setStyle("-fx-tick-label-fill: white;");
        xAxis.setTickUnit(1);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Net Worth ($)");
        yAxis.setStyle("-fx-tick-label-fill: white;");

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Trader Net Worth Over Time");
        lineChart.setStyle("-fx-background-color: transparent;");

        Map<String, String> traderColors = Map.of(
            "Abdullah", "red",
            "Ahmed", "blue",
            "Yamin", "green",
            "Saud", "orange",
            "Mohanned", "purple"
        );

        for (Trader trader : mainApp.listOfTraders) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(trader.getName());
            traderSeriesMap.put(trader.getName(), series);
            lineChart.getData().add(series);

            String color = traderColors.getOrDefault(trader.getName(), "gray");
            Platform.runLater(() -> series.getNode().setStyle("-fx-stroke: " + color + "; -fx-background-color: " + color + ", white;"));
        }

        ToggleButton autoSimulateButton = createStyledToggleButton("Start Auto Simulation", "#007bff");
        autoSimulateButton.setOnAction(e -> toggleSimulation(autoSimulateButton));

        Button downloadResultsButton = createStyledButton("Download Results", "#17a2b8", e -> downloadResults());
        Button downloadChartButton = createStyledButton("Download Chart", "#ffc107", e -> downloadChart());
        Button resetButton = createStyledButton("Reset", "#28a745", e -> resetSimulation());
        Button backButton = createStyledButton("Back", "#007bff", e -> {
            if (simulationTimeline != null && simulationTimeline.getStatus() == Timeline.Status.RUNNING) {
                simulationTimeline.stop(); // Stop the simulation before navigating back
            }
            initializeMainMenu(primaryStage); // Go back to the main menu
        });
    
        HBox buttonLayout = new HBox(10, autoSimulateButton, resetButton, backButton, downloadResultsButton, downloadChartButton);
        buttonLayout.setStyle("-fx-alignment: center; -fx-padding: 10px;");
    
        VBox layout = new VBox(10, headerTitle, latestNewsLabel, eventsDisplay, dayCounterLabel, lineChart, buttonLayout);
        layout.setStyle("-fx-background-color: #1e1e1e; -fx-padding: 10px;");
    
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    
        initializeTimeline(); // Start the timeline for simulation
    }

    private Button createStyledButton(String text, String color, EventHandler<ActionEvent> action) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 16px; -fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold;");
        button.setOnAction(action);
        return button;
    }

    private ToggleButton createStyledToggleButton(String text, String color) {
        ToggleButton button = new ToggleButton(text);
        button.setStyle("-fx-font-size: 16px; -fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold;");
        return button;
    }

    private void toggleSimulation(ToggleButton button) {
        if (simulationTimeline != null && simulationTimeline.getStatus() == Timeline.Status.RUNNING) {
            simulationTimeline.stop();
            button.setText("Start Auto Simulation");
        } else {
            simulationTimeline.play();
            button.setText("Stop Auto Simulation");
        }
    }

    private void resetSimulation() {
        if (simulationTimeline != null && simulationTimeline.getStatus() == Timeline.Status.RUNNING) {
            simulationTimeline.stop(); // Stop the timeline if it's running
        }
    
        day = 0; // Reset the day counter
        dayCounterLabel.setText("Day: 0");
        eventsDisplay.clear(); // Clear the event log
        lineChart.getData().clear(); // Clear the chart
        traderSeriesMap.clear(); // Clear trader series map
    
        // Reinitialize traders and chart data
        Map<String, String> traderColors = Map.of(
            "Abdullah", "red",
            "Ahmed", "blue",
            "Yamin", "green",
            "Saud", "orange",
            "Mohanned", "purple"
        );
    
        for (Trader trader : mainApp.listOfTraders) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(trader.getName());
            traderSeriesMap.put(trader.getName(), series);
            lineChart.getData().add(series);
    
            String color = traderColors.getOrDefault(trader.getName(), "gray");
            Platform.runLater(() -> series.getNode().setStyle("-fx-stroke: " + color + "; -fx-background-color: " + color + ", white;"));
        }
    }
    

    private void initializeTimeline() {
        simulationTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> simulateDay()));
        simulationTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void simulateDay() {
        day++;
        dayCounterLabel.setText("Day: " + day);

        List<String> dailyEvents = mainApp.simulateDay();
        eventsDisplay.appendText("--- Day " + day + " ---\n");
        eventsDisplay.appendText(String.join("\n", dailyEvents) + "\n");

        double globalMin = Double.MAX_VALUE;
        double globalMax = Double.MIN_VALUE;

        for (Trader trader : mainApp.listOfTraders) {
            double netWorth = trader.calculateNetWorth(trader.getStockPortfolio());
            XYChart.Series<Number, Number> series = traderSeriesMap.get(trader.getName());
            if (series != null) {
                series.getData().add(new XYChart.Data<>(day, netWorth));
            }

            globalMin = Math.min(globalMin, netWorth);
            globalMax = Math.max(globalMax, netWorth);
        }

        NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
        double padding = (globalMax - globalMin) * 0.1;
        if (padding == 0) padding = 500;
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(Math.max(0, globalMin - padding));
        yAxis.setUpperBound(globalMax + padding);
    }

    private void startComparison(Stage primaryStage) {
        Button backButton = createStyledButton("Back", "#007bff", e -> initializeMainMenu(primaryStage));

        TableView<Trader> table = new TableView<>();
        TableColumn<Trader, String> nameColumn = new TableColumn<>("Trader Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Trader, Double> cashColumn = new TableColumn<>("Starting Cash");
        cashColumn.setCellValueFactory(new PropertyValueFactory<>("cash"));

        TableColumn<Trader, Double> netWorthColumn = new TableColumn<>("Net Worth");
        netWorthColumn.setCellValueFactory(new PropertyValueFactory<>("netWorth"));

        table.getColumns().addAll(nameColumn, cashColumn, netWorthColumn);

        for (Trader trader : mainApp.listOfTraders) {
            table.getItems().add(trader);
        }

        VBox layout = new VBox(10, table, backButton);
        layout.setStyle("-fx-background-color: #1e1e1e; -fx-padding: 20px;");

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Trader Comparison");
        primaryStage.show();
    }

    private void downloadChart() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Chart");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            WritableImage image = lineChart.snapshot(new SnapshotParameters(), null);

            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Download Chart");
                alert.setHeaderText(null);
                alert.setContentText("Chart downloaded successfully!");
                alert.showAndWait();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void downloadResults() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Results");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("Trader Name,Net Worth");
                for (Trader trader : mainApp.listOfTraders) {
                    writer.printf("%s,%.2f%n", trader.getName(), trader.getNetWorth());
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Download Results");
                alert.setHeaderText(null);
                alert.setContentText("Results downloaded successfully!");
                alert.showAndWait();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void initializeChartData() {
        // Initialize or reset the chart data here
        for (Trader trader : mainApp.listOfTraders) {
            XYChart.Series<Number, Number> series = traderSeriesMap.get(trader.getName());
            if (series != null) {
                series.getData().clear(); // Clear existing data
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
