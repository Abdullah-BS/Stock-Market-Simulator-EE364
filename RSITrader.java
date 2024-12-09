import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RSITrader extends Trader implements knowledgeableTrader {

    private int period;
    private static final double STOP_LOSS_PERCENTAGE = 0.10; // 10% stop-loss
    private static final double PROFIT_GRAB_PERCENTAGE = 0.35; // 15% profit-grab

    public RSITrader(String name, int period, MarketSimulator market) {
        super(name, market);
        this.period = period;
    }

    @Override
    public double calculate(int period, List<Double> priceHistory) {
        // Ensure there is enough data for calculation
        try {
            double gain = 0;
            double loss = 0;

            for (int j = 1; j < period; j++) {
                double change = priceHistory.get(j) - priceHistory.get(j - 1);
                if (change > 0) {
                    gain += change;
                } else {
                    loss -= change;
                }
            }

            double avgGain = gain / period;
            double avgLoss = loss / period;

            return avgLoss == 0 ? 100 : 100 - (100 / (1 + (avgGain / avgLoss)));
        } catch (Exception e) {
            System.out.println("Error calculating RSI: " + e.getMessage());
            return 50; // Return neutral RSI value on error
        }
    }

    public void execute(Stocks stock, int quantity) {
        double random = Math.random();
        String advice;
        String action;

        // Step 1: Calculate RSI for the current stock
        List<Double> priceHistory = stock.getPriceHistory();
        double RSI = calculate(this.period, priceHistory);
        double currentPrice = stock.getPrice();

        // Step 2: Determine advice based on RSI
        if (RSI < 30) {
            advice = "Buy";
        } else if (RSI > 70) {
            advice = "Sell";
        } else {
            advice = "Hold";
        }


        // Step 3: Random excuses or actual execution
        if (random < 0.3) {
            System.out.println(randomExcuses());
            action = randomExcuses();
        }

        //BUY
        else if (advice.equals("Buy")) {
                do{
                    if (getCash() >= quantity * currentPrice){
                        buy(stock, quantity, currentPrice);
                        action = "Bought " + quantity + " units of " + stock.getSymbol() +
                                " at price " + currentPrice;
                        System.out.println(this.getName() + ": Bought " + quantity + " units of " + stock.getSymbol() +
                                " at price " + currentPrice);
                        break;
                    }
                    quantity--; // Reduce quantity by 1

                    if (quantity <= 0) {
                        System.out.println(this.getName() + ": Not enough cash to buy stock " + stock.getSymbol());
                        action = "Not enough cash to buy stock " + stock.getSymbol();
                        break;
                    }
                } while (true);
                }

        //SELL
        else if (advice.equals("Sell") && getStockPortfolio().containsKey(stock)) {
            do {
                if (getStockPortfolio().get(stock) >= quantity) {
                        sell(stock, quantity, currentPrice);

                        action = "Sold " + quantity + " units of " + stock.getSymbol() +
                                " at price " + currentPrice;
                        System.out.println(this.getName() + ": Sold " + quantity + " units of " + stock.getSymbol() +
                                " at price " + currentPrice);
                        break;
                }
                quantity--;

                if (quantity <= 0) {
                    System.out.println("Not enough stock to sell.");
                    action = "Not enough stock to sell";
                    break;
                }
            } while (true);
        }
        else {
            action = "Hold";
            System.out.println(this.getName() + ": Hold stock, price is within the threshold range of the moving average.");
        }


        // Step 4: Stop-loss and profit-grab logic
        applyStopLossAndProfitGrab(currentPrice ,quantity);

        // Step 5: Store advice and action in the hashmap
        advice_VS_action.put(advice, action);
}

    // Helper method to process stop-loss and profit-grab logic
    private void applyStopLossAndProfitGrab(double currentPrice, int quantity) {
        Map<Stocks, Integer> portfolioCopy = new HashMap<>(getStockPortfolio());
        for (Map.Entry<Stocks, Integer> entry : portfolioCopy.entrySet()) {
            Stocks portfolioStock = entry.getKey();
            int ownedQuantity = entry.getValue();
            double purchasePrice = portfolioStock.getPrice(); // Assuming we track purchase price
            double profitPercentage = (currentPrice - purchasePrice) / purchasePrice;

            // Stop-loss logic
            if (profitPercentage <= -STOP_LOSS_PERCENTAGE) {
                sell(portfolioStock, ownedQuantity, currentPrice);
                System.out.println(this.getName() + ": Sold (Stop Loss) " + ownedQuantity + " units of " +
                        portfolioStock.getSymbol());
            }
            // Profit-grab logic
            else if (profitPercentage >= PROFIT_GRAB_PERCENTAGE) {
                sell(portfolioStock, ownedQuantity, currentPrice);
                System.out.println(this.getName() + ": Sold (Profit Grab) " + ownedQuantity + " units of " +
                        portfolioStock.getSymbol());
            }
        }
        double maxRSI = 0.0;
        Stocks stockToSell = null;

        for (Stocks portfolioStock : portfolioCopy.keySet()) {
            List<Double> portfolioPriceHistory = portfolioStock.getPriceHistory();
            if (portfolioPriceHistory.size() >= this.period) {
                double rsi = calculate(this.period, portfolioPriceHistory);
                if (rsi > maxRSI) {
                    maxRSI = rsi;
                    stockToSell = portfolioStock;
                }
            }
        }

        if (stockToSell != null && maxRSI > 70) {
            int ownedQuantity = getStockPortfolio().getOrDefault(stockToSell, 0);
            if (ownedQuantity >= quantity) {
                sell(stockToSell, quantity, stockToSell.getPrice());
                System.out.println(this.getName() + ": Sold " + quantity + " units of " + stockToSell.getSymbol() +
                        " at price " + stockToSell.getPrice());
            } else {
                System.out.println(this.getName() + ": Not enough stock to sell.");
            }
        }
    }

    @Override
    public String getName() {
        return super.getName() + " (RSI Strategy)";
    }
}
