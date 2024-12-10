import java.util.ArrayList;
import java.util.List;

public class TradingBotTrader extends Trader {

    //<editor-fold desc="Data Fields">

    private int period;
    private double gains;
    private double losses;
    private boolean hasTraded = false;
    private static final double STOP_LOSS_PERCENTAGE = 0.10;
    private static final double PROFIT_GRAB_PERCENTAGE = 0.35;
    private static final int MAX_TRADES_PER_DAY = 1;
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
        if (dailyTradeCount >= MAX_TRADES_PER_DAY) {
            return;
        }
        List<Stocks> stocksList = market.getListStock();

        Stocks buyStock = null;
        double highestBuyScore = Double.NEGATIVE_INFINITY;
        for (Stocks oneStock : stocksList) {
            double RSI = calculateRSI(period, oneStock.getPriceHistory());
            double MovingAverage = calculateMovingAverage(period, oneStock.getPriceHistory());

            // Assess buy opportunity
            if (RSI < 30 && oneStock.getPrice() < MovingAverage) {
                double buyScore = (30 - RSI) + (MovingAverage - oneStock.getPrice()); // Example scoring mechanism
                if (buyScore > highestBuyScore) {
                    highestBuyScore = buyScore;
                    buyStock = oneStock;
                }
            }
        }
        if (buyStock != null && getCash() >= buyStock.getPrice() * quantity) {
            buy(buyStock, 1, buyStock.getPrice());
            dailyTradeCount++;
        }
        List<Stocks> stocksPortList = new ArrayList<>(stockPortfolio.keySet());
        Stocks sellStock = null;
        double lowestSellScore = Double.POSITIVE_INFINITY;
        for (Stocks oneStock : stocksPortList) {
            double RSI = calculateRSI(period, oneStock.getPriceHistory());
            double MovingAverage = calculateMovingAverage(period, oneStock.getPriceHistory());

            // Assess sell opportunity
            if (RSI > 50 && oneStock.getPrice() > MovingAverage) {
                double sellScore = (oneStock.getPrice() - MovingAverage * (1 + 0.015)); // Example scoring mechanism
                if (sellScore < lowestSellScore) {
                    lowestSellScore = sellScore;
                    sellStock = oneStock;
                    System.out.println(sellStock);
                }
            }
        }
        do {
            if (stockPortfolio.getOrDefault(sellStock, 0) >= quantity) {
                sell(sellStock, quantity, sellStock.getPrice());
                dailyTradeCount++;
                quantity=3;
                break;
            }
            quantity--;
        } while (quantity > 0);
    }



    public String getName(){

        return super.getName()+"TradingBotTrader";
    }
    //</editor-fold>
}
