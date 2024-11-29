import java.util.List;

public class MovingAverageTrader extends Trader implements knowledgeableTrader{

    private int period;
    private double threshold = 0.015;
    private String traderType="RSI Trader";


    public MovingAverageTrader(String name, double cash, int period, MarketSimulator market) {
        super(name, cash, market);
        this.period = period;

    }
   
    public double calculate(int period, List<Double> priceHistory) {

        period = Math.min(period, priceHistory.size());

        try {

            double sum = 0.0;

            for (double value : priceHistory) {
                sum += value;
            }

            return sum / period;
        }

        catch (Exception e) {
            System.out.println("Cant calculate MovingAverageTrader" + e.getMessage());
            return 222.60;
        }

    }

    public void execute(Stocks stock, int quantity) {

            List<Double> priceHistory = stock.getPriceHistory();
            double movingAverage = calculate(this.period, priceHistory);
            double currentPrice = stock.getPrice();

            if (currentPrice > movingAverage * (1 + threshold) && (getStockPortfolio().containsKey(stock))) {
                do {
                    if (getStockPortfolio().get(stock) >= quantity) {
                        System.out.println("Action: Sell stock, price is significantly above the moving average.");
                        sell(stock, quantity, currentPrice);
                        break;
                    }
                    quantity = quantity - 1;
                } while (true);


            } else if (currentPrice < movingAverage * (1 - threshold)) {
                System.out.println("Action: Buy stock, price is significantly below the moving average.");
                buy(stock, quantity, currentPrice);
            } else {
                System.out.println("Action: Hold stock, price is within the threshold range of the moving average.");
            }
        }

    public String getName() {
        return super.getName() + "(Moving Average)";
    }
}
