public class TradingBotTrader extends Trader {

    //<editor-fold desc="Data Fields">

    private int period;
    private double[] values;
    private double gians;
    private double losses;
    //</editor-fold>

    //<editor-fold desc="Constructors">

    TradingBotTrader() {

    }
    TradingBotTrader(String Name, double intialCash, int period, double[] values, double gians, double losses) {
        super(Name, intialCash);
        this.period = period;
        this.values = values;
        this.gians = gians;
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

    public double getGians() {
        return gians;
    }
    public void setGians(double gians) {
        this.gians = gians;
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
        RSI = 100 - (100 / (1 + rs));

        return RSI;
    }

    // MovingAverage
    public double calculateMovingAverage(int period) {
        double valueMA;
        double[] prices= stocks.getPricehistory();
        double sum=0;
        int index= prices.length-period;

        if(index>0){
            for(double amount :prices){
                sum+=amount;
                valueMA=sum/period;
            }
        else{
                System.out.println("Error");
            }
        return valueMA;
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