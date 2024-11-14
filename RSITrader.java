import java.util.List;

public class RSITrader extends Trader implements knowledgeableTrader{
    
    private int period;

    public RSITrader(String name, double cash) {
        super(name, cash);
        this.period = 5;
    }

    public RSITrader(String name, double cash, int period) {
        super(name, cash);
        this.period = period;
    }

    public int getPeriod() {return period;}
    public void setPeriod(int period) {this.period = period;}

    public double calculate(int period, List<Double> priceHistory) {

            period = Math.min(period, priceHistory.size());

            try {
                
        
            double gain = 0;
            double loss = 0;


            for (int j = 1; j <= period; j++) {
                double change = priceHistory.get(j) - priceHistory.get(j - 1);
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

        catch (Exception e) {
            System.out.println("Error calculating RSI");
            return 0;
        }

        }


    public void execute(Stocks stock, int quantity) {
        List<Double> priceHistory = stock.getPriceHistory();
        double RSI = calculate(this.period, priceHistory);
        double currentPrice = stock.getPrice();

        if (RSI > 70) {
            System.out.println("Action: Sell stock, price is above the RSI.");
            sell(stock, quantity, currentPrice);

        } else if (RSI < 30) {
            System.out.println("Action: Buy stock, price is significantly below the RSI.");
            buy(stock, quantity, currentPrice);
        } 

        else {
            System.out.println("Action: Hold stock, price is within the RSI range.");
        }
        
        
    }

    public String getName() { return  super.getName() + "(RSI)";}




}
