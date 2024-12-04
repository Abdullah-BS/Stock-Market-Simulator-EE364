import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RSITrader extends Trader implements knowledgeableTrader {

    private int period;

    public RSITrader(String name, int period, MarketSimulator market) {
        super(name, market);
        this.period = period;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public double calculate(int period, List<Double> priceHistory) {
        // Ensure there is enough data for calculation
        if (priceHistory.size() < period) {
            System.out.println("Insufficient data for RSI calculation.");
            return 50; // Neutral RSI value
        }

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
        // Step 1: Calculate RSI for the current stock
        List<Double> priceHistory = stock.getPriceHistory();
        double buyRSI = calculate(this.period, priceHistory);
        double currentPrice = stock.getPrice();

        // Step 2: Buy if RSI is below 30 and cash is sufficient
        if (buyRSI < 30) {
            if (getCash() >= quantity * currentPrice) {
                buy(stock, quantity, currentPrice);
                System.out.println("RSIT:Bought " + quantity + " units of " + stock.getSymbol() +
                                   " at price " + currentPrice);
            } else {
                System.out.println("RSIT:Not enough cash to buy stock: " + stock.getSymbol());
            }
        }

        // Step 3: Calculate RSI for all portfolio stocks and identify the one with max RSI
        HashMap<Stocks, Double> stockRSIMap = new HashMap<>();
        for (Stocks portfolioStock : super.getStockPortfolio().keySet()) {
            List<Double> portfolioPriceHistory = portfolioStock.getPriceHistory();
            if (portfolioPriceHistory.size() >= this.period) {
                double rsi = calculate(this.period, portfolioPriceHistory);
                stockRSIMap.put(portfolioStock, rsi);
            }
        }

        // Step 4: Find stock with the maximum RSI
        Map.Entry<Stocks, Double> maxEntry = stockRSIMap.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        // Step 5: Sell if the maximum RSI is above 70 and quantity is sufficient
        if (maxEntry != null) {
            Stocks maximumStock = maxEntry.getKey();
            double maximumRSI = maxEntry.getValue();
            if (maximumRSI > 70) {
                int stockQuantity = getStockPortfolio().getOrDefault(maximumStock, 0);
                if (stockQuantity >= quantity) {
                    sell(maximumStock, quantity, maximumStock.getPrice());
                    System.out.println("RSIT: Sold " + quantity + " units of " + maximumStock.getSymbol() +
                                       " at price " + maximumStock.getPrice());
                } else {
                    System.out.println("RSIT:Not enough stock to sell: " + maximumStock.getSymbol());
                }
            }
        }
    }

    public String getName() {
        return super.getName() + " (RSI)";
    }
}
