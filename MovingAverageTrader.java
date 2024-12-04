import java.util.List;

public class MovingAverageTrader extends Trader implements knowledgeableTrader {

    private static final double DEFAULT_THRESHOLD = 0.015;
    private int period;
    private double threshold = DEFAULT_THRESHOLD;

    public MovingAverageTrader(String name, double cash, MarketSimulator market) {
        super(name, market);
        this.period = period;
    }

    public double calculate(int period, List<Double> priceHistory) {
        period = Math.min(period, priceHistory.size());

        try {
            double sum = 0.0;
            for (int i = priceHistory.size() - period; i < priceHistory.size(); i++) {
                sum += priceHistory.get(i);
            }
            return sum / period;
        } catch (Exception e) {
            System.out.println("Error calculating moving average: " + e.getMessage());
            return 0.0; // Default to 0.0 to avoid incorrect trading decisions
        }
    }

    public void execute(Stocks stock, int quantity) {
        List<Double> priceHistory = stock.getPriceHistory();
        double movingAverage = calculate(this.period, priceHistory);
        double currentPrice = stock.getPrice();

        if (currentPrice > movingAverage * (1 + threshold) && getStockPortfolio().containsKey(stock)) {
            do {
                if (getStockPortfolio().get(stock) >= quantity) {
                    sell(stock, quantity, currentPrice);
                    System.out.println("MAT:Sold " + quantity + " units of " + stock.getSymbol() +
                                       " at price " + currentPrice);
                    break;
                }
                quantity--;
                if (quantity <= 0) {
                    System.out.println("MAT:Not enough stock to sell.");
                    break;
                }
            } while (true);
        } else if (currentPrice < movingAverage * (1 - threshold)) {
            if (getCash() >= quantity * currentPrice) {
                buy(stock, quantity, currentPrice);
                System.out.println("MAT:Bought " + quantity + " units of " + stock.getSymbol() +
                                   " at price " + currentPrice);
            } else {
                System.out.println("MAT:Not enough cash to buy stock.");
            }
        } else {
            System.out.println("MAT:Action: Hold stock, price is within the threshold range of the moving average.");
        }
    }

    public String getName() {
        return super.getName() + " (Moving Average)";
    }
}
