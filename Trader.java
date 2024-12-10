
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public abstract class Trader {
    private String name; // Trader's name
    private double cash; // Available cash for trading
    protected HashMap<Stocks, Integer> stockPortfolio; // Stock portfolio mapping stock to quantity owned
    private double netWorth; // Current net worth (cash + stock value)
    private ArrayList<Double> worthHistory; // History of net worth over time
    public double initialCash = 10000; // Initial cash allocated to the trader
    private String[] excuses; // List of excuses for human errors
    private Random random; // Random generator for simulating probabilities
    protected ObservableMap<String, String> Buy_Advice_VS_action;
    protected ObservableMap<String, String> Sell_Advice_VS_action;
    public int dailyTradeCount = 0;

    // Performance Metrics
    private int totalTrades = 0; // Total number of trades
    private double winLossRatio;
    private double averageProfit=0;
    private int winCount = 0; // Total number of winning trades
    private int lossCount = 0; // Total number of losing trades
    private double totalProfit = 0; // Total profit from trades

    public Trader(String name, MarketSimulator market) {
        this.name = name;
        this.cash = initialCash;
        this.Buy_Advice_VS_action = FXCollections.observableHashMap();
        this.Sell_Advice_VS_action = FXCollections.observableHashMap();
        this.stockPortfolio = new HashMap<>();
        this.worthHistory = new ArrayList<>();
        this.random = new Random();
        this.totalTrades = 0;
        this.winCount = 0;
        this.lossCount = 0;
        this.totalProfit = 0.0;
        initializeStockPortfolio(market); // Initialize portfolio with random stocks
    }

    // Calculate and return the trader's cash (rounded to 2 decimal places)
    public double getCash() {
        cash = Math.round(cash * 100.0) / 100.0;
        return cash;
    }

    // Calculate and return the trader's net worth (rounded to 2 decimal places)
    public double getNetWorth() {
        return Math.round(netWorth * 100.0) / 100.0;
    }

    // Return the history of the trader's net worth over time
    public ArrayList<Double> getWorthHistory() {
        return worthHistory;
    }

    // Buy a stock with human error simulation
    public boolean buy(Stocks stock, int quantity, double price) {

        double totalCost = price * quantity;
        if (cash >= totalCost) {
            cash -= totalCost;
            stockPortfolio.put(stock, stockPortfolio.getOrDefault(stock, 0) + quantity);
            totalTrades++;
            return true; // Successful purchase
        } else {
            return false; // Not enough cash
        }
    }

    // Sell a stock with human error simulation
    public boolean sell(Stocks stock, int quantity, double price) {
        if (!stockPortfolio.containsKey(stock)) {
            System.out.println("Stock " + stock.getSymbol() + " is not in the portfolio.");
            return false; // Stock not in portfolio
        }

        int availableQuantity = stockPortfolio.get(stock);
        if (availableQuantity < quantity) {
            System.out.println("Not enough quantity of stock " + stock.getSymbol() + " to sell.");
            return false; // Insufficient stock quantity
        }

        // Calculate total revenue and update cash
        double totalRevenue = price * quantity;
        double buyPrice = stock.getPrice(); // Replace with actual buy price if available
        double profit = totalRevenue - (buyPrice * quantity);

        cash += totalRevenue;
        stockPortfolio.put(stock, availableQuantity - quantity);

        if (stockPortfolio.get(stock) == 0) {
            stockPortfolio.remove(stock); // Remove stock if no quantity left
        }

        // Update metrics
        if (profit > 0) {
            winCount++;
            totalProfit += profit;
        } else {
            lossCount++;
        }

        totalTrades++;
        return true; // Successful sale
    }
    // Calculate net worth
    public double calculateNetWorth(HashMap<Stocks, Integer> stockPortfolio) {
        netWorth = cash;
        for (Stocks stock : stockPortfolio.keySet()) {
            double stockPrice = stock.getPrice();
            netWorth += stockPrice * stockPortfolio.get(stock);
        }
        worthHistory.add(netWorth);
        return Math.round(netWorth * 100.0) / 100.0; // Round to two decimal places
    }

    public HashMap<Stocks, Integer> getStockPortfolio() {
        return stockPortfolio;
    }

    public ObservableMap<String, String> getBuy_Advice_VS_action() {
        return Buy_Advice_VS_action;
    }

    public ObservableMap<String, String> getSell_Advice_VS_action() {
        return Sell_Advice_VS_action;
    }


    public abstract double calculate(int period, List<Double> priceHistory);

    public abstract void execute(MarketSimulator market,Stocks stock, int quantity);



    public String getName() {
        return name;
    }

    // Updates metrics based on the result of a trade
    public void updateMetrics(boolean isWinningTrade, double profit) {
        totalTrades++;
        if (isWinningTrade) {
            winCount++;
            totalProfit += profit;
        } else {
            lossCount++;
        }
    }

    // Get total trades
    public int getTotalTrades() {
        return totalTrades;
    }

    // Get win/loss ratio
    public double getWinLossRatio() {

        if (lossCount == 0) {
            return winCount ; // Avoid division by zero
        }

        return  (double) winCount / lossCount;
    }
    // Get average profit per trade
    public double getAverageProfitPerTrade() {
        if (totalTrades == 0) {
            return averageProfit; // Avoid division by zero
        }

        return averageProfit = totalProfit / totalTrades;
    }

    public double getAverageProfit() {
        averageProfit = Math.round(averageProfit * 100.0)/100.0 ;
        return averageProfit;
    }

    public int getWinCount() {
        return winCount;
    }

    public int getLossCount() {
        return lossCount;
    }

    // Initialize stock portfolio with random stocks from the market
    public void initializeStockPortfolio(MarketSimulator market) {
        int numberOfRandomStocks = 10; // Number of stocks to add
        List<Stocks> listOfStocks = market.getRandomStocks(numberOfRandomStocks);
        for (Stocks stock : listOfStocks) {
            stockPortfolio.put(stock, stockPortfolio.getOrDefault(stock, 0) + 1);
        }
    }

    public String randomExcuses() {
        this.excuses = new String[] {
                this.name + " experienced an internet connection failure.",
                this.name + " couldn't trade because the trading platform was temporarily unavailable.",
                this.name + "'s system crashed during the trading process.",
                this.name + " got distracted by a personal event.",
                this.name + " hesitated, doubting the reliability of their strategy.",
                this.name + " forgot to place the order due to being sidetracked.",
                this.name + " missed the opportunity due to overthinking.",
                this.name + " was stopped by fear of loss.",
                this.name + " waited too long, overconfident about finding a better opportunity.",
                "An emergency prevented " + this.name + " from executing the trade.",
                "A power outage stopped " + this.name + " from accessing the trading platform.",
                this.name + " missed the opportunity due to a delay."
        };
        String randomExcuse = excuses[(int) (Math.random() * excuses.length)];
        return randomExcuse;
    }

    public void resetDailyTradeCount() {
        dailyTradeCount = 0;
    }

}
