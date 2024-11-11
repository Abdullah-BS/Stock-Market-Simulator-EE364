import java.util.List;

public class MovingAverageTrader extends Trader implements knowledgeableTrader{

    private int period;
    private double threshold = 0.05;


    public MovingAverageTrader(String name, double cash, int period) {
        super(name, cash);
        this.period = period;

    }
   
    public double calculate(int period, List<Double> priceHistory) {
        if (priceHistory.size() < period + 1) {
            // إذا كانت عدد القيم أقل من الفترة المطلوبة، لا يمكننا حساب المتوسط
            System.out.println("Problem Here");
            return 0.0;
        }

        double sum = 0.0;
        for (double value : priceHistory) {
            sum += value;
        }
        
         return sum / period;
    }

    public void execute(Stocks stock, int quantity) {
        List<Double> priceHistory = stock.getPriceHistory();
        double movingAverage = calculate(this.period, priceHistory);
        double currentPrice = stock.getPrice();

        if (currentPrice > movingAverage * (1 + threshold)) {
            System.out.println("Action: Sell stock, price is significantly above the moving average.");
            sell(stock, quantity, currentPrice);

        } else if (currentPrice < movingAverage * (1 - threshold)) {
            System.out.println("Action: Buy stock, price is significantly below the moving average.");
            buy(stock, quantity, currentPrice);
        } 
        
        else {
            System.out.println("Action: Hold stock, price is within the threshold range of the moving average.");
        }
    }
    
    public String getName() {
        return super.getName() + "(Moving Average)";
    }
}
