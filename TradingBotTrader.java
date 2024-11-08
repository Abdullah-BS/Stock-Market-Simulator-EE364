import java.util.List;

public class TradingBotTrader extends Trader {

    //<editor-fold desc="Data Fields">

    private int period;
    private double gains;
    private double losses;
    //</editor-fold>

    //<editor-fold desc="Constructors">

    public TradingBotTrader(String Name, double initialCash) {
        super(Name, initialCash);

    }
    public TradingBotTrader(String Name, double initialCash, int period) {
        super(Name, initialCash);
        this.period = period;

    }
    //</editor-fold>


    // <editor-fold desc="Get and Set Methods">

    public int getPeriod() {
        return period;
    }
    public void setPeriod(int period) {
        this.period = period;
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
    public double calculateRSI(int period, List<Double> prices) {
        double RSI;
        double gain = 0;
        double loss = 0;

        for (int i = 0; i <= period; i++) {

            double change = prices.get(i+1) - prices.get(i);
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
    public double calculateMovingAverage(int period, List<Double> prices) {
        double valueMA=0;
        double sum=0;
        int index= prices.size()-period+1;


        if(index>0){
            for(double price :prices){
                sum+=price;
                valueMA=sum/period;
            }
        }
        else{
                System.out.println("Error");

        }
        return valueMA;
    }


    // Calculate based on RSI and MovingAverage
    public double calculate(int period, List<Double> prices) {
        double value;
        double RSI= calculateRSI(period, prices);
        double MA= calculateMovingAverage(period, prices);

        value= (RSI+MA)/2;

        return value;
    }
    //</editor-fold>


    //<editor-fold desc="Execute">
    public void execute(int period,Stocks stock, int quantity) {
        List<Double> priceHistory = stock.getPriceHistory();
        double threshold = 1.10;
        // Check if the period is within the limit of the array then Execute
        if (stock.getPriceHistory().size() < period + 1) {

            double value = calculate(period, priceHistory);
            double currentPrice = stock.getPrice();

            if (value < currentPrice * threshold) {

                System.out.println("Suggestion to buy stock");
                buy(stock, quantity, currentPrice);
            } else if (value >= currentPrice * threshold) {

                System.out.println("Suggestion to sell stock");
                sell(stock, quantity, currentPrice);

            }

        } else {
            System.out.println("Error");
        }
    }

    public String getName(){

        return "TradingBotTrader";
    }
    //</editor-fold>
}