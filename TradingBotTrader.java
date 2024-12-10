import java.util.*;

public class TradingBotTrader extends Trader {

    private int period;
    private static final double STOP_LOSS_PERCENTAGE = 0.10; // 10% stop-loss
    private static final double PROFIT_GRAB_PERCENTAGE = 0.35; // 35% profit-grab
    private Map<Stocks, Double> highestPriceTracker; // Track the highest price of each stock

    public TradingBotTrader(String name, int period, MarketSimulator market) {
        super(name, market);
        this.period = period;
        this.highestPriceTracker = new HashMap<>();
    }

    @Override
    public double calculate(int period, List<Double> priceHistory) {
        try {
            if (priceHistory.size() < period) {
                System.out.println("Not enough data for RSI calculation.");
                return 50.0; // Neutral RSI
            }

            double gain = 0;
            double loss = 0;

            for (int j = priceHistory.size() - period + 1; j < priceHistory.size(); j++) {
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
            return 50.0; // Return neutral RSI value on error
        }
    }

    @Override
    public void execute(MarketSimulator market, Stocks stock, int quantity) {
        List<Stocks> allStocks = market.getListStock();
        Map<Stocks, Double> stockScores = new HashMap<>();

        // Evaluate all stocks in the market
        for (Stocks s : allStocks) {
            List<Double> priceHistory = s.getPriceHistory();
            if (priceHistory.size() >= period) {
                double rsi = calculate(this.period, priceHistory);
                double momentum = priceHistory.get(priceHistory.size() - 1) - priceHistory.get(0); // Entire history momentum
                double score = rsi - momentum; // Combine RSI and momentum
                stockScores.put(s, score);
            }
        }

        // Sort stocks based on their scores (ascending, lower RSI and higher momentum preferred)
        List<Map.Entry<Stocks, Double>> sortedStocks = new ArrayList<>(stockScores.entrySet());
        sortedStocks.sort(Map.Entry.comparingByValue());

        // Buy top stocks
        for (Map.Entry<Stocks, Double> entry : sortedStocks) {
            Stocks topStock = entry.getKey();
            double currentPrice = topStock.getPrice();
            double rsi = calculate(this.period, topStock.getPriceHistory());

            // Buy if RSI indicates opportunity and cash is available
            if (rsi < 50 && getCash() > currentPrice) {
                int buyQuantity = Math.min(quantity, (int) (getCash() / currentPrice));
                buy(topStock, buyQuantity, currentPrice);
                highestPriceTracker.put(topStock, currentPrice); // Track the highest price
                System.out.println(this.getName() + ": Bought " + buyQuantity + " units of " + topStock.getSymbol() +
                        " at price $" + String.format("%.2f", currentPrice) + " (RSI: " + rsi + ")");
            }
        }

        // Apply stop-loss and profit-grab logic for the portfolio
        applyStopLossAndProfitGrab();
    }

    private void applyStopLossAndProfitGrab() {
        Map<Stocks, Integer> portfolioCopy = new HashMap<>(getStockPortfolio());
        for (Map.Entry<Stocks, Integer> entry : portfolioCopy.entrySet()) {
            Stocks stock = entry.getKey();
            int ownedQuantity = entry.getValue();
            List<Double> priceHistory = stock.getPriceHistory();

            if (priceHistory.isEmpty()) continue;

            double currentPrice = stock.getPrice();

            // Update the highest price tracker
            highestPriceTracker.put(stock, Math.max(highestPriceTracker.getOrDefault(stock, 0.0), currentPrice));

            double highestPrice = highestPriceTracker.get(stock);
            double purchasePrice = priceHistory.get(0); // Assuming the first price is the purchase price
            double profitPercentage = (currentPrice - purchasePrice) / purchasePrice;
            double dropFromHighest = (currentPrice - highestPrice) / highestPrice;

            // Stop-loss logic: Sell if current price drops significantly
            if (profitPercentage <= -STOP_LOSS_PERCENTAGE || dropFromHighest <= -STOP_LOSS_PERCENTAGE) {
                sell(stock, ownedQuantity, currentPrice);
                highestPriceTracker.remove(stock); // Reset the tracker after selling
                System.out.println(this.getName() + ": Sold (Stop Loss) " + ownedQuantity +
                        " units of " + stock.getSymbol() + " at price $" + String.format("%.2f", currentPrice));
            }

            // Profit-grab logic
            if (profitPercentage >= PROFIT_GRAB_PERCENTAGE) {
                sell(stock, ownedQuantity, currentPrice);
                highestPriceTracker.remove(stock); // Reset the tracker after selling
                System.out.println(this.getName() + ": Sold (Profit Grab) " + ownedQuantity +
                        " units of " + stock.getSymbol() + " at price $" + String.format("%.2f", currentPrice));
            }
        }
    }

    @Override
    public String getName() {
        return super.getName() + " (Improved Trading Bot)";
    }
}
