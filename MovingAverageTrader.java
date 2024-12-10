import java.util.*;

public class MovingAverageTrader extends Trader implements knowledgeableTrader {

    private int period;
    private double threshold = 0.015;
    private static final double STOP_LOSS_PERCENTAGE = 0.10; // 10% stop-loss
    private static final double PROFIT_GRAB_PERCENTAGE = 0.45; // 45% profit-grab
    private static final int MAX_TRADES_PER_DAY = 2; // Max trades allowed per day

    public MovingAverageTrader(String name, int period, MarketSimulator market) {
        super(name, market);
        this.period = period;
    }

    public double calculate(int period, List<Double> priceHistory) {
        period = Math.min(period, priceHistory.size());

        try {
            double sum = 0.0;

            for (int i = priceHistory.size() - period + 1; i < priceHistory.size(); i++) {
                sum += priceHistory.get(i);
            }
            return sum / period;
        } catch (Exception e) {
            System.out.println("Error calculating moving average: " + e.getMessage());
            return 0.0; // Default to 0.0 to avoid incorrect trading decisions
        }
    }

    public void execute(MarketSimulator market,Stocks stock, int quantity) {
        if (dailyTradeCount  >= MAX_TRADES_PER_DAY) {
            System.out.println(this.getName() + ": Daily trade limit reached.");
            return;
        }

        double random = Math.random();
        Random rand = new Random();

        List<Stocks> marketStocks = market.getListStock();
        Stocks buyStock = marketStocks.get(rand.nextInt(marketStocks.size()));


        List<Stocks> portStocks = new ArrayList<>(stockPortfolio.keySet());
        Stocks sellStock = portStocks.get(rand.nextInt(portStocks.size()));


        double buyMovingAverage = calculate(this.period, buyStock.getPriceHistory());
        double sellMovingAverage = calculate(this.period, sellStock.getPriceHistory());

        double buyCurrentPrice = buyStock.getPrice();
        double sellCurrentPrice = sellStock.getPrice();

        String buyAdviceMessage;
        String sellAdviceMessage;

        String buyAction;
        String sellAction;

        Boolean buyAdvice;
        Boolean sellAdvice;


        // BUY Advice
        // Determine the advice based on market conditions
        if (buyCurrentPrice < buyMovingAverage * (1 + threshold)) {
            buyAdviceMessage = "Buy: Price is Higher than the MA " + buyCurrentPrice + " < " + buyMovingAverage * (1 + threshold);
            buyAdvice = true;
        } else {
            buyAdviceMessage = "Hold: Price is around than the MA " + buyCurrentPrice + " ~= " + buyMovingAverage * (1 + threshold);
            buyAdvice = false;
        }

        //Sell Advice
        if (sellCurrentPrice > sellMovingAverage * (1 + threshold)) {
            sellAdviceMessage = "Sell: Price is lower than the MA " + sellCurrentPrice + " > " + sellMovingAverage * (1 + threshold);
            sellAdvice = true;
        } else {
            sellAdviceMessage = "Hold: Price is around than the MA " + sellCurrentPrice + " ~= " + sellMovingAverage * (1 + threshold);
            sellAdvice = false;
        }

        // Buy Actions
        if (random < 0.3) {
            System.out.println(randomExcuses());
            buyAction = randomExcuses();
        } else {

            if (buyAdvice){
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
        }   else{
                buyAction = "Hold";
            }
        }

        // Sell Action
        if (random < 0.3) {
            System.out.println(randomExcuses());
            sellAction = randomExcuses();
        } else {

            if (sellAdvice){
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
            }   else{
                sellAction = "Hold";
            }
        }

//        // Stop-loss and profit-grab checks for all owned stocks
//        for (Map.Entry<Stocks, Integer> entry : new HashMap<>(getStockPortfolio()).entrySet()) {
//            if (dailyTradeCount >= MAX_TRADES_PER_DAY) break;
//
//            Stocks portfolioStock = entry.getKey();
//            int ownedQuantity = entry.getValue();
//            double purchasePrice = portfolioStock.getPriceHistory().get(portfolioStock.getPriceHistory().size() - 1); // Last price as purchase price
//            double profitPercentage = (currentPrice - purchasePrice) / purchasePrice;
//
////             Stop-loss logic
//            if (profitPercentage <= -STOP_LOSS_PERCENTAGE) {
//                sell(portfolioStock, ownedQuantity, currentPrice);
//                dailyTradeCount++;
//                System.out.println(this.getName() + ": Sold (Stop Loss) " + ownedQuantity + " units of " +
//                        portfolioStock.getSymbol() + " at price " + currentPrice);
//                return; // Exit after executing stop-loss
//            }
//
//            // Profit-grab logic
//            if (profitPercentage >= PROFIT_GRAB_PERCENTAGE) {
//                sell(portfolioStock, ownedQuantity, currentPrice);
//                dailyTradeCount++;
//                System.out.println(this.getName() + ": Sold (Profit Grab) " + ownedQuantity + " units of " +
//                        portfolioStock.getSymbol() + " at price " + currentPrice);
//                return; // Exit after executing profit-grab
//            }
//        }


        getBuy_Advice_VS_action().put(buyAdviceMessage, buyAction);
        getSell_Advice_VS_action().put(sellAdviceMessage, sellAction);
    }

    public String getName() {
        return super.getName() + " (Moving Average)";
    }
}
