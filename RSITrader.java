import java.util.List;

public class RSITrader extends Trader implements knowledgeableTrader{
    
    private int period;
    private double gain; 
    private double loss;

    public RSITrader(String name, double cash) {
        super(name, cash);
    }

    public RSITrader(String name, double cash, int period) {
        super(name, cash);
        this.period = period;
    }

    public int getPeriod() {return period;}
    public void setPeriod(int period) {this.period = period;}

    public double calculate(int period, List<Double> priceHistory) {
        
        for (double price : priceHistory) {

            // what does continue do? infinite loop?
            if (priceHistory.size() < period + 1) {
//                System.out.println("Not enough price data to calculate RSI for " + stock.getSymbol());
                continue;
            }

            double gain = 0;
            double loss = 0;


            for (int i = 1; i <= period; i++) {
                double change = priceHistory.get(i) - priceHistory.get(i - 1);
                if (change > 0) {
                    gain += change;
                } else {
                    loss -= change;
                }
            }

            double avgGain = gain / period;
            double avgLoss = loss / period;

            double rsi = avgLoss == 0 ? 100 : 100 - (100 / (1 + (avgGain / avgLoss)));


            return rsi;
        }
        return 0;
        // no Return statement outside the loop was an error. no idea why.
    }
    public void execute(Stocks stock, int quantity) {
        List<Double> priceHistory = stock.getPriceHistory();
        double RSI = calculate(this.period, priceHistory);
        double currentPrice = stock.getPrice();
        double threshold = 1.10;

        if (RSI > currentPrice*threshold) {
            System.out.println("Action: Sell stock, price is above the RSI.");
            sell(stock.getSymbol(), quantity, currentPrice);
        } else {
            System.out.println("Action: Buy stock, price is below or equal to the RSI.");
            buy(stock.getSymbol(), quantity, currentPrice);
        }
    }

    public String getName() { return  "RSI Trading Strategy";}




}
