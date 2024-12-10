import java.util.ArrayList;
import java.util.List;

public class TradingBotTrader extends Trader {

    //<editor-fold desc="Data Fields">

    private int period;
    private double gains;
    private double losses;
    private boolean hasTraded = false;
    //</editor-fold>

    //<editor-fold desc="Constructors">

    public TradingBotTrader(String name, int period, MarketSimulator market) {
        super(name, market);
        this.period = period;

    }

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
    public double calculateRSI(int period, List<Double> priceHistory) {
        try{
            double gain = 0;
            double loss = 0;

            for (int j = priceHistory.size() - period + 1; j < priceHistory.size(); j++) {
                double change = priceHistory.get(j) - priceHistory.get(j - 1);
                if (change > 0) {
                    gain += change;
                } else {
                    loss -= change;
                }
            }

            double avgGain = gain / period;
            double avgLoss = loss / period;

            return avgLoss == 0 ? 100 : 100 - (100 / (1 + (avgGain / avgLoss)));
        } catch (Exception e) {
            System.out.println("Error calculating RSI: " + e.getMessage());
            return 50; // Return neutral RSI value on error
        }
    }

    // MovingAverage
    public double calculateMovingAverage(int period, List<Double> priceHistory) {
        period = Math.min(period, priceHistory.size());

        try {
            double sum = 0.0;
            for (int i = priceHistory.size() - period; i < priceHistory.size(); i++) {
                sum += priceHistory.get(i);
            }
            return sum / period;
        } catch (Exception e) {
            System.out.println("Error calculating moving average: " + e.getMessage());
            return 0.0; // Default to 0.0 to avoid incorrect trading decisions
        }

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

    public void resetDailyTradeFlag() {
        boolean hasTraded = false;
    }
    //<editor-fold desc="Execute">
    public void execute(MarketSimulator market,Stocks stock, int quantity) {

        if (hasTraded) {
            return; // Prevent further trades if already traded today
        }
        List<Stocks> stocksList= market.getListStock();

        Stocks buyStock= stocksList.get(0);
        double comparelineBuy = Double.MIN_VALUE;
        for (Stocks oneStock : stocksList) {

            double RSI = calculateRSI(period,oneStock.getPriceHistory());
            double MovingAverage = calculateMovingAverage(period,oneStock.getPriceHistory());

            double line = Math.abs(oneStock.getPrice() - MovingAverage*(1+0.015));
            if (RSI < 30 && line > comparelineBuy) {

                comparelineBuy = line;
                buyStock = oneStock;

            }
        }
        double comparelineSell = Double.MIN_VALUE;

        if (comparelineSell > 0) {
            buy(buyStock, quantity, buyStock.getPrice());
        }
        List<Stocks> stocksPortList = new ArrayList<Stocks>(stockPortfolio.keySet());
        Stocks sellStock= stocksList.get(0);
        for (Stocks oneStock : stocksPortList) {

            double RSI = calculateRSI(period,oneStock.getPriceHistory());
            double MovingAverage = calculateMovingAverage(period,oneStock.getPriceHistory());

            double line = Math.abs(oneStock.getPrice() - MovingAverage*(1+0.015));
            if (RSI > 70 && line < comparelineSell) {
                comparelineSell = line;
                if(buyStock!=oneStock) {
                    sellStock = oneStock;
                }
            }
        }
        if (comparelineSell > 0 && buyStock != sellStock) {
            sell(sellStock, quantity, sellStock.getPrice());
        }
        hasTraded = true;
    }

    public String getName(){

        return "TradingBotTrader";
    }
    //</editor-fold>
}
