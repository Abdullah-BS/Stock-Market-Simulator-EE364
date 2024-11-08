import java.util.List;

public class MovingAverageTrader extends Trader implements knowledgeableTrader{

    private int period;
//    private Queue<Double> values;

    public MovingAverageTrader(String name, double cash, int period) {
        super(name, cash);
        this.period = period;
//        this.values = new LinkedList<>();

    }
   
    public double calculate(int period, List<Double> priceHistory) {
        if (priceHistory.size() < period) {
            // إذا كانت عدد القيم أقل من الفترة المطلوبة، لا يمكننا حساب المتوسط
            return 0.0;
        }

        double sum = 0.0;
        for (double value : priceHistory) {
            sum += value;
        }
        
         return sum / period;
    }

   public void execute(Stocks stock) {
        List<Double> priceHistory = stock.getPriceHistory();
        double movingAverage = calculate(this.period, priceHistory);
        double currentPrice = priceHistory.size() != 0.0 ? priceHistory.get(priceHistory.size() - 1) : 0.0;

        if (currentPrice > movingAverage) {
            System.out.println("Action: Sell stock, price is above the moving average.");
            sell(stock, period, currentPrice)
            
        } else {
            System.out.println("Action: Buy stock, price is below or equal to the moving average.");
        }
}

    public String getName() { return "MovingAverage Trading Strategy";}
}

    // دالة لإضافة قيمة جديدة وتحديث القائمة
//    public void addValue(double newValue) {
//        if (values.size() == period) {
//            values.poll(); // إزالة أقدم قيمة إذا كانت القائمة ممتلئة
//        }
//        values.add(newValue); // إضافة القيمة الجديدة
//    }
