import java.util.List;
import java.util.Random;

public class RandomTrader extends Trader {

    private final Random random;
    private int dailyOperationCount; // Counter for daily operations
    private static final int MAX_DAILY_OPERATIONS = 3; // Maximum operations per day

    public RandomTrader(String name, MarketSimulator market) {
        super(name, market);
        this.random = new Random();
        this.dailyOperationCount = 0; // Initialize operation count
    }

    @Override
    public double calculate(int period, List<Double> priceHistory) {
        return random.nextDouble(); // Random value for calculation
    }

    @Override
    public void execute(Stocks stock, int quantity) {
        if (dailyOperationCount >= MAX_DAILY_OPERATIONS) {
            System.out.println(this.getName() + ": Reached daily operation limit.");
            return; // Skip execution if daily limit is reached
        }

        boolean buyOrSell = random.nextBoolean();
        double currentPrice = stock.getPrice();

        if (buyOrSell) { // Buy logic
            if (getCash() >= quantity * currentPrice) {
                buy(stock, quantity, currentPrice);
                dailyOperationCount++;
                System.out.println(this.getName() + ": Bought " + quantity + " units of " + stock.getSymbol() +
                        " at price " + currentPrice);
            } else {
                System.out.println(this.getName() + ": Insufficient cash to buy stock " + stock.getSymbol());
            }
        } else { // Sell logic
            if (getStockPortfolio().getOrDefault(stock, 0) >= quantity) {
                sell(stock, quantity, currentPrice);
                dailyOperationCount++;
                System.out.println(this.getName() + ": Sold " + quantity + " units of " + stock.getSymbol() +
                        " at price " + currentPrice);
            } else {
                System.out.println(this.getName() + ": Not enough stock to sell.");
            }
        }
    }

    public String getName() {
        return super.getName() + " (Random Strategy)";
    }

    public void resetDailyOperations() {
        System.out.println(this.getName() + ": Resetting daily operations.");
        dailyOperationCount = 0;
    }
}
