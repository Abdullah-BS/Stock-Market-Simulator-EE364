// Trader.java

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Trader {
    private String name;
    private double cash;
    protected HashMap<Stocks, Integer> stockPortfolio;
    private double netWorth;
    private ArrayList<Double> worthHistory;
    public double initialCash = 10000;

    public Trader(String name, MarketSimulator market) {
        this.name = name;
        this.cash = initialCash;
        this.stockPortfolio = new HashMap<>();
        this.worthHistory = new ArrayList<>();
        initializeStockPortfolio(market);
    }

    public double getCash() {
        cash = Math.round(cash * 100.0) / 100.0;
        return cash;
    }

    public double getNetWorth() {
        return Math.round(netWorth * 100.0) / 100.0;

    }

    public ArrayList<Double> getWorthHistory() {
        return worthHistory;
    }

    public boolean buy(Stocks stock, int quantity, double price) {      // Set quanitity to 1 (in phase 1)
//        System.out.println(price);
        double totalCost = price * quantity;
//        System.out.println(totalCost);
        if (cash >= totalCost) {
//            System.out.println(cash);
            cash -= totalCost;
//            System.out.println(cash);
            stockPortfolio.put(stock, stockPortfolio.getOrDefault(stock, 0) + quantity);
            return true;
        } else {
//            System.out.println("Not enough cash to buy " + quantity + " of " + stock.getSymbol() + "  "+getName()+ "   "+ cash+"  "+ stock.getPrice());
            return false;
        }
    }

    public boolean sell(Stocks stock, int quantity, double price) {

        if (stockPortfolio.containsKey(stock)) {

            do {
                if(getStockPortfolio().get(stock) >= quantity) {
//                    System.out.println("Action: Sell stock, price is significantly above the RSI threshold.");

                    double totalRevenue = price * quantity;
                    cash += totalRevenue;
                    stockPortfolio.put(stock, stockPortfolio.get(stock) - quantity);
//                    System.out.println(stockPortfolio.get(stock));

                    if (stockPortfolio.get(stock) == 0) {
                    stockPortfolio.remove(stock);
                }

                    return true;

                }

                quantity = quantity - 1;
            } while(true);



        } else {
            System.out.println("Stock " + stock.getSymbol() + " is not in the portfolio.");
            return false;       
        }
    }

    public double calculateNetWorth(HashMap<Stocks, Integer> stockPortfolio) {
        netWorth = cash;
        for (Stocks stock : stockPortfolio.keySet()) {
            double stockPrice = stock.getPrice();
//            System.out.println(stockPrice);
            netWorth += stockPrice * stockPortfolio.get(stock);
        }
        worthHistory.add(netWorth);
//        System.out.println(netWorth);
//        System.out.println(worthHistory);
        return Math.round(netWorth * 100.0) / 100.0;

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


    public void initializeStockPortfolio(MarketSimulator market) {

        int numberOfRandomStocks = 10;
        List<Stocks> listOfStocks = market.getRandomStocks(numberOfRandomStocks);
//        System.out.println("Number of random stocks: " + listOfStocks.size());
        for (Stocks stock : listOfStocks) {
//            System.out.println("Processing stock: " + stock);
//            System.out.println("Current quantity in portfolio: " + stockPortfolio.getOrDefault(stock, 0));
            stockPortfolio.put(stock, stockPortfolio.getOrDefault(stock, 0) + 1);
//            System.out.println("problem????");




        }
//        System.out.println("Updated quantity in Trader portfolio: " + stockPortfolio);
    }


}

