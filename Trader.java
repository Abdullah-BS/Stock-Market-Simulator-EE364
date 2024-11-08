// Trader.java

import java.util.HashMap;
import java.util.ArrayList;

public abstract class Trader {
    private String name;
    private double cash;
    private HashMap<Stocks, Integer> stockPortfolio;
    private double netWorth;
    private ArrayList<Double> worthHistory;

    public Trader(String name, double cash) {
        this.name = name;
        this.cash = cash;
        this.stockPortfolio = new HashMap<>();
        this.worthHistory = new ArrayList<>();
    }

    public boolean buy(Stocks stock, int quantity, double price) {
        double totalCost = price * quantity;
        if (cash >= totalCost) {
            cash -= totalCost;
            stockPortfolio.put(stock, stockPortfolio.getOrDefault(stock, 0) + quantity);
            return true;
        } else {
            System.out.println("Not enough cash to buy " + quantity + " of " + stock);
            return false;
        }
    }

    public boolean sell(Stocks stock, int quantity, double price) {
        if (stockPortfolio.containsKey(stock) && stockPortfolio.get(stock) >= quantity) {
            double totalRevenue = price * quantity;
            cash += totalRevenue;
            stockPortfolio.put(stock, stockPortfolio.get(stock) - quantity);
            
            if (stockPortfolio.get(stock) == 0) {
                stockPortfolio.remove(stock);
            }
            return true;

        } else {
            System.out.println("Not enough stock to sell " + quantity + " of " + stock);
            return false;       
        }
    }

    public double calculateNetWorth(HashMap<Stocks, Integer> stockPortfolio) {
        netWorth = cash;
        for (Stocks stock : stockPortfolio.keySet()) {
            double stockPrice = stock.getPrice();
            netWorth += stockPrice * stockPortfolio.get(stock);
        }
        worthHistory.add(netWorth);
        return netWorth;
    }

    // public void trade(String stock, int quantity, double price, boolean isBuying) {
    //     if (isBuying) {
    //         buy(stock, quantity, price);
    //     } else {
    //         sell(stock, quantity, price);
    //     }
    // }

    public abstract double calculate();

    public abstract void execute();

    public String getName() {
        return name;
    }

}

