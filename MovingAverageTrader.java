import java.util.LinkedList; 
import java.util.Queue; 

public class MovingAverageTrader extends Trader implements knowledgeableTrader{

    private int period;
    private Queue<Double> values;

    public MovingAverageTrader(int period) {
        this.period = period;
        this.values = new LinkedList<>();

    }
   
    public double calculate(int period) {
        if (values.size() < period) {
            // إذا كانت عدد القيم أقل من الفترة المطلوبة، لا يمكننا حساب المتوسط
            return 0.0;
        }

        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        
         return sum / period;
    }


    // دالة لإضافة قيمة جديدة وتحديث القائمة
    public void addValue(double newValue) {
        if (values.size() == period) {
            values.poll(); // إزالة أقدم قيمة إذا كانت القائمة ممتلئة
        }
        values.add(newValue); // إضافة القيمة الجديدة
    }

    
    public String getName() { return  "MovingAverage Trading Strategy";}
}

