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
    private String[] excuses;


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

    public boolean buy(Stocks stock, int quantity, double price) {

        double totalCost = price * quantity;

        if (cash >= totalCost) {

            cash -= totalCost;

            stockPortfolio.put(stock, stockPortfolio.getOrDefault(stock, 0) + quantity);
            return true;
        } else {
            return false;
        }
    }

    public boolean sell(Stocks stock, int quantity, double price) {

        if (stockPortfolio.containsKey(stock)) {

            do {
                if(getStockPortfolio().get(stock) >= quantity) {

                    double totalRevenue = price * quantity;
                    cash += totalRevenue;
                    stockPortfolio.put(stock, stockPortfolio.get(stock) - quantity);

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
            netWorth += stockPrice * stockPortfolio.get(stock);
        }
        worthHistory.add(netWorth);
        return Math.round(netWorth * 100.0) / 100.0;

    }




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
}

