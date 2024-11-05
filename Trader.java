// Trader.java

import java.util.HashMap;
import java.util.ArrayList;

public class Trader {
    private String name;
    private double cash;
    private HashMap<String, Integer> stockPortfolio;
    private double netWorth;
    private ArrayList<Double> worthHistory;

    public Trader(String name, double cash) {
        this.name = name;
        this.cash = cash;
        this.stockPortfolio = new HashMap<>();
        this.worthHistory = new ArrayList<>();
    }

    public void buy(String stock, int quantity, double price) {
        double totalCost = price * quantity;
        if (cash >= totalCost) {
            cash -= totalCost;
            stockPortfolio.put(stock, stockPortfolio.getOrDefault(stock, 0) + quantity);
        } else {
            System.out.println("Not enough cash to buy " + quantity + " of " + stock);
        }
    }

    public void sell(String stock, int quantity, double price) {
        if (stockPortfolio.containsKey(stock) && stockPortfolio.get(stock) >= quantity) {
            double totalRevenue = price * quantity;
            cash += totalRevenue;
            stockPortfolio.put(stock, stockPortfolio.get(stock) - quantity);
            if (stockPortfolio.get(stock) == 0) {
                stockPortfolio.remove(stock);
            }
        } else {
            System.out.println("Not enough stock to sell " + quantity + " of " + stock);
        }
    }

    public double calculateNetWorth() {
        netWorth = cash;
        for (String stock : stockPortfolio.keySet()) {
            double stockPrice = getStockPrice(stock);
            netWorth += stockPrice * stockPortfolio.get(stock);
        }
        worthHistory.add(netWorth);
        return netWorth;
    }

    public void trade(String stock, int quantity, double price, boolean isBuying) {
        if (isBuying) {
            buy(stock, quantity, price);
        } else {
            sell(stock, quantity, price);
        }
    }

    public double calculate() {
        return calculateNetWorth();
    }

    public void execute(String stock, int quantity, double price, boolean isBuying) {
        trade(stock, quantity, price, isBuying);
        System.out.println("Executed " + (isBuying ? "buy" : "sell") + " for " + quantity + " of " + stock + " at price " + price);
    }

    public String getName() {
        return name;
    }

}

