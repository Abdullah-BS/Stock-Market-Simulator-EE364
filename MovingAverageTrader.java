import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovingAverageTrader extends Trader implements knowledgeableTrader {

    private int period;
    private double threshold = 0.015;
    private static final double STOP_LOSS_PERCENTAGE = 0.10; // 10% stop-loss
    private static final double PROFIT_GRAB_PERCENTAGE = 0.45; // 45% profit-grab
    private static final int MAX_TRADES_PER_DAY = 2; // Max trades allowed per day

    public MovingAverageTrader(String name, int period, MarketSimulator market) {
        super(name, market);
        this.period = period;
    }

    public double calculate(int period, List<Double> priceHistory) {
        period = Math.min(period, priceHistory.size());

        try {
            double sum = 0.0;

            for (int i = priceHistory.size() - period + 1; i < priceHistory.size(); i++) {
                sum += priceHistory.get(i);
            }
            return sum / period;
        } catch (Exception e) {
            System.out.println("Error calculating moving average: " + e.getMessage());
            return 0.0; // Default to 0.0 to avoid incorrect trading decisions
        }
    }

    public void execute(MarketSimulator market,Stocks stock, int quantity) {
        if (dailyTradeCount  >= MAX_TRADES_PER_DAY) {
            System.out.println(this.getName() + ": Daily trade limit reached.");
            return;
        }

        double random = Math.random();
        List<Double> priceHistory = stock.getPriceHistory();
        double movingAverage = calculate(this.period, priceHistory);
        double currentPrice = stock.getPrice();
        String advice;
        String action;

        // Determine the advice based on market conditions
        if (currentPrice > movingAverage * (1 + threshold)) {
            advice = "Sell";
        } else if (currentPrice < movingAverage * (1 - threshold)) {
            advice = "Buy";
        } else {
            advice = "Hold";
        }

        // Stop-loss and profit-grab checks for all owned stocks
        for (Map.Entry<Stocks, Integer> entry : new HashMap<>(getStockPortfolio()).entrySet()) {
            if (dailyTradeCount >= MAX_TRADES_PER_DAY) break;

            Stocks portfolioStock = entry.getKey();
            int ownedQuantity = entry.getValue();
            double purchasePrice = portfolioStock.getPriceHistory().get(portfolioStock.getPriceHistory().size() - 1); // Last price as purchase price
            double profitPercentage = (currentPrice - purchasePrice) / purchasePrice;

            // Stop-loss logic
//            if (profitPercentage <= -STOP_LOSS_PERCENTAGE) {
//                sell(portfolioStock, ownedQuantity, currentPrice);
//                dailyTradeCount++;
//                System.out.println(this.getName() + ": Sold (Stop Loss) " + ownedQuantity + " units of " +
//                        portfolioStock.getSymbol() + " at price " + currentPrice);
//                return; // Exit after executing stop-loss
//            }
//
//            // Profit-grab logic
//            if (profitPercentage >= PROFIT_GRAB_PERCENTAGE) {
//                sell(portfolioStock, ownedQuantity, currentPrice);
//                dailyTradeCount++;
//                System.out.println(this.getName() + ": Sold (Profit Grab) " + ownedQuantity + " units of " +
//                        portfolioStock.getSymbol() + " at price " + currentPrice);
//                return; // Exit after executing profit-grab
//            }
        }

        // Simulate trader's action (random excuse or actual action)
        if (random < 0.1) {
            System.out.println(randomExcuses());
            action = randomExcuses();
            return;
        } else {
            // SELL
            if (advice.equals("Sell") && getStockPortfolio().containsKey(stock)) {
                do {
                    if (getStockPortfolio().get(stock) >= quantity) {
                        sell(stock, quantity, currentPrice);
                        dailyTradeCount++;

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
            // BUY
            else if (advice.equals("Buy")) {
                do {
                    if (getCash() >= quantity * currentPrice) {
                        buy(stock, quantity, currentPrice);
                        dailyTradeCount++;

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
            } else {
                action = "Hold";
                System.out.println(this.getName() + ": Hold stock, price is within the threshold range of the moving average.");
            }
        }

        getAdvice_VS_action().put(advice, action);
    }

    public String getName() {
        return super.getName() + " (Moving Average Strategy)";
    }
}
