import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomTrader extends Trader {

    private final Random random;
    private int dailyOperationCount; // Counter for daily operations
    private static final int MAX_DAILY_OPERATIONS = 2; // Maximum operations per day
    private int dailyTradeCount = 0;

    public RandomTrader(String name, MarketSimulator market) {
        super(name, market);
        this.random = new Random();

    }

    @Override
    public double calculate(int period, List<Double> priceHistory) {
        return random.nextDouble(); // Random value for calculation
    }

    @Override
    public void execute(MarketSimulator market,Stocks stock, int quantity) {
        if (dailyTradeCount >= MAX_DAILY_OPERATIONS) {
            System.out.println(this.getName() + ": Reached daily operation limit.");
            return; // Skip execution if daily limit is reached
        }

        boolean buyOrSell = random.nextBoolean();
        double currentPrice = stock.getPrice();
        double random = Math.random();
        String advice;
        String action;

        if (random < 0.1) {
            System.out.println(randomExcuses());
            action = randomExcuses();
            return;
        }

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

            List<Stocks> stocksPortList = new ArrayList<>(stockPortfolio.keySet());
            Stocks stockToSell = stocksPortList.get((int) (Math.random() * stocksPortList.size()));

            if (getStockPortfolio().getOrDefault(stockToSell, 0) >= quantity) {
                sell(stockToSell, quantity, currentPrice);
                dailyOperationCount++;
                System.out.println(this.getName() + ": Sold " + quantity + " units of " + stockToSell.getSymbol() +
                        " at price " + currentPrice);
            } else {
                System.out.println(this.getName() + ": Not enough stock to sell.");
            }
        }
    }

    public String getName() {
        return super.getName() + " (Random)";
    }


}
