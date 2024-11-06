import java.util.List;

public class RSITrader extends Trader implements knowledgeableTrader{
    
    private int period;
    private double gain; 
    private double loss;
    List<Stock> stocks;

    public RSITrader(int period, List<Stock> stocks) {
        this.period = period;
        this.stocks = stocks;
    }

    public void calculate() {
        
        for (Stock stock : stocks) {
            double[] prices = stock.getPriceHistory(); // Assuming this returns a double array
            
            
            if (prices.length < period + 1) {
                System.out.println("Not enough price data to calculate RSI for " + stock.getSymbol());
                continue;
            }

            double gain = 0;
            double loss = 0;

          
            for (int i = 1; i <= period; i++) {
                double change = prices[i] - prices[i - 1];
                if (change > 0) {
                    gain += change;
                } else {
                    loss -= change;
                }
            }

            double avgGain = gain / period;
            double avgLoss = loss / period;

            double rsi = avgLoss == 0 ? 100 : 100 - (100 / (1 + (avgGain / avgLoss)));
            
           
            System.out.println("Stock: " + stock.getSymbol() + ", RSI: " + rsi);
        }
    }
     
    

    public String getName() { return  "RSI Trading Strategy";}




}
