import java.util.ArrayList;
import java.util.List;

public class MovingAverageTrader extends Trader implements knowledgeableTrader {

    private int period;
    private double threshold = 0.015;

    public MovingAverageTrader(String name, int period, MarketSimulator market) {
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
        double random = Math.random();
        List<Double> priceHistory = stock.getPriceHistory();
        double movingAverage = calculate(this.period, priceHistory);
        double currentPrice = stock.getPrice();

        if (random < 0.3) {
            System.out.println(randomExcuses());
        } else {

            //SELL
            if (currentPrice > movingAverage * (1 + threshold) && getStockPortfolio().containsKey(stock)) {
                do {
                    if (getStockPortfolio().get(stock) >= quantity) {
                        sell(stock, quantity, currentPrice);
                        System.out.println(this.getName() + ":Sold " + quantity + " units of " + stock.getSymbol() +
                                " at price " + currentPrice);
                        break;
                    }
                    quantity--;

                    if (quantity <= 0) {
                        System.out.println(this.getName() + ":Not enough stock to sell.");
                        break;
                    }
                } while (true);
            }

            //BUY
            else if (currentPrice < movingAverage * (1 - threshold)) {
                do {
                    if (getCash() >= quantity * currentPrice) {
                        buy(stock, quantity, currentPrice);
                        System.out.println(this.getName() + ": Bought " + quantity + " units of " + stock.getSymbol() +
                                " at price " + currentPrice);
                        break;
                    }

                    quantity--; // Reduce quantity by 1

                    if (quantity <= 0) {
                        System.out.println(this.getName() + ": Not enough cash to buy stock " + stock.getSymbol());
                        break;
                    }
                } while (true);
            } else {
                System.out.println(this.getName() + ": Hold stock, price is within the threshold range of the moving average.");
            }



        }
    }
    public String getName() {
        return super.getName() + " (Moving Average)";
    }
}
