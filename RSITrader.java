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

    @Override
    public void execute(Stocks stock, int quantity) {
        double random = Math.random();
        if (random < 0.3) {
            System.out.println(randomExcuses());
        } else {
            // Step 1: Calculate RSI for the current stock
            List<Double> priceHistory = stock.getPriceHistory();
            double buyRSI = calculate(this.period, priceHistory);
            double currentPrice = stock.getPrice();

            // Step 2: Buy if RSI is below 30 and cash is sufficient
            if (buyRSI < 30) {
                if (getCash() >= quantity * currentPrice) {
                    buy(stock, quantity, currentPrice);
                    System.out.println(this.getName() + ": Bought " + quantity + " units of " + stock.getSymbol() +
                            " at price " + currentPrice);
                } else {
                    System.out.println(this.getName() + " does not have enough cash to buy stock " + stock.getSymbol());
                }
            }

            // Step 3: Make a copy of the portfolio to safely iterate
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

            // Step 4: Sell stocks with RSI > 70 if sufficient quantity
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
    }

    @Override
    public String getName() {
        return super.getName() + " (RSI Strategy)";
    }
}
