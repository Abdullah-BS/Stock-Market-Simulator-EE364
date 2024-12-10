import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.imageio.ImageIO;

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
    private VBox metricsPanel; // Metrics panel to display performance metrics
    private Label dayCounterLabel; // Make it a class-level variable
    private VBox mainLayout; // Main layout for Phase 1
    private GridPane stockGrid;
    private Random random = new Random();
    private boolean isPhase1; // Track the current phase
    // Starting page scene

    @Override
    public void start(Stage primaryStage) {
//        mainApp = new MainApp();
//        traderSeriesMap = new HashMap<>();
//        traderObservableList = FXCollections.observableArrayList(mainApp.listOfTraders);

        primaryStage.getIcons().add(new Image("LOGO.jpg"));

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        // Set the stage size and position to fit within the screen
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());

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
//       Phase 2 yet to be implemented
        phase2Button.setOnAction(e -> initializePhase2(primaryStage));
        phase2Button.setPrefWidth(200);
        phase2Button.setPrefHeight(50);
        phase2Button.getStyleClass().add("phase2-button");


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
    private void initializePhase1(Stage primaryStage) {
        isPhase1 = true; // Set the current phase to Phase 1
        mainApp = new MainApp(true);
        traderSeriesMap = new HashMap<>();
        traderObservableList = FXCollections.observableArrayList(mainApp.listOfTraders);

        primaryStage.setTitle("Market Simulation Phase 1");

        // create the Buttons layout
        HBox buttonLayout= creatButtonLayout(primaryStage, true);

        //create the Top Layout
        VBox eventsBox = createEventDisplay();
        stockGrid=creatStockGrid();

        HBox TopLayout = new HBox(10, eventsBox, stockGrid);
        TopLayout.setStyle("-fx-alignment: center;");


        //create the Right Layout
        LineChart<Number, Number> lineChart=createLineChart();
        HBox circlesLayout = createTraderCircles();

        VBox Rightlayout = new VBox(10,lineChart,circlesLayout);

        //create the Left Layout
        VBox infoPanel=createInfoPanel();
        TableView<Trader> metricsTable = createMetricTable();

        VBox Leftlayout = new VBox(10,metricsTable,infoPanel);

        //create the Middle Layout
        HBox BottomLayout = new HBox(10,Leftlayout,Rightlayout);

        // Initialize metricsPanel
        metricsPanel = new VBox(10);
        metricsPanel.setStyle("-fx-padding: 10;");

        // Create the Main layout using the above Layouts
        mainLayout = new VBox(10, TopLayout, buttonLayout, BottomLayout, metricsPanel); // Add metricsPanel to mainLayout
        mainLayout.setPrefSize(1080, 600);
        mainLayout.getStyleClass().add("root");

        //create the Scene
        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());


        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());

        primaryStage.setScene(scene);
        primaryStage.show();

        initializeTimeline();
    }


    // YET TO BE IMPLEMENTED
    private void initializePhase2(Stage primaryStage) {
        isPhase1 = false; // Set the current phase to Phase 2
        mainApp = new MainApp(false);
        traderSeriesMap = new HashMap<>();
        traderObservableList = FXCollections.observableArrayList(mainApp.listOfTraders);

        primaryStage.setTitle("Market Simulation Phase 2");

        // create the Buttons layout
        HBox buttonLayout= creatButtonLayout(primaryStage, false);

        //create the Top Layout
        VBox eventsBox = createEventDisplay();
        stockGrid=creatStockGrid();

        HBox TopLayout = new HBox(10, eventsBox, stockGrid);
        TopLayout.setStyle("-fx-alignment: center;");


        //create the Right Layout
        LineChart<Number, Number> lineChart=createLineChart();
        HBox circlesLayout = createTraderCircles();

        VBox Rightlayout = new VBox(10,lineChart,circlesLayout);

        //create the Left Layout
        VBox infoPanel=createInfoPanel();
        TableView<Trader> metricsTable = createMetricTable();

        VBox Leftlayout = new VBox(10,metricsTable,infoPanel);

        //create the Middle Layout
        HBox BottomLayout = new HBox(10,Leftlayout,Rightlayout);

        // Initialize metricsPanel
        metricsPanel = new VBox(10);
        metricsPanel.setStyle("-fx-padding: 10;");

        // Create the Main layout using the above Layouts
        mainLayout = new VBox(10, TopLayout, buttonLayout, BottomLayout, metricsPanel); // Add metricsPanel to mainLayout
        mainLayout.setPrefSize(1080, 600);
        mainLayout.getStyleClass().add("root");

        //create the Scene
        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());


        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());

        primaryStage.setScene(scene);
        primaryStage.show();

        initializeTimeline();
    }
    private VBox createEventDisplay(){

        dayCounterLabel = new Label("Day: 0"); // Initialize dayCounterLabel
        dayCounterLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        eventsDisplay = new TextArea();
        eventsDisplay.setEditable(false);
        eventsDisplay.setPrefHeight(300); // Increase height to accommodate more events
        eventsDisplay.getStyleClass().add("text-area");

        VBox eventsBox = new VBox(10, dayCounterLabel, eventsDisplay); // Include day counter with events display
        eventsBox.setMinSize(580,100);
        eventsBox.setMaxHeight(200);
        return eventsBox;
    }
    private LineChart<Number, Number> createLineChart(){
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Day");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Net Worth ($)");
        yAxis.setForceZeroInRange(false);

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Trader Net Worth Over Time");
        lineChart.getStyleClass().add("chart");
        lineChart.setMinSize(1245,400);
        lineChart.setLayoutY(100);
        lineChart.setMaxHeight(400);

        for (Trader trader : mainApp.listOfTraders) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(trader.getName());
            traderSeriesMap.put(trader.getName(), series);
            lineChart.getData().add(series);
        }
        return lineChart;
    }

    private HBox creatButtonLayout(Stage primaryStage, Boolean isphase1){
        ToggleButton autoSimulateButton = new ToggleButton("Start Auto Simulation");
        autoSimulateButton.setOnAction(e -> toggleSimulation(autoSimulateButton));

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> ResetButton(primaryStage, false, true));

        Button downloadResultsButton = new Button("Download Results");
        downloadResultsButton.setOnAction(e -> downloadResults());

        Button downloadChartButton = new Button("Download Chart");
        downloadChartButton.setOnAction(e -> downloadChart());

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            if (simulationTimeline != null && simulationTimeline.getStatus() == Timeline.Status.RUNNING) {
                simulationTimeline.stop();
            }
            ResetButton(primaryStage, true, isphase1);
        });

        Button showTableButton = new Button("Show Trader Table");
        showTableButton.setOnAction(e -> showTraderTableWindow());


        if (!isphase1){
            autoSimulateButton.getStyleClass().add("phase2-inner-buttons");
            restartButton.getStyleClass().add("phase2-inner-buttons");
            downloadResultsButton.getStyleClass().add("phase2-inner-buttons");
            downloadChartButton.getStyleClass().add("phase2-inner-buttons");
            backButton.getStyleClass().add("phase2-inner-buttons");
            showTableButton.getStyleClass().add("phase2-inner-buttons");
        }
        else {
            autoSimulateButton.getStyleClass().add("phase1-inner-buttons");
            restartButton.getStyleClass().add("phase1-inner-buttons");
            downloadResultsButton.getStyleClass().add("phase1-inner-buttons");
            downloadChartButton.getStyleClass().add("phase1-inner-buttons");
            backButton.getStyleClass().add("phase1-inner-buttons");
            showTableButton.getStyleClass().add("phase1-inner-buttons");

        }

        HBox buttonLayout = new HBox(10, autoSimulateButton, backButton, restartButton, showTableButton, downloadResultsButton, downloadChartButton);
        return buttonLayout;
    }


    private VBox createInfoPanel() {
        VBox infoPanel = new VBox(18);
        infoPanel.getStyleClass().add("InfoPanel");
        infoPanel.setPrefSize(200, 500);

        Label traderActionsTitle = new Label("Metrics Advices vs Trader Actions");
        traderActionsTitle.setStyle("-fx-font-weight: bold;-fx-font-size: 16px;");
        infoPanel.getChildren().add(traderActionsTitle);

        // A map to hold the existing labels (for updating purposes)
        Map<Trader, List<Label>> traderLabelMap = new HashMap<>();

        for (Trader trader : mainApp.listOfTraders) {
            VBox traderBox = new VBox(1);  // This VBox will hold the trader's data

            ObservableMap<String, String> adviceVsAction = trader.getBuy_Advice_VS_action();

            // Trader name
            Label traderName = new Label("Trader: " + trader.getName());
            traderName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            traderBox.getChildren().add(traderName);  // Add the trader name to this specific VBox

            // Store initial labels for each trader
            List<Label> traderLabels = new ArrayList<>();
            traderLabelMap.put(trader, traderLabels);

            // Add the placeholder label for "No data"
            Label noDataLabel = new Label("No advice or actions recorded yet.");
            noDataLabel.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");
            traderBox.getChildren().add(noDataLabel);  // Add the placeholder to the trader's VBox

            // Listener to update the panel when the map changes
            adviceVsAction.addListener((MapChangeListener<String, String>) change -> {
                // Debug log to check if the listener is being triggered
                System.out.println("Advice map changed for trader: " + trader.getName());
                refreshTraderInfo(infoPanel, traderLabels, adviceVsAction, trader, noDataLabel, traderBox);
            });

            // If there's existing data, update immediately
            if (!adviceVsAction.isEmpty()) {
                System.out.println("Initializing trader data: " + trader.getName());
                refreshTraderInfo(infoPanel, traderLabels, adviceVsAction, trader, noDataLabel, traderBox);
            }

            // Add the trader's VBox (containing the trader's data) to the main panel
            infoPanel.getChildren().add(traderBox);

            // Add a separator for clarity
            Separator separator = new Separator();
            infoPanel.getChildren().add(separator);
        }

        return infoPanel;
    }

    private void refreshTraderInfo(VBox infoPanel, List<Label> traderLabels, ObservableMap<String, String> adviceVsAction, Trader trader, Label noDataLabel, VBox traderBox) {
        // Remove the placeholder label if data is present
        if (!adviceVsAction.isEmpty()) {
            traderBox.getChildren().remove(noDataLabel);
        }

        // Clear the previous labels for the trader
        traderBox.getChildren().removeAll(traderLabels);

        // Get the last advice and action (the latest entry in the map)
        Map.Entry<String, String> lastEntry = null;
        for (Map.Entry<String, String> entry : adviceVsAction.entrySet()) {
            lastEntry = entry;  // The last entry will overwrite this value in the loop
        }

        // Debug log to verify the last entry
        if (lastEntry != null) {
            System.out.println("Last advice: " + lastEntry.getKey() + ", Last action: " + lastEntry.getValue());
        }

        // If a last entry exists, display the advice and action
        if (lastEntry != null) {
            String advice = lastEntry.getKey();
            String action = lastEntry.getValue();

            Label adviceLabel = new Label("Advice: " + advice);
            adviceLabel.setStyle("-fx-font-style: italic;");
            Label actionLabel = new Label("Action: " + action);
            actionLabel.setStyle("-fx-font-style: italic;");

            // Add the last advice and action to the trader's VBox
            traderLabels.add(adviceLabel);
            traderLabels.add(actionLabel);

            // Add them to the trader's VBox
            Platform.runLater(() -> {
                traderBox.getChildren().addAll(adviceLabel, actionLabel);
            });
        }
    }


    private TableView<Trader>  createTraderTable(){


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


    private TableView<Trader> createMetricTable() {
        TableView<Trader> metricTable = new TableView<>(traderObservableList);
        // to make column resize automatically
        metricTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        metricTable.setMinHeight(150);



        TableColumn<Trader, String> nameColumn = new TableColumn<>("Trader Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Trader, String> totalTradesColumn = new TableColumn<>("Total Trades");
        totalTradesColumn.setCellValueFactory(new PropertyValueFactory<>("totalTrades"));

        TableColumn<Trader, Double> WinColumn = new TableColumn<>("Win Ratio");
        WinColumn.setCellValueFactory(new PropertyValueFactory<>("winCount"));

        TableColumn<Trader, Double> LossColumn = new TableColumn<>("Loss Ratio");
        LossColumn.setCellValueFactory(new PropertyValueFactory<>("lossCount"));

        TableColumn<Trader, Double> AvgProfitColumn = new TableColumn<>("Avg Profit/Trade");
        AvgProfitColumn.setCellValueFactory(new PropertyValueFactory<>("averageProfit"));


        metricTable.getColumns().addAll(nameColumn,totalTradesColumn, WinColumn, LossColumn,AvgProfitColumn);

        // Add a row selection listener
        metricTable.setRowFactory(tv -> {
            TableRow<Trader> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() & event.getClickCount() == 2) {
                    Trader selectedTrader = row.getItem();  // Get selected trader
                    showTraderInfoWindow(selectedTrader);   // Open a new window to show details
                }
            });
            return row;
        });

        metricTable.setMinWidth(580);
        return metricTable;
    }

    private GridPane creatStockGrid() {
        stockGrid = new GridPane();
        stockGrid.setHgap(10);
        stockGrid.setVgap(10);
        stockGrid.setMaxSize(10000,100);
        stockGrid.setStyle("-fx-padding: 20;");
        stockGrid.setStyle("-fx-background-color: black; -fx-hgap: 2; -fx-vgap: 2; -fx-border-color: black; -fx-border-width: 4;");

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 11; col++) {
                Label emptyLabel = new Label("                   ");
                emptyLabel.setFont(Font.font(12));
                emptyLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5;-fx-min-width: 110;");
                stockGrid.add(emptyLabel, col, row);
            }
        }
        return stockGrid;
    }

    private void populateStockGrid(GridPane stockGrid, ArrayList<Stocks> stockList) {
        stockGrid.getChildren().clear();

        int column = 0;
        int row = 0;

        for (Stocks stock : stockList) {
            String formattedPrice = String.format("%.2f", stock.getPrice());
            if (stock.getPrice() >= stock.getPriceHistory().get(stock.getPriceHistory().size() - 2)) {
                Label stockLabel = new Label(stock.getSymbol() + " - $" + formattedPrice);
                stockLabel.setStyle("-fx-background-color: lightgreen; -fx-padding: 5; -fx-font-size: 12;-fx-border-color: white;-fx-border-width: 2;-fx-min-width: 110");
                stockGrid.add(stockLabel, column, row);

                column++;
            } else {
                Label stockLabel = new Label(stock.getSymbol() + " - $" + formattedPrice);
                stockLabel.setStyle("-fx-background-color: #FF9999; -fx-padding: 5; -fx-font-size: 12;-fx-border-color: white;-fx-border-width: 2; -fx-min-width: 110");
                stockGrid.add(stockLabel, column, row);

                column++;
            }

            if (column == 11) {
                column = 0;
                row++;
            }
        }
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
        portfolioTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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




    private void ResetMainApp(Boolean isPhase1) {
        if (isPhase1 == true)
        mainApp = new MainApp(true); // Reinitialize the MainApp instance
        else mainApp = new MainApp(false);
    }

    private void ResetButton(Stage primaryStage, boolean navigateToMainMenu, boolean isPhase1) {
        if (simulationTimeline != null && simulationTimeline.getStatus() == Timeline.Status.RUNNING) {
            simulationTimeline.stop();
        }

        ResetMainApp(isPhase1);

        day = 0; // Reset the day counter
        traderSeriesMap.clear();
        eventsDisplay.clear();
        lineChart.getData().clear();
        traderObservableList.setAll(mainApp.listOfTraders);

        // Reload the current phase or go to the main menu
        if (navigateToMainMenu) {
            initializeMainMenu(primaryStage);
        } else {
            // Check the current phase and reset to it
            if (this.isPhase1) {
                initializePhase1(primaryStage);
            } else {
                initializePhase2(primaryStage);
            }
        }
    }


    private void initializeTimeline() {
        simulationTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> simulateDay()));
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
        dayCounterLabel.setText("Day: " + day);

        // Update day counter

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

            // Simulate trade results and update metrics
            boolean isWinningTrade = random.nextBoolean(); // Simulate win/loss (replace with actual logic)
            double profit = isWinningTrade ? 100 : -50;    // Simulate profit/loss
            trader.updateMetrics(isWinningTrade, profit);
        }

        globalMinNetWorth = Math.min(globalMinNetWorth, minNetWorth);
        globalMaxNetWorth = Math.max(globalMaxNetWorth, maxNetWorth);

        double padding = (globalMaxNetWorth - globalMinNetWorth) * 0.1;
        if (padding == 0) padding = 500;
        lineChart.getYAxis().setAutoRanging(false);
        NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
        yAxis.setLowerBound(Math.max(0, globalMinNetWorth - padding));
        yAxis.setUpperBound(globalMaxNetWorth + padding);

        traderObservableList.setAll(mainApp.listOfTraders);
        ChangeCircleColors(CircleLayout);
        ArrayList<Stocks> stockList = mainApp.marketSimulator.getListStock();
        populateStockGrid(stockGrid, stockList);
        addTooltipsToChart();

        // Update metrics panel
        metricsPanel.getChildren().clear(); // Clear previous metrics
        Label metricsTitle = new Label("Trader Performance Metrics");
        metricsTitle.setStyle("-fx-font-weight: bold;");
        metricsPanel.getChildren().add(metricsTitle); // Re-add title

        for (Trader trader : traderObservableList) {
            Label traderMetrics = new Label(trader.getName() +
                    " - Total Trades: " + trader.getTotalTrades() +
                    ", Win/Loss Ratio: " + String.format("%.2f", trader.getWinLossRatio()) +
                    ", Avg Profit/Trade: $" + String.format("%.2f", trader.getAverageProfitPerTrade()));
            metricsPanel.getChildren().add(traderMetrics);
        }
    }




    private HBox createTraderCircles() {
        CircleLayout = new HBox(10);
        CircleLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        for (Trader trader : traderObservableList) {
            Circle circle = new Circle(100);
            circle.setFill(Color.ORANGE);

            Text traderName = new Text(trader.getName());
            traderName.getStyleClass().add("circle-text");

            StackPane traderCircle = new StackPane(circle, traderName);
            traderCircle.setStyle("-fx-alignment: center;");

            CircleLayout.getChildren().add(traderCircle);

            traderCircle.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) { // Double-click to show metrics
                    showTraderPerformanceMetrics(trader);
                }
            });
        }

        return CircleLayout;
    }

    private void addTooltipsToChart() {
        for (Trader trader : mainApp.listOfTraders) {
            XYChart.Series<Number, Number> series = traderSeriesMap.get(trader.getName());
            if (series != null) {
                for (XYChart.Data<Number, Number> data : series.getData()) {
                    Platform.runLater(() -> {
                        Node node = data.getNode();
                        if (node != null) {
                            Tooltip tooltip = new Tooltip(
                                    "Day: " + data.getXValue() +
                                            "\nNet Worth: $" + String.format("%.2f", data.getYValue()) +
                                            "\nTotal Trades: " + trader.getTotalTrades() +
                                            "\nAverage Profit: $" + String.format("%.2f", trader.getAverageProfitPerTrade()) +
                                            "\nWin/Loss Ratio: " + trader.getWinCount() + ":" + trader.getLossCount()
                            );
                            Tooltip.install(node, tooltip);

                            // Customize the node for better interaction
                            node.setOnMouseEntered(e -> node.setStyle("-fx-scale: 1.2; -fx-cursor: hand;"));
                            node.setOnMouseExited(e -> node.setStyle("-fx-scale: 1.0;"));
                        }
                    });
                }
            }
        }
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
    private void showTraderPerformanceMetrics(Trader trader) {
        Stage metricsStage = new Stage();
        metricsStage.setTitle(trader.getName() + " - Performance Metrics");

        Label totalTradesLabel = new Label("Total Trades: " + trader.getTotalTrades());
        Label avgProfitLabel = new Label(String.format("Average Profit/Trade: $%.2f", trader.getAverageProfitPerTrade()));
        Label winLossRatioLabel = new Label("Win/Loss Ratio: " + trader.getWinCount() + ":" + trader.getLossCount());

        VBox metricsBox = new VBox(10, totalTradesLabel, avgProfitLabel, winLossRatioLabel);
        metricsBox.setStyle("-fx-padding: 20; -fx-border-color: gray; -fx-border-width: 1; -fx-border-radius: 5;");

        Scene scene = new Scene(metricsBox, 300, 200);
        metricsStage.setScene(scene);
        metricsStage.show();
    }

    private void downloadResults() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Results");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", ".csv"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("Trader Name,Net Worth");
                for (Trader trader : mainApp.listOfTraders) {
                    writer.printf("%s,%.2f%n", trader.getName(), trader.getNetWorth());
                }
                showAlert("Results saved successfully!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    } private void downloadChart() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Chart");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", ".png"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            WritableImage image = lineChart.snapshot(new SnapshotParameters(), null);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                showAlert("Chart saved successfully!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
