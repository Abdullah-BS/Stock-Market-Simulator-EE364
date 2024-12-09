import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RSITrader extends Trader implements knowledgeableTrader {

    private int period;
    private static final double STOP_LOSS_PERCENTAGE = 0.10; // 10% stop-loss
    private static final double PROFIT_GRAB_PERCENTAGE = 0.35; // 35% profit-grab
    private static final int MAX_TRADES_PER_DAY = 3; // Max trades allowed per day
    private int dailyTradeCount = 0; // Counter for daily trades

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

    @Override
    public void execute(Stocks stock, int quantity) {
        if (dailyTradeCount >= MAX_TRADES_PER_DAY) {
            System.out.println(this.getName() + ": Daily trade limit reached.");
            return;
        }

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

        // BUY
        else if (advice.equals("Buy")) {
            if (getCash() >= quantity * currentPrice) {
                buy(stock, quantity, currentPrice);
                dailyTradeCount++;
                action = "Bought " + quantity + " units of " + stock.getSymbol() +
                        " at price " + currentPrice;
                System.out.println(this.getName() + ": Bought " + quantity + " units of " + stock.getSymbol() +
                        " at price " + currentPrice);
            } else {
                action = "Not enough cash to buy stock";
                System.out.println(this.getName() + ": Not enough cash to buy stock " + stock.getSymbol());
            }
        }

        // SELL
        else if (advice.equals("Sell") && getStockPortfolio().containsKey(stock)) {
            int ownedQuantity = getStockPortfolio().getOrDefault(stock, 0);
            if (ownedQuantity >= quantity) {
                sell(stock, quantity, currentPrice);
                dailyTradeCount++;
                action = "Sold " + quantity + " units of " + stock.getSymbol() +
                        " at price " + currentPrice;
                System.out.println(this.getName() + ": Sold " + quantity + " units of " + stock.getSymbol() +
                        " at price " + currentPrice);
            } else {
                action = "Not enough stock to sell";
                System.out.println(this.getName() + ": Not enough stock to sell.");
            }
        } else {
            action = "Hold";
            System.out.println(this.getName() + ": Holding stock.");
        }

        // Step 4: Stop-loss and profit-grab logic
        applyStopLossAndProfitGrab();

        // Step 5: Store advice and action in the hashmap
        advice_VS_action.put(advice, action);
    }

    // Helper method to process stop-loss and profit-grab logic
    private void applyStopLossAndProfitGrab() {
        Map<Stocks, Integer> portfolioCopy = new HashMap<>(getStockPortfolio());

        for (Map.Entry<Stocks, Integer> entry : portfolioCopy.entrySet()) {
            if (dailyTradeCount >= MAX_TRADES_PER_DAY) break;

            Stocks portfolioStock = entry.getKey();
            int ownedQuantity = entry.getValue();
            double currentPrice = portfolioStock.getPrice();
            double purchasePrice = portfolioStock.getPriceHistory().get(0); // Assume first price is purchase price
            double profitPercentage = (currentPrice - purchasePrice) / purchasePrice;

            // Stop-loss logic
            if (profitPercentage <= -STOP_LOSS_PERCENTAGE) {
                sell(portfolioStock, ownedQuantity, currentPrice);
                dailyTradeCount++;
                System.out.println(this.getName() + ": Sold (Stop Loss) " + ownedQuantity + " units of " +
                        portfolioStock.getSymbol() + " at price " + currentPrice);
            }
            // Profit-grab logic
            else if (profitPercentage >= PROFIT_GRAB_PERCENTAGE) {
                sell(portfolioStock, ownedQuantity, currentPrice);
                dailyTradeCount++;
                System.out.println(this.getName() + ": Sold (Profit Grab) " + ownedQuantity + " units of " +
                        portfolioStock.getSymbol() + " at price " + currentPrice);
            }
        }
    }

    public void resetDailyTradeCount() {
        dailyTradeCount = 0;
    }

    @Override
    public String getName() {
        return super.getName() + " (RSI Strategy with Trade Limit)";
    }
}
