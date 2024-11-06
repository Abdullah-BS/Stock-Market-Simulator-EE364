public class TradingBotTrader extends Trader {

    //<editor-fold desc="Data Fields">

    private int period;
    private double[] values;
    private double gains;
    private double losses;
    //</editor-fold>

    //<editor-fold desc="Constructors">

    TradingBotTrader() {

    }
    TradingBotTrader(String Name, double intialCash, int period, double[] values, double gains, double losses) {
        super(Name, intialCash);
        this.period = period;
        this.values = values;
        this.gains = gains;
        this.losses = losses;
    }
    //</editor-fold>


    // <editor-fold desc="Get and Set Methods">

    public int getPeriod() {
        return period;
    }
    public void setPeriod(int period) {
        this.period = period;
    }

    public double[] getValues() {
        return values;
    }
    public void setValues(double[] values) {
        this.values = values;
    }

    public double getGains() {
        return gains;
    }
    public void setGains(double gains) {
        this.gains = gains;
    }

    public double getLosses() {
        return losses;
    }
    public void setLosses(double losses) {
        this.losses = losses;
    }

    //</editor-fold>


    //<editor-fold desc="Trading bot-- Calculations behaviour">

    // RSI
    public double calculateRSI(int period) {
        double RSI;
        double prices[] = stocks.getPriceHistory();
        double gain = 0;
        double loss = 0;

        for (int i = 0; i <= period; i++) {

            double change = prices[i+1] - prices[i];
            if (change > 0) {
                gain += change;
            } else {
                loss -= change;
            }
        }
        double avgGain = gain / period;
        double avgLoss = loss / period;

        double rs = avgGain / avgLoss;
        RSI = 100 - (100 / (1 + rs));

        return RSI;
    }

    // MovingAverage
    public double calculateMovingAverage(int period) {
        double valueMA=0;
        double[] prices= stocks.getPricehistory();
        double sum=0;
        int index= prices.length-period;

        if(index>0){
            for(double amount :prices){
                sum+=amount;
                valueMA=sum/period;
            }
        }
        else{
                System.out.println("Error");

        }
        return valueMA;
    }


    // Calculate based on RSI and MovingAverage
    public double calculate(int period) {
        double value;
        double RSI= calculateRSI(period);
        double MA= calculateMovingAverage(period);

        value= (RSI+MA)/2;

        return value;
    }
    //</editor-fold>


    //<editor-fold desc="Excute">
    public void excute(){}

    public String getName(){

        return "TradingBotTrader";
    }
    //</editor-fold>
}