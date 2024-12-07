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
    private List<String> excuses; // List of excuses for human errors
    private List<Double> probabilities; // Corresponding probabilities for each excuse
    private double errorProbability = 0.1; // Probability of a human error (10%)
    private Random random; // Random generator for simulating probabilities

    // Performance Metrics
    private int totalTrades = 0; // Total number of trades
    private int winCount = 0; // Total number of winning trades
    private int lossCount = 0; // Total number of losing trades
    private double totalProfit = 0; // Total profit from trades

    public Trader(String name, MarketSimulator market) {
        this.name = name;
        this.cash = initialCash;
        this.stockPortfolio = new HashMap<>();
        this.worthHistory = new ArrayList<>();
        this.excuses = new ArrayList<>();
        this.probabilities = new ArrayList<>();
        this.random = new Random();
        this.totalTrades = 0;
        this.winCount = 0;
        this.lossCount = 0;
        this.totalProfit = 0.0;
        initializeStockPortfolio(market); // Initialize portfolio with random stocks
        loadHumanErrorsFromCSV("human_trading_errors.csv"); // Load errors from CSV
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

    // Load human errors and probabilities from a CSV file
    private void loadHumanErrorsFromCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip the header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    excuses.add(parts[0]); // Add the excuse
                    probabilities.add(Double.parseDouble(parts[1])); // Add the corresponding probability
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading human errors file: " + e.getMessage());
        }
    }

    // Get a random excuse based on defined probabilities
    public String randomExcuses() {
        double randomValue = random.nextDouble();
        double cumulativeProbability = 0.0;
        for (int i = 0; i < excuses.size(); i++) {
            cumulativeProbability += probabilities.get(i);
            if (randomValue <= cumulativeProbability) {
                return excuses.get(i); // Return the selected excuse
            }
        }
        return "Unknown error occurred.";
    }

    // Buy a stock with human error simulation
    public boolean buy(Stocks stock, int quantity, double price) {
        if (random.nextDouble() < errorProbability) {
            System.out.println(randomExcuses());
            return false; // Buying failed due to human error
        }

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
        if (random.nextDouble() < errorProbability) {
            System.out.println(randomExcuses());
            return false; // Selling failed due to human error
        }

        if (stockPortfolio.containsKey(stock)) {
            do {
                if (getStockPortfolio().get(stock) >= quantity) {
                    double totalRevenue = price * quantity;
                    double profit = totalRevenue - (stock.getPrice() * quantity);
                    cash += totalRevenue;
                    stockPortfolio.put(stock, stockPortfolio.get(stock) - quantity);

                    if (profit > 0) {
                        winCount++;
                        totalProfit += profit;
                    } else {
                        lossCount++;
                    }

                    if (stockPortfolio.get(stock) == 0) {
                        stockPortfolio.remove(stock); // Remove stock if no quantity left
                    }

                    totalTrades++;
                    return true; // Successful sale
                }
                quantity--; // Reduce quantity if insufficient stocks are available
            } while (true);
        } else {
            System.out.println("Stock " + stock.getSymbol() + " is not in the portfolio.");
            return false; // Stock not in portfolio
        }
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

    public abstract double calculate(int period, List<Double> priceHistory);

    public abstract void execute(Stocks stock, int quantity);

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
            return winCount; // Avoid division by zero
        }
        return (double) winCount / lossCount;
    }
    
    // Get average profit per trade
    public double getAverageProfitPerTrade() {
        if (totalTrades == 0) {
            return 0; // Avoid division by zero
        }
        return totalProfit / totalTrades;
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
}
