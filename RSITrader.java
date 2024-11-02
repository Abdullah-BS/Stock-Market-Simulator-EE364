import java.util.List;

public class RSITrader {
    
    private int period;
    private double gain; 
    private double loss;
    Market market;
    List<Stock> stocks = market.getStocks();

    public double calculate(int period){
    double prices[] = stocks.getPriceHistory();
    gain = 0;
    loss = 0;

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

    double rs = avgGain / avgLoss;
    return 100 - (100 / (1 + rs));
    }
     
    

    public String getName() { return  "RSI Trading Strategy";}




}
