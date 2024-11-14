import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RSITrader extends Trader implements knowledgeableTrader{
    
    private int period;

    public RSITrader(String name, double cash, int period, MarketSimulator market) {
        super(name, cash, market);
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
                    loss = (loss * (period - 1)) / period;  // No new loss
                } else if (change < 0) {
                    gain = (gain * (period - 1)) / period;  // No new gain
                    loss += -change;
                }
            }

            double avgGain = gain / period;
            double avgLoss = loss / period;

            double rsi = avgLoss == 0 ? 100 : 100 - (100 / (1 + (avgGain / avgLoss)));


            return rsi;
        }

        catch (Exception e) {
//            System.out.println("Error calculating RSI");
            return 0;
        }

        }


    public void execute(Stocks stock, int quantity) {
            System.out.println(stockPortfolio);
            List<Double> priceHistory = stock.getPriceHistory();

            double buyRSI = calculate(this.period, priceHistory);
            double currentPrice = stock.getPrice();

            if (buyRSI < 30) {
                System.out.println("Action: Buy stock, price is significantly below the RSI.");
                buy(stock, quantity, currentPrice);
            }

            HashMap<Stocks, Double> stockRSIMap = new HashMap<>();

            for (Stocks newStock : super.getStockPortfolio().keySet()) {
                double rsi = calculate(this.period, newStock.getPriceHistory());
                stockRSIMap.put(stock, rsi);
            }

            Map.Entry<Stocks, Double> maxEntry = stockRSIMap.entrySet()
                     .stream()
                     .max(Map.Entry.comparingByValue())
                     .orElse(null);  // If the map is empty, return null

        if (maxEntry != null) {

            Stocks maximumStock = maxEntry.getKey();
            double maximumRSI = maxEntry.getValue();


            if (maximumRSI > 50) {
                sell(maximumStock, quantity, currentPrice);

            } else {
                System.out.println("Action: Hold stock, price is within the RSI range.");
            }
        }

        }


    public String getName() { return  super.getName() + "(RSI)";}




}
