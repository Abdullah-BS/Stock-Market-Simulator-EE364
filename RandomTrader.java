import java.util.List;
import java.util.Random;

public class RandomTrader extends Trader {

    private final Random random;

    public RandomTrader(String name, MarketSimulator market) {
        super(name, market);
        this.random = new Random();
    }

    @Override
    public double calculate(int period, List<Double> priceHistory) {
        // Generate a random value between 0 and 1 to fulfill the methods requirement for the Trader class
        return random.nextDouble();
    }

    @Override
    public void execute(Stocks stock, int quantity) {
        double randomExcuse = Math.random();
        if (randomExcuse < 0.3) {
            System.out.println(randomExcuses());
        }

        else {
        // Decide whether to buy or sell randomly
        boolean buyOrSell = random.nextBoolean();
        double currentPrice = stock.getPrice();

        if (buyOrSell) {
            // Buy logic: Ensure the trader has enough cash
            if (getCash() >= quantity * currentPrice) {
                buy(stock, quantity, currentPrice);
                System.out.println(this.getName() + ":Bought " + quantity + " units of " + stock.getSymbol() +
                        " at price " + currentPrice);
            } else {
                System.out.println(this.getName() + "Has no enough cash to buy stock " + stock.getSymbol());
            }
        } else {
            // Sell logic: Ensure the trader has enough stock to sell
            if (getStockPortfolio().getOrDefault(stock, 0) >= quantity) {
                sell(stock, quantity, currentPrice);
                System.out.println(this.getName() + ":Sold " + quantity + " units of " + stock.getSymbol() +
                        " at price " + currentPrice);
            } else {
                System.out.println(this.getName() + ":Not enough stock to sell.");
            }
        }
    }
    }
    public String getName() {
        return super.getName() + " (Random Strategy)";
    }
}
