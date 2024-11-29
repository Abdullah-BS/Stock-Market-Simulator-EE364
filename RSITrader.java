import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RSITrader extends Trader implements knowledgeableTrader{
    
    private int period;

    public RSITrader(String name, int period, MarketSimulator market) {
        super(name, market);
        this.period = period;
    }

    public int getPeriod() {return period;}
    public void setPeriod(int period) {this.period = period;}

    public double calculate(int period, List<Double> priceHistory) {

            period = 10;

            try {
                
        
            double gain = 0;
            double loss = 0;


            for (int j = 1; j <= period - 1 ; j++) {
                double change = priceHistory.get(j) - priceHistory.get(j - 1);
                if (change > 0) {
                    gain += change;
                    // No new loss
                } else if (change < 0) {
                    // No new gain
                    loss -= change;
                }
//                System.out.println("Day " + j + ": Change = " + change + ", Gain = " + gain + ", Loss = " + loss);

            }

            double avgGain = gain / period;
            double avgLoss = loss / period;
//                System.out.println(avgGain + " " + avgLoss);

            double rsi = avgLoss == 0 ? 100 : 100 - (100 / (1 + (avgGain / avgLoss)));
//                System.out.println("RSI: " + rsi);


            return rsi;
        }

        catch (Exception e) {
//            System.out.println("Error calculating RSI");
            return 0;
        }

        }


    public void execute(Stocks stock, int quantity) {
//            System.out.println(stockPortfolio);
            List<Double> priceHistory = stock.getPriceHistory();

            double buyRSI = calculate(this.period, priceHistory);
            double currentPrice = stock.getPrice();
//        System.out.println("Buy RSI: " + buyRSI);
            if (buyRSI < 30) {
//                System.out.println("Action: Buy stock, price is significantly below the RSI.");
                buy(stock, quantity, currentPrice);
            }

            HashMap<Stocks, Double> stockRSIMap = new HashMap<>();

            for (Stocks newStock : super.getStockPortfolio().keySet()) {
                List<Double> newPriceHistory = newStock.getPriceHistory();

                // Only calculate RSI if there's enough price history for the specified period
                if (newPriceHistory.size() >= this.period) {
                    double rsi = calculate(this.period, newPriceHistory);
                    stockRSIMap.put(newStock, rsi);
                } else {
                    // Handle the case where there isn't enough data for RSI calculation (optional)
                    System.out.println("Not enough price history for stock " + newStock.getSymbol() + " to calculate RSI."+ getName());
                }
            }

            Map.Entry<Stocks, Double> maxEntry = stockRSIMap.entrySet()
                     .stream()
                     .max(Map.Entry.comparingByValue())
                     .orElse(null);  // If the map is empty, return null

        if (maxEntry != null) {

            Stocks maximumStock = maxEntry.getKey();
            double maximumRSI = maxEntry.getValue();
//            System.out.println("Maximum RSI: " + maximumRSI);

            if (maximumRSI > 70) {
                sell(maximumStock, quantity, maximumStock.getPrice());

            } else {
//                System.out.println("Action: Hold stock, price is within the RSI range.");
            }
        }

        }


    public String getName() { return  super.getName() + "(RSI)";}




}
