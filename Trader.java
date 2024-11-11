// Trader.java

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public boolean buy(Stocks stock, int quantity, double price) {      // Set quanitity to 1 (in phase 1)
        double totalCost = price * quantity;
        if (cash >= totalCost) {
            cash -= totalCost;
            stockPortfolio.put(stock, stockPortfolio.getOrDefault(stock, 0) + quantity);
            return true;
        } else {
            System.out.println("Not enough cash to buy " + quantity + " of " + stock.getSymbol());
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
            System.out.println("Not enough stock to sell " + quantity + " of " + stock.getSymbol());
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


    public HashMap<Stocks, Integer> getStockPortfolio() {
        return stockPortfolio;
    }

    public abstract double calculate(int period, List<Double> priceHistory);

    public abstract void execute(Stocks stock, int quantity);

    public String getName() {
        return name;
    }

}