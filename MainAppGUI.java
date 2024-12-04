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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.Node;


import java.awt.*;
import java.util.*;
import java.util.List;

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
    private Scene mainMenuScene;
    private HBox CircleLayout;
    // Starting page scene

    @Override
    public void start(Stage primaryStage) {
        mainApp = new MainApp();
        traderSeriesMap = new HashMap<>();
        traderObservableList = FXCollections.observableArrayList(mainApp.listOfTraders);

        primaryStage.getIcons().add(new Image("LOGO.jpg"));

        primaryStage.setWidth(1920);
        primaryStage.setHeight(1080);

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
        phase1Button.getStyleClass().add("phase1-button");


        Button phase2Button = new Button("Phase 2");
        phase2Button.setOnAction(e -> initializePhase2(primaryStage));
        phase2Button.setPrefWidth(200);
        phase2Button.setPrefHeight(50);
        phase2Button.getStyleClass().add("phase2-button");


//        Button compareButton = new Button("Compare Phases");
//        compareButton.setOnAction(e -> startComparison(primaryStage));
//        compareButton.setPrefWidth(200);
//        compareButton.setPrefHeight(50);

        ImageView imageView = new ImageView(new Image("Trading-Bot.jpg"));
        imageView.setPreserveRatio(true);

        VBox menuLayout = new VBox(20, phase1Button, phase2Button);
        menuLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        StackPane root = new StackPane();
        root.getChildren().addAll(imageView, menuLayout);

        mainMenuScene = new Scene(root, 1200, 1000);
        // Reference to the style file to apply it into the main menu scene
        mainMenuScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        primaryStage.setScene(mainMenuScene);
        primaryStage.setTitle("Market Simulator");
        primaryStage.show();
         
    }

    private TableView<Trader> createTraderTable() {
        TableView<Trader> table = new TableView<>(traderObservableList);
        // to make column resize automatically
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        TableColumn<Trader, String> nameColumn = new TableColumn<>("Trader Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Trader, String> initialCashColumn = new TableColumn<>("Starting Cash ($)");
        initialCashColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().initialCash).asString() // Directly access initialCash
        );

        TableColumn<Trader, Double> cashColumn = new TableColumn<>("Current Cash ($)");
        cashColumn.setCellValueFactory(new PropertyValueFactory<>("cash"));

        TableColumn<Trader, Double> netWorthColumn = new TableColumn<>("Net Worth ($)");
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

        // Add a row selection listener
        table.setRowFactory(tv -> {
            TableRow<Trader> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() & event.getClickCount() == 2) {
                    Trader selectedTrader = row.getItem();  // Get selected trader
                    showTraderInfoWindow(selectedTrader);   // Open a new window to show details
                }
            });
            return row;
        });


        return table;
    }


    private void showTraderInfoWindow(Trader trader) {
        // Create a new Stage (window)
        Stage infoStage = new Stage();
        infoStage.setTitle("Trader Details");

        // Create a VBox layout to display the trader's information
        VBox layout = new VBox(10);

        // Display trader details in labels
        Label nameLabel = new Label("Trader Name: " + trader.getName());
        Label cashLabel = new Label("Current Cash: $" + trader.getCash());
        Label netWorthLabel = new Label("Net Worth: $" + trader.getNetWorth());

        // Create an ObservableList for the portfolio
        ObservableList<Map.Entry<Stocks, Integer>> portfolioData = FXCollections.observableArrayList(trader.getStockPortfolio().entrySet());

        // Create the portfolio table
        TableView<Map.Entry<Stocks, Integer>> portfolioTable = new TableView<>(portfolioData);

        // Columns for stock name and quantity
        TableColumn<Map.Entry<Stocks, Integer>, String> stockNameColumn = new TableColumn<>("Stock Symbol");
        stockNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey().getSymbol()));

        // Column for stock price
        TableColumn<Map.Entry<Stocks, Integer>, String> stockPriceColumn = new TableColumn<>("Stock Price");
        stockPriceColumn.setCellValueFactory(cellData -> {
            double price = cellData.getValue().getKey().getPrice();
            return new SimpleStringProperty(String.format("$%.2f", price));
        });


        TableColumn<Map.Entry<Stocks, Integer>, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getValue()));

        TableColumn<Map.Entry<Stocks, Integer>, String> stockWorthColumn = new TableColumn<>("Stock Worth");
        stockWorthColumn.setCellValueFactory(cellData -> {
            Stocks stock = cellData.getValue().getKey();
            Integer quantity = cellData.getValue().getValue();
            double worth = quantity * stock.getPrice(); // Calculate stock worth
            return new SimpleStringProperty(String.format("$%.2f", worth));
        });


        // Add columns to the portfolio table
        portfolioTable.getColumns().addAll(stockNameColumn, stockPriceColumn, quantityColumn, stockWorthColumn);

        // Add portfolio data to the table directly from trader's stock portfolio
        portfolioTable.getItems().addAll(trader.getStockPortfolio().entrySet());

        // Add all elements to the layout
        layout.getChildren().addAll(nameLabel, cashLabel, netWorthLabel, portfolioTable);

        // Set up the scene and show the window
        Scene scene = new Scene(layout, 400, 300);
        infoStage.setScene(scene);
        infoStage.show();
    }




    private void ResetMainApp() {
        mainApp = new MainApp();
    }
    private void ResetButton(Stage primaryStage, boolean navigateToMainMenu, boolean isPhase1) {
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
        if (navigateToMainMenu) {
            initializeMainMenu(primaryStage); // Go to the main menu
        } else if (isPhase1) {
            initializePhase1(primaryStage); // Reload Phase 1
        } else {
            initializePhase2(primaryStage); // Reload Phase 2
        }

         
    }


    private void initializePhase1(Stage primaryStage) {
        primaryStage.setTitle("Market Simulation Phase 1");

        eventsDisplay = new TextArea();
        eventsDisplay.setEditable(false);
        eventsDisplay.setPrefHeight(200);
        eventsDisplay.getStyleClass().add("text-area");

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Day");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Net Worth ($)");

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Trader Net Worth Over Time");
        lineChart.getStyleClass().add("chart");

        for (Trader trader : mainApp.listOfTraders) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(trader.getName());
            traderSeriesMap.put(trader.getName(), series);
            lineChart.getData().add(series);
        }

        ToggleButton autoSimulateButton = new ToggleButton("Start Auto Simulation");
        autoSimulateButton.setOnAction(e -> toggleSimulation(autoSimulateButton));
        autoSimulateButton.getStyleClass().add("phase1-inner-buttons");

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> ResetButton(primaryStage, false, true));
        restartButton.getStyleClass().add("phase1-inner-buttons");

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            if (simulationTimeline != null && simulationTimeline.getStatus() == Timeline.Status.RUNNING) {
                simulationTimeline.stop();
            }
            ResetButton(primaryStage, true, true);
        });
        backButton.getStyleClass().add("phase1-inner-buttons");

        Button showTableButton = new Button("Show Trader Table");
        showTableButton.setOnAction(e -> showTraderTableWindow()); // New method for displaying the table
        showTableButton.getStyleClass().add("phase1-inner-buttons");

        // Creating circular representations for each trader
        HBox circlesLayout = new HBox(20);
        circlesLayout.setStyle("-fx-alignment: center;");

        circlesLayout = createTraderCircles();

        HBox buttonLayout = new HBox(10, autoSimulateButton, backButton, restartButton, showTableButton);
        VBox layout = new VBox(10, eventsDisplay, buttonLayout, lineChart, circlesLayout);
        layout.setPrefSize(800, 600);
        layout.getStyleClass().add("root");

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();

        initializeTimeline();
    }


    private void initializePhase2(Stage primaryStage) {
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
        restartButton.setOnAction(e -> ResetButton(primaryStage, false, false));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            if (simulationTimeline != null && simulationTimeline.getStatus() == Timeline.Status.RUNNING) {
                simulationTimeline.stop(); // Stop the simulation timeline
            }
            ResetButton(primaryStage, true, false);
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
        ChangeCircleColors(CircleLayout);

    }

    private HBox createTraderCircles() {
        CircleLayout= new HBox(10); // Horizontal layout with spacing
        CircleLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        for (Trader trader : traderObservableList) {
            double netWorth = trader.getNetWorth();
            double startingCash = trader.getCash();

            Circle circle = new Circle(80); // Circle with a radius of 40

            // Set color based on net worth compared to starting cash
            if (netWorth >= startingCash) {
                circle.setFill(Color.GREEN); // Profit (green)
            } else {
                circle.setFill(Color.RED); // Loss (red)
            }

            Text traderName = new Text(trader.getName());

            // StackPane to center the text inside the circle
            StackPane traderCircle = new StackPane(circle, traderName);
            traderCircle.setStyle("-fx-alignment: center;");

            CircleLayout.getChildren().add(traderCircle);

            traderCircle.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) { // Check for double click
                    showTraderInfoWindow(trader); // Show trader info window
                }
            }
            );

        }

        return CircleLayout;
    }

    private void ChangeCircleColors(HBox circleLayout) {

        int TraderIndex = 0;

        for (Trader trader : traderObservableList) {

            ArrayList<Double> TraderWorthHistory = trader.getWorthHistory();

            if (circleLayout.getChildren().get(TraderIndex) instanceof StackPane) {
                StackPane traderCircle = (StackPane) circleLayout.getChildren().get(TraderIndex);


                for (Node node : traderCircle.getChildren()) {
                    if (node instanceof Circle) {

                        if (TraderWorthHistory.getLast() >= TraderWorthHistory.get(TraderWorthHistory.size() - 2)) {
                            Circle circle = (Circle) node;

                            // Change the Circle's color
                            circle.setFill(Color.GREEN);
                            break; // Stop once we've found and updated the Circle

                        }else {

                            Circle circle = (Circle) node;


                            circle.setFill(Color.RED);
                            break;

                        }
                    }
                }
            }

            TraderIndex++;
        }
    }




    private void showTraderTableWindow() {
        Stage tableStage = new Stage();
        tableStage.setTitle("Trader Details");

        TableView<Trader> traderTable = createTraderTable();
        VBox layout = new VBox(traderTable);
        layout.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(layout, 800, 600);
        tableStage.setScene(scene);
        tableStage.show();
    }


}
