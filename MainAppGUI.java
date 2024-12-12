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
    private Label dayCounterLabel; // Make it a class-level variable
    private VBox mainLayout; // Main layout for Phase 1
    private GridPane stockGrid;
    private Random random = new Random();
    private boolean isPhase1; // Track the current phase
    // Starting page scene

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image("LOGO.jpg"));

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds(); // Get screen bounds

        // Set the stage size and position to fit within the screen
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());

        // Initialize Main Menu
        initializeMainMenu(primaryStage);


        // Show the Main Menu scene initially
        primaryStage.setScene(mainMenuScene); // Set the scene to the main menu
        primaryStage.show(); // Display the stage

    }

    private void initializeMainMenu(Stage primaryStage) {
        // Create Phase 1 button and set its action and style
        Button phase1Button = new Button("Phase 1");
        phase1Button.setOnAction(e -> initializePhase1(primaryStage));
        phase1Button.setPrefWidth(200);
        phase1Button.setPrefHeight(50);
        phase1Button.getStyleClass().add("phase1-button");


        // Create Phase 2 button and set its action and style
        Button phase2Button = new Button("Phase 2");
        phase2Button.setOnAction(e -> initializePhase2(primaryStage));
        phase2Button.setPrefWidth(200);
        phase2Button.setPrefHeight(50);
        phase2Button.getStyleClass().add("phase2-button");


        // Create image view for logo
        ImageView imageView = new ImageView(new Image("Trading-Bot.jpg"));
        imageView.setPreserveRatio(true);

        // Layout for the buttons
        VBox menuLayout = new VBox(20, phase1Button, phase2Button);
        menuLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Root container for the scene
        StackPane root = new StackPane();
        root.getChildren().addAll(imageView, menuLayout);

        mainMenuScene = new Scene(root, 1200, 1000);
        // Reference to the style file to apply it into the main menu scene
        mainMenuScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        // Set the scene and title for the primary stage
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

        // Create the Top Layout with events and stock grid
        VBox eventsBox = createEventDisplay();
        stockGrid=creatStockGrid();

        HBox TopLayout = new HBox(10, eventsBox, stockGrid);
        TopLayout.setStyle("-fx-alignment: center;");


        // Create the Right Layout with a line chart and trader circles
        LineChart<Number, Number> lineChart=createLineChart();
        HBox circlesLayout = createTraderCircles();

        VBox Rightlayout = new VBox(10,lineChart,circlesLayout);

        // Create the Left Layout with metrics table and info panel
        VBox infoPanel=createInfoPanel();
        TableView<Trader> metricsTable = createMetricTable(true);

        VBox Leftlayout = new VBox(10,metricsTable,infoPanel);

        // Create the Bottom Layout combining Left and Right layouts
        HBox BottomLayout = new HBox(10,Leftlayout,Rightlayout);


        // Create the Main layout using the above Layouts
        mainLayout = new VBox(10, TopLayout, buttonLayout, BottomLayout); // Add metricsPanel to mainLayout
        mainLayout.setPrefSize(1080, 600);
        mainLayout.getStyleClass().add("root");

        //create the Scene
        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());


        // Set the stage size and position to fit within the screen
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());

        primaryStage.setScene(scene); // Set the scene for the primary stage
        primaryStage.show(); // Show the primary stage

        initializeTimeline(); // Initialize the timeline for Phase 1
    }


    private void initializePhase2(Stage primaryStage) {
        isPhase1 = false; // Set the current phase to Phase 2
        mainApp = new MainApp(false); // Initialize the main app for Phase 2
        traderSeriesMap = new HashMap<>(); // Initialize the map to store trader data
        traderObservableList = FXCollections.observableArrayList(mainApp.listOfTraders); // Create an observable list of traders

        primaryStage.setTitle("Market Simulation Phase 2"); // Set the stage title

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
        TableView<Trader> metricsTable = createMetricTable(false);

        VBox Leftlayout = new VBox(10,metricsTable,infoPanel);

        // Create the Bottom Layout combining Left and Right layouts
        HBox BottomLayout = new HBox(10,Leftlayout,Rightlayout);

        // Create the Main layout using the above Layouts
        mainLayout = new VBox(10, TopLayout, buttonLayout, BottomLayout); // Add metricsPanel to mainLayout
        mainLayout.setPrefSize(1080, 600);
        mainLayout.getStyleClass().add("root");

        //create the Scene
        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());


        // Set the stage size and position to fit within the screen
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());

        primaryStage.setScene(scene); // Set the scene for the primary stage
        primaryStage.show(); // Show the primary stage

        initializeTimeline(); // Initialize the timeline for Phase 2
    }
    private VBox createEventDisplay(){

        dayCounterLabel = new Label("Day: 0"); // Initialize the day counter label
        dayCounterLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;"); // Style the day counter label

        // Create a text area to display events
        eventsDisplay = new TextArea();
        eventsDisplay.setEditable(false);
        eventsDisplay.setPrefHeight(300);
        eventsDisplay.getStyleClass().add("text-area");

        VBox eventsBox = new VBox(10, dayCounterLabel, eventsDisplay);
        eventsBox.setMinSize(580, 100);
        eventsBox.setMaxHeight(200);
        return eventsBox;
    }
    private LineChart<Number, Number> createLineChart(){
        // Create the X and Y axes for the line chart
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Day");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Net Worth ($)");
        yAxis.setForceZeroInRange(false);

        // Create the line chart with the X and Y axes
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Trader Net Worth Over Time");
        lineChart.getStyleClass().add("chart");
        lineChart.setMinSize(1245,400);
        lineChart.setLayoutY(100);
        lineChart.setMaxHeight(400);

        // Add a series for each trader and add it to the chart
        for (Trader trader : mainApp.listOfTraders) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(trader.getName()); // Set series name to the trader's name
            traderSeriesMap.put(trader.getName(), series); // Store series in the map
            lineChart.getData().add(series); // Add series to the chart
        }
        return lineChart;
    }

    private HBox creatButtonLayout(Stage primaryStage, Boolean isphase1){
        // Create the "Start Auto Simulation" button and handle its action
        ToggleButton autoSimulateButton = new ToggleButton("Start Auto Simulation");
        autoSimulateButton.setOnAction(e -> toggleSimulation(autoSimulateButton));

        // Create other buttons and define their actions
        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> ResetButton(primaryStage, false, true));

        Button downloadResultsButton = new Button("Download Results");
        downloadResultsButton.setOnAction(e -> downloadResults());

        Button downloadChartButton = new Button("Download Chart");
        downloadChartButton.setOnAction(e -> downloadChart());

        // Back button with stop simulation functionality
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            if (simulationTimeline != null && simulationTimeline.getStatus() == Timeline.Status.RUNNING) {
                simulationTimeline.stop();
            }
            ResetButton(primaryStage, true, isphase1);
        });

        // Show trader table and show trades buttons
        Button showTableButton = new Button("Show Trader Table");
        showTableButton.setOnAction(e -> showTraderTableWindow());

         Button showTraderTradesButton = new Button("Type-based Trades");
        showTraderTradesButton.setOnAction(e -> showTraderTotalTrades());


        // Apply styles based on the phase (Phase 1 or Phase 2)
        if (!isphase1){
            autoSimulateButton.getStyleClass().add("phase2-inner-buttons");
            restartButton.getStyleClass().add("phase2-inner-buttons");
            downloadResultsButton.getStyleClass().add("phase2-inner-buttons");
            downloadChartButton.getStyleClass().add("phase2-inner-buttons");
            backButton.getStyleClass().add("phase2-inner-buttons");
            showTableButton.getStyleClass().add("phase2-inner-buttons");
            showTraderTradesButton.getStyleClass().add("phase2-inner-buttons");
        }
        else {
            autoSimulateButton.getStyleClass().add("phase1-inner-buttons");
            restartButton.getStyleClass().add("phase1-inner-buttons");
            downloadResultsButton.getStyleClass().add("phase1-inner-buttons");
            downloadChartButton.getStyleClass().add("phase1-inner-buttons");
            backButton.getStyleClass().add("phase1-inner-buttons");
            showTableButton.getStyleClass().add("phase1-inner-buttons");
            showTraderTradesButton.getStyleClass().add("phase1-inner-buttons");

        }

        // Create and return a horizontal box (HBox) containing the buttons
        HBox buttonLayout = new HBox(10, autoSimulateButton, backButton, restartButton, showTableButton,  showTraderTradesButton , downloadResultsButton, downloadChartButton);
        return buttonLayout;
    }

      private void showTraderTotalTrades() {
        // Create a new Stage (window)
        Stage tableStage = new Stage();
        tableStage.setTitle("Trader Total Trades");

        // Create a TableView
        TableView<Map.Entry<String, Integer>> traderTable = new TableView<>();
        traderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Create a column for Trader Type
        TableColumn<Map.Entry<String, Integer>, String> traderTypeColumn = new TableColumn<>("Trader Type");
        traderTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));

        // Create a column for Total Trades
        TableColumn<Map.Entry<String, Integer>, Integer> totalTradesColumn = new TableColumn<>("Total Trades");
        totalTradesColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getValue()));

        // Add the columns to the table
        traderTable.getColumns().addAll(traderTypeColumn, totalTradesColumn);

        // Populate the table with data
        traderTable.setItems(getTraderTypeTotalTradesData());

        // Create a layout and add the table
        VBox layout = new VBox(10, traderTable);
        layout.setStyle("-fx-padding: 20;");

        // Set the scene and show the stage
        Scene scene = new Scene(layout, 400, 300);
        tableStage.setScene(scene);
        tableStage.show();
    }

    // Helper method to prepare the data for the table
    private ObservableList<Map.Entry<String, Integer>> getTraderTypeTotalTradesData() {
        // Create a map to accumulate total trades by trader type
        Map<String, Integer> traderTypeTotals = new HashMap<>();
        traderTypeTotals.put("Trading Bot", 0);
        traderTypeTotals.put("Knowledgeable Trader", 0);

        // Sum up the total trades for each type
        for (Trader trader : mainApp.listOfTraders) {
            if (trader instanceof TradingBotTrader) {
                traderTypeTotals.put("Trading Bot", traderTypeTotals.get("Trading Bot") + trader.getTotalTrades());
            } else if (trader instanceof knowledgeableTrader){
                traderTypeTotals.put("Knowledgeable Trader", traderTypeTotals.get("Knowledgeable Trader") + trader.getTotalTrades());
            }
        }

        // Return the map entries as an ObservableList
        return FXCollections.observableArrayList(traderTypeTotals.entrySet());
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
        // Create a TableView for displaying traders' data
        TableView<Trader> table = new TableView<>(traderObservableList);

        // to make column resize automatically
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        // Create a column for Trader Name
        TableColumn<Trader, String> nameColumn = new TableColumn<>("Trader Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Create a column for Initial Cash, convert value to string for display
        TableColumn<Trader, String> initialCashColumn = new TableColumn<>("Starting Cash ($)");
        initialCashColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().initialCash).asString() // Directly access initialCash
        );

        // Create a column for Current Cash
        TableColumn<Trader, Double> cashColumn = new TableColumn<>("Current Cash ($)");
        cashColumn.setCellValueFactory(new PropertyValueFactory<>("cash"));

        // Create a column for Net Worth
        TableColumn<Trader, Double> netWorthColumn = new TableColumn<>("Net Worth ($)");
        netWorthColumn.setCellValueFactory(new PropertyValueFactory<>("netWorth"));

        // Create a column for Trader Type, determine based on trader class type
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

        // Add all columns to the table
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


    private TableView<Trader> createMetricTable(Boolean isPhase1) {
        // Create a TableView for displaying traders' metrics
        TableView<Trader> metricTable = new TableView<>(traderObservableList);
        // to make column resize automatically
        metricTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        metricTable.setMinHeight(150);

        // Create a column for Trader Name
        TableColumn<Trader, String> nameColumn = new TableColumn<>("Trader Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Create a column for Total Trades
        TableColumn<Trader, String> totalTradesColumn = new TableColumn<>("Total Trades");
        totalTradesColumn.setCellValueFactory(new PropertyValueFactory<>("totalTrades"));

        // Create a column for Win Ratio
        TableColumn<Trader, Double> WinColumn = new TableColumn<>("Win Ratio");
        WinColumn.setCellValueFactory(new PropertyValueFactory<>("winCount"));

        // Create a column for Loss Ratio
        TableColumn<Trader, Double> LossColumn = new TableColumn<>("Loss Ratio");
        LossColumn.setCellValueFactory(new PropertyValueFactory<>("lossCount"));

        // Create a column for Average Profit per Trade
        TableColumn<Trader, Double> AvgProfitColumn = new TableColumn<>("Avg Profit/Trade");
        AvgProfitColumn.setCellValueFactory(new PropertyValueFactory<>("averageProfit"));


        // Add all columns to the table
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

        // Set the minimum width of the table
        metricTable.setMinWidth(580);

        // Apply different styles based on the phase
        if (isPhase1){
            metricTable.getStyleClass().add("metric-table1");
        }
        else {
            metricTable.getStyleClass().add("metric-table2");
        }
        return metricTable;
    }

    private GridPane creatStockGrid() {
        // Create a new GridPane for the stock grid layout
        stockGrid = new GridPane();
        stockGrid.setHgap(10);  // Set horizontal gap between cells
        stockGrid.setVgap(10);  // Set vertical gap between cells
        stockGrid.setMaxSize(10000,100);
        stockGrid.setStyle("-fx-padding: 20;");
        stockGrid.setStyle("-fx-background-color: black; -fx-hgap: 2; -fx-vgap: 2; -fx-border-color: black; -fx-border-width: 4;");

        // Loop through each row and column to create empty labels for the grid
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 11; col++) {
                Label emptyLabel = new Label("                   ");  // Create a blank label
                emptyLabel.setFont(Font.font(12));
                emptyLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5;-fx-min-width: 110;");
                stockGrid.add(emptyLabel, col, row);  // Add the label to the grid at the specified row and column
            }
        }
        return stockGrid;
    }

    private void populateStockGrid(GridPane stockGrid, ArrayList<Stocks> stockList) {
        stockGrid.getChildren().clear();  // Clear the grid

        int column = 0;
        int row = 0;

        // Iterate through the stock list
        for (Stocks stock : stockList) {
            String formattedPrice = String.format("%.2f", stock.getPrice());

            // Check if the price is increasing or stable
            if (stock.getPrice() >= stock.getPriceHistory().get(stock.getPriceHistory().size() - 2)) {
                // Green background for price increase or stability
                Label stockLabel = new Label(stock.getSymbol() + " - $" + formattedPrice);
                stockLabel.setStyle("-fx-background-color: lightgreen; -fx-padding: 5; -fx-font-size: 12;-fx-border-color: white;-fx-border-width: 2;-fx-min-width: 110");
                stockGrid.add(stockLabel, column, row);

                column++;
            } else {
                // Red background for price decrease
                Label stockLabel = new Label(stock.getSymbol() + " - $" + formattedPrice);
                stockLabel.setStyle("-fx-background-color: #FF9999; -fx-padding: 5; -fx-font-size: 12;-fx-border-color: white;-fx-border-width: 2; -fx-min-width: 110");
                stockGrid.add(stockLabel, column, row);

                column++;  // Move to the next column
            }

            // Move to the next row if 11 columns are filled
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
        // Reinitialize the MainApp instance based on the phase
        if (isPhase1 == true)
            mainApp = new MainApp(true);  // Initialize for Phase 1
        else
            mainApp = new MainApp(false); // Initialize for Phase 2
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
        // Create a Timeline to simulate each day every 0.5 seconds
        simulationTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> simulateDay()));
        simulationTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void toggleSimulation(ToggleButton button) {
        // Check if the simulation is currently running
        if (simulationTimeline.getStatus() == Timeline.Status.RUNNING) {
            simulationTimeline.stop();  // Stop the simulation
            button.setText("Start Auto Simulation");  // Change button text to "Start"
        } else {
            simulationTimeline.play();  // Start the simulation
            button.setText("Stop Auto Simulation");  // Change button text to "Stop"
        }
    }

    private void simulateDay() {
        // Simulate events for the day and increment the day counter
        List<String> dailyEvents = mainApp.simulateDay();
        day++;
        dayCounterLabel.setText("Day: " + day);

        // Update day counter
        eventsDisplay.appendText("--- Day " + day + " ---\n");
        eventsDisplay.appendText(String.join("\n", dailyEvents) + "\n");

        double minNetWorth = Double.MAX_VALUE;
        double maxNetWorth = Double.MIN_VALUE;

        // Iterate through traders to calculate net worth and update chart
        for (Trader trader : mainApp.listOfTraders) {
            double netWorth = trader.calculateNetWorth(trader.getStockPortfolio());
            XYChart.Series<Number, Number> series = traderSeriesMap.get(trader.getName());
            if (series != null) {
                series.getData().add(new XYChart.Data<>(day, netWorth));
            }

            // Track the min and max net worth
            minNetWorth = Math.min(minNetWorth, netWorth);
            maxNetWorth = Math.max(maxNetWorth, netWorth);

            // Simulate trade results and update metrics
            boolean isWinningTrade = random.nextBoolean(); // Simulate win/loss (replace with actual logic)
            double profit = isWinningTrade ? 100 : -50;    // Simulate profit/loss
            trader.updateMetrics(isWinningTrade, profit);
        }

        // Update global min and max net worth values
        globalMinNetWorth = Math.min(globalMinNetWorth, minNetWorth);
        globalMaxNetWorth = Math.max(globalMaxNetWorth, maxNetWorth);

        // Adjust Y-axis range based on net worth values
        double padding = (globalMaxNetWorth - globalMinNetWorth) * 0.1;
        if (padding == 0) padding = 500;
        lineChart.getYAxis().setAutoRanging(false);
        NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
        yAxis.setLowerBound(Math.max(0, globalMinNetWorth - padding));
        yAxis.setUpperBound(globalMaxNetWorth + padding);

        // Update trader list and visual elements
        traderObservableList.setAll(mainApp.listOfTraders);
        ChangeCircleColors(CircleLayout);
        ArrayList<Stocks> stockList = mainApp.marketSimulator.getListStock();
        populateStockGrid(stockGrid, stockList);
        addTooltipsToChart();

        // Update metrics panel
        Label metricsTitle = new Label("Trader Performance Metrics");
        metricsTitle.setStyle("-fx-font-weight: bold;");

        for (Trader trader : traderObservableList) {
            Label traderMetrics = new Label(trader.getName() +
                    " - Total Trades: " + trader.getTotalTrades() +
                    ", Win/Loss Ratio: " + String.format("%.2f", trader.getWinLossRatio()) +
                    ", Avg Profit/Trade: $" + String.format("%.2f", trader.getAverageProfitPerTrade()));
        }
    }


    private void showTraderInfoTable(Trader trader) {
        // Create main layout for the trader info table
        VBox endLookPage= new VBox(10);
        endLookPage.setPrefSize(1700,500);

        // Info panel header with trader name and daily metrics title
        HBox infoPanel = new HBox(18); // The main panel to hold labels and data
        infoPanel.getStyleClass().add("InfoPanel");
        infoPanel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;-fx-background-color: WHITE;-fx-border-color: black;-fx-border-width: 2;");
        infoPanel.setPrefSize(100, 100);
        infoPanel.setMinSize(100,100);
        Label traderActionsTitle = new Label("Daily Metrics - Buy & Sell Advices vs Actions\n\n Trader: " + trader.getName());
        traderActionsTitle.setStyle("-fx-font-weight: bold;-fx-font-size: 18px;");
        infoPanel.getChildren().add(traderActionsTitle);
        endLookPage.getChildren().add(infoPanel);

        // Containers for Buy and Sell advice vs. actions
        HBox traderBox = new HBox(5);
        VBox buyBox = new VBox(10);
        VBox sellBox = new VBox(10);

        // Titles for Buy and Sell sections
        Label buyTitle = new Label("\tBuy Advice vs Actions:");
        buyTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: green;-fx-border-color: Black;-fx-border-width: 2;-fx-background-color: white");
        buyTitle.setPrefSize(250,60);
        buyTitle.setMinHeight(60);
        buyBox.getChildren().add(buyTitle);

        Label sellTitle = new Label("\tSell Advice vs Actions:");
        sellTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: red;-fx-border-color: Black;-fx-border-width: 2;-fx-background-color: white");
        sellTitle.setPrefSize(250,60);
        sellTitle.setMinHeight(60);
        sellBox.getChildren().add(sellTitle);
        traderBox.getChildren().addAll(buyBox, sellBox);

        // Initialize data maps for Buy and Sell advice vs. actions
        ObservableMap<String, String> buyAdviceVsAction = trader.getBuy_Advice_VS_action();
        ObservableMap<String, String> sellAdviceVsAction = trader.getSell_Advice_VS_action();

        // Listener to add new Buy advice-action pairs dynamically
        MapChangeListener<String, String> buyAdviceListener = change -> {
            if (change.wasAdded()) {
                String newAdvice = change.getKey();
                String newAction = change.getValueAdded();
                // Create label for the new Buy advice-action pair
                Label newBuyLabel = new Label("  Advice: " + newAdvice + " ||  Action: " + newAction);
                buyBox.getChildren().add(newBuyLabel);
            }
        };

        // Listener to add new Sell advice-action pairs dynamically
        MapChangeListener<String, String> sellAdviceListener = change -> {
            if (change.wasAdded()) {
                String newAdvice = change.getKey();
                String newAction = change.getValueAdded();
                // Create label for the new Sell advice-action pair
                Label newSellLabel = new Label("  Advice: " + newAdvice + " ||  Action: " + newAction);
                sellBox.getChildren().add(newSellLabel);
            }
        };

        // Apply styles to Buy and Sell boxes
        sellBox.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;-fx-border-color: Black;-fx-border-width: 2;-fx-background-color: white");

        buyBox.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;-fx-border-color: Black;-fx-border-width: 2;-fx-background-color: white");
        // Attach listeners to `buyAdviceVsAction` and `sellAdviceVsAction` maps
        buyAdviceVsAction.addListener(buyAdviceListener);
        sellAdviceVsAction.addListener(sellAdviceListener);

        // Populate initial data (if exists) for Buy
        if (!buyAdviceVsAction.isEmpty()) {
            for (Map.Entry<String, String> entry : buyAdviceVsAction.entrySet()) {
                Label existingBuyLabel = new Label("  Advice: " + entry.getKey() + " ||  Action: " + entry.getValue());
                buyBox.getChildren().add(existingBuyLabel);
            }
        }
        if (!sellAdviceVsAction.isEmpty()) {
            for (Map.Entry<String, String> entry : sellAdviceVsAction.entrySet()) {
                Label existingSellLabel = new Label("  Advice: " + entry.getKey() + " ||  Action: " + entry.getValue());
                sellBox.getChildren().add(existingSellLabel);
            }
        }

        // Add trader's box to the main info panel
        endLookPage.getChildren().add(traderBox);

        // Add a separator
        Separator separator = new Separator();
        infoPanel.getChildren().add(separator);

        // Popup Stage
        Stage popupStage = new Stage();
        popupStage.setTitle("Trader Info - Daily Updates");

        // Use ScrollPane in case of large volume of data
        ScrollPane scrollPane = new ScrollPane(endLookPage);
        scrollPane.setFitToWidth(true);
        Scene scene = new Scene(scrollPane);
        popupStage.setScene(scene);
        popupStage.show();
    }


    private HBox createTraderCircles() {
        // Layout to hold all trader circles
        CircleLayout = new HBox(10);
        CircleLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Loop through each trader in the observable list
        for (Trader trader : traderObservableList) {
            // Create a circle for the trader
            Circle circle = new Circle(100);
            circle.setFill(Color.ORANGE);

            // Add the trader's name inside the circle
            Text traderName = new Text(trader.getName());
            traderName.getStyleClass().add("circle-text");

            // Combine the circle and text into a StackPane
            StackPane traderCircle = new StackPane(circle, traderName);
            traderCircle.setStyle("-fx-alignment: center;");

            // Add the trader circle to the layout
            CircleLayout.getChildren().add(traderCircle);

            // Set double-click event to show trader info table
            traderCircle.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) { // Double-click to show metrics
                    showTraderInfoTable(trader);
                }
            });
        }

        return CircleLayout;
    }

    private void addTooltipsToChart() {
        // Iterate over all traders in the application
        for (Trader trader : mainApp.listOfTraders) {
            // Get the series corresponding to the trader
            XYChart.Series<Number, Number> series = traderSeriesMap.get(trader.getName());
            if (series != null) {
                // Iterate over all data points in the series
                for (XYChart.Data<Number, Number> data : series.getData()) {
                    Platform.runLater(() -> {
                        Node node = data.getNode(); // Get the graphical node for the data point
                        if (node != null) {
                            // Create a tooltip displaying detailed information about the data point
                            Tooltip tooltip = new Tooltip(
                                    "Day: " + data.getXValue() +
                                            "\nNet Worth: $" + String.format("%.2f", data.getYValue()) +
                                            "\nTotal Trades: " + trader.getTotalTrades() +
                                            "\nAverage Profit: $" + String.format("%.2f", trader.getAverageProfitPerTrade()) +
                                            "\nWin/Loss Ratio: " + trader.getWinCount() + ":" + trader.getLossCount()
                            );
                            Tooltip.install(node, tooltip); // Attach the tooltip to the node

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
        int traderIndex = 0; // Initialize the trader index to track positions in the circle layout

        for (Trader trader : traderObservableList) {
            ArrayList<Double> traderWorthHistory = trader.getWorthHistory(); // Get the trader's worth history

            // Check if the trader's worth history is not empty
            if (traderWorthHistory.isEmpty()) {
                continue; // Skip if there's no worth history
            }

            // Ensure the index is within bounds
            if (traderIndex < circleLayout.getChildren().size()) {
                if (circleLayout.getChildren().get(traderIndex) instanceof StackPane) {
                    StackPane traderCircle = (StackPane) circleLayout.getChildren().get(traderIndex);

                    for (Node node : traderCircle.getChildren()) {
                        if (node instanceof Circle) {
                            // Check if the last worth history value is available
                            if (traderWorthHistory.size() > 1) {
                                if (traderWorthHistory.get(traderWorthHistory.size() - 1) >= traderWorthHistory.get(traderWorthHistory.size() - 2)) {
                                    Circle circle = (Circle) node;
                                    circle.setFill(Color.GREEN);
                                } else {
                                    Circle circle = (Circle) node;
                                    circle.setFill(Color.RED);
                                }
                            }
                        }
                    }
                }
            }

            traderIndex++;
        }
    }




    private void showTraderTableWindow() {
        // Create a new stage for displaying the trader table
        Stage tableStage = new Stage();
        tableStage.setTitle("Trader Details");

        // Create a TableView containing trader data
        TableView<Trader> traderTable = createTraderTable();

        // Add the table to a VBox layout
        VBox layout = new VBox(traderTable);
        layout.setStyle("-fx-padding: 20;");

        // Configure the scene with the VBox layout
        Scene scene = new Scene(layout, 800, 600);

        // Attach the scene to the stage and display it
        tableStage.setScene(scene);
        tableStage.show();
    }

    private void downloadResults() {
        // Create a FileChooser for saving the results
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Results");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", ".csv"));

        // Show the save dialog and get the chosen file
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                // Write the header row for the CSV file
                writer.println("Trader Name,Net Worth");

                // Write trader data to the CSV file
                for (Trader trader : mainApp.listOfTraders) {
                    writer.printf("%s,%.2f%n", trader.getName(), trader.getNetWorth());
                }

                // Show a success alert to the user
                showAlert("Results saved successfully!");

            } catch (IOException e) {
                // Print stack trace and show error alert
                e.printStackTrace();
            }
        }

    } private void downloadChart() {
        // Create a FileChooser for saving the chart
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Chart");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", ".png"));

        // Show the save dialog and get the chosen file
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
