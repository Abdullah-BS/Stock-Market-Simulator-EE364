import java.util.*;

public class MovingAverageTrader extends Trader implements knowledgeableTrader {

    private int period; // Moving average period
    private double threshold = 0.25; // Threshold for trading decision


    public MovingAverageTrader(String name, int period, MarketSimulator market) {
        super(name, market);
        this.period = period;
    }

    // Calculate the moving average for the specified period
    public double calculate(int period, List<Double> priceHistory) {
        period = Math.min(period, priceHistory.size());

        try {
            double sum = 0.0;
            for (int i = priceHistory.size() - period + 1; i < priceHistory.size(); i++) {
                sum += priceHistory.get(i);
            }
            return Math.round((sum/period) *100.0) / 100.0;
        } catch (Exception e) {
            System.out.println("Error calculating moving average: " + e.getMessage());
            return 0.0; // Default to 0.0 to avoid incorrect trading decisions
        }
    }

    // Execute a trading strategy based on moving average analysis
    public void execute(MarketSimulator market,Stocks stock, int quantity) {
        try {

            // Random factor and market setup
            double random = Math.random();
            Random rand = new Random();
            List<Stocks> marketStocks = market.getListStock();

            // Check if sufficient stocks are available in the market
            if (marketStocks.size() < 2) {
                System.out.println(this.getName() + ": Not enough stocks in the market for trading.");
                return;
            }

            // Randomly select stocks for buy and sell actions
            Stocks buyStock = marketStocks.get(rand.nextInt(marketStocks.size() - 1));
            List<Stocks> portStocks = new ArrayList<>(stockPortfolio.keySet());

            Stocks sellStock = portStocks.get(rand.nextInt(portStocks.size() - 1));


            // Calculate moving averages for buy and sell stocks
            double buyMovingAverage = calculate(this.period, buyStock.getPriceHistory());
            double sellMovingAverage = calculate(this.period, sellStock.getPriceHistory());
            double buyCurrentPrice = buyStock.getPrice();
            double sellCurrentPrice = sellStock.getPrice();

            // Initialize advice and action variables
            String buyAdviceMessage;
            String sellAdviceMessage;
            String buyAction;
            String sellAction;
            Boolean buyAdvice;
            Boolean sellAdvice;

            // Generate buy advice based on moving average and threshold
            if (buyCurrentPrice < buyMovingAverage * (1 + threshold)) {
                buyAdviceMessage = "Buy: Price is Higher than the MA " + buyCurrentPrice + " < " + Math.round(buyMovingAverage * (1 + threshold)*100)/100;
                buyAdvice = true;
            } else {
                buyAdviceMessage = "Hold: Price is around than the MA " + buyCurrentPrice + " ~= " + Math.round(buyMovingAverage * (1 + threshold)*100)/100;
                buyAdvice = false;
            }

            // Generate sell advice based on moving average and threshold
            if (sellCurrentPrice > sellMovingAverage * (1 + threshold)) {
                sellAdviceMessage = "Sell: Price is lower than the MA " + sellCurrentPrice + " > " + Math.round(sellMovingAverage * (1 + threshold)*100)/100;
                sellAdvice = true;
            } else {
                sellAdviceMessage = "Hold: Price is around than the MA " + sellCurrentPrice + " ~= " + Math.round(sellMovingAverage * (1 + threshold)*100)/100;
                sellAdvice = false;
            }

            // Execute buy action based on advice
            if (random < 0.3) {
                System.out.println(randomExcuses());
                buyAction = randomExcuses();
            } else {

                if (buyAdvice) {
                    do {
                        if (getCash() >= quantity * buyCurrentPrice) {
                            buy(buyStock, quantity, buyCurrentPrice);
                            dailyTradeCount++;

                            buyAction = "Bought " + quantity + " units of " + buyStock.getSymbol() +
                                    " at price " + buyCurrentPrice;
                            System.out.println(this.getName() + ": Bought " + quantity + " units of " + buyStock.getSymbol() +
                                    " at price " + buyCurrentPrice);
                            break;
                        }

                        quantity--; // Reduce quantity by 1

                        if (quantity <= 0) {
                            System.out.println(this.getName() + ": Not enough cash to buy stock " + buyStock.getSymbol());
                            buyAction = "Not enough cash to buy stock " + buyStock.getSymbol();
                            break;
                        }
                    } while (true);
                } else {
                    buyAction = "Hold";
                }
            }

            // Execute sell action based on advice
            if (random < 0.3) {
                System.out.println(randomExcuses());
                sellAction = randomExcuses();
            } else {

                if (sellAdvice) {
                    do {
                        if (getStockPortfolio().get(sellStock) >= quantity) {
                            sell(sellStock, quantity, sellCurrentPrice);
                            dailyTradeCount++;

                            sellAction = "Sold " + quantity + " units of " + sellStock.getSymbol() +
                                    " at price " + sellCurrentPrice;
                            System.out.println(this.getName() + ": Sold " + quantity + " units of " + sellStock.getSymbol() +
                                    " at price " + sellCurrentPrice);
                            break;
                        }
                        quantity--;

                        if (quantity <= 0) {
                            System.out.println("Not enough stock to sell.");
                            sellAction = "Not enough stock to sell";
                            break;
                        }
                    } while (true);
                } else {
                    sellAction = "Hold";
                }
            }

            // Log buy and sell advice with corresponding actions
            getBuy_Advice_VS_action().put(buyAdviceMessage, buyAction);
            getSell_Advice_VS_action().put(sellAdviceMessage, sellAction);
        } catch(Exception e){
            // Random factor and market setup
            double random = Math.random();
            Random rand = new Random();
            List<Stocks> marketStocks = market.getListStock();

            // Check if sufficient stocks are available in the market
            if (marketStocks.size() < 2) {
                System.out.println(this.getName() + ": Not enough stocks in the market for trading.");
                return;
            }

            // Randomly select stocks for buy and sell actions
            Stocks buyStock = marketStocks.get(rand.nextInt(marketStocks.size() - 1));



            // Calculate moving averages for buy and sell stocks
            double buyMovingAverage = calculate(this.period, buyStock.getPriceHistory());
            double buyCurrentPrice = buyStock.getPrice();


            // Initialize advice and action variables
            String buyAdviceMessage;
            String sellAdviceMessage="No Stocks in Portfolio";
            String buyAction;
            String sellAction="No Stocks in Portfolio";
            Boolean buyAdvice;


            // Generate buy advice based on moving average and threshold
            if (buyCurrentPrice < buyMovingAverage * (1 + threshold)) {
                buyAdviceMessage = "Buy: Price is Higher than the MA " + buyCurrentPrice + " < " + buyMovingAverage * (1 + threshold);
                buyAdvice = true;
            } else {
                buyAdviceMessage = "Hold: Price is around than the MA " + buyCurrentPrice + " ~= " + buyMovingAverage * (1 + threshold);
                buyAdvice = false;
            }



            // Execute buy action based on advice
            if (random < 0.3) {
                System.out.println(randomExcuses());
                buyAction = randomExcuses();
                sellAction=randomExcuses();
            } else {

                if (buyAdvice) {
                    do {
                        if (getCash() >= quantity * buyCurrentPrice) {
                            buy(buyStock, quantity, buyCurrentPrice);
                            dailyTradeCount++;

                            buyAction = "Bought " + quantity + " units of " + buyStock.getSymbol() +
                                    " at price " + buyCurrentPrice;
                            System.out.println(this.getName() + ": Bought " + quantity + " units of " + buyStock.getSymbol() +
                                    " at price " + buyCurrentPrice);
                            break;
                        }

                        quantity--; // Reduce quantity by 1

                        if (quantity <= 0) {
                            System.out.println(this.getName() + ": Not enough cash to buy stock " + buyStock.getSymbol());
                            buyAction = "Not enough cash to buy stock " + buyStock.getSymbol();
                            break;
                        }
                    } while (true);
                } else {
                    buyAction = "Hold";
                }
            }

            // Log buy and sell advice with corresponding actions
            getBuy_Advice_VS_action().put(buyAdviceMessage, buyAction);
            getSell_Advice_VS_action().put(sellAdviceMessage, sellAction);

        }

    }

    // Override getName to include trader type
    public String getName() {
        return super.getName() + " (Moving Average)";
    }
}
