import java.util.*;

public class RSITrader extends Trader implements knowledgeableTrader {

    private int period;
    private static final double STOP_LOSS_PERCENTAGE = 0.10; // 10% stop-loss
    private static final double PROFIT_GRAB_PERCENTAGE = 0.35; // 35% profit-grab
    private static final int MAX_TRADES_PER_DAY = 2; // Max trades allowed per day

    public RSITrader(String name, int period, MarketSimulator market) {
        super(name, market);
        this.period = period;
    }

    @Override
    public double calculate(int period, List<Double> priceHistory) {
        // Ensure there is enough data for calculation
        try {
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

    @Override
    public void execute(MarketSimulator market,Stocks stock, int quantity) {
        if (dailyTradeCount  >= MAX_TRADES_PER_DAY) {
            System.out.println(this.getName() + ": Daily trade limit reached.");
            return;
        }

        double random = Math.random();
        Random rand = new Random();
        // Step 1: Calculate RSI for the buy and sell stocks
        List<Stocks> marketStocks = market.getListStock();
        Stocks buyStock = marketStocks.get(rand.nextInt(marketStocks.size()-1));

        List<Stocks> portStocks = new ArrayList<>(stockPortfolio.keySet());
        Stocks sellStock = portStocks.get(rand.nextInt(portStocks.size()-1));

        double buyRSI = calculate(this.period, buyStock.getPriceHistory());
        double sellRSI = calculate(this.period, sellStock.getPriceHistory());

        double buyCurrentPrice = buyStock.getPrice();
        double sellCurrentPrice = sellStock.getPrice();

        String buyAdviceMessage;
        String sellAdviceMessage;

        String buyAction;
        String sellAction;

        Boolean BuyAction = true;
        Boolean SellAction = true;

        Boolean buyAdvice=true;
        Boolean sellAdvice=true;

        // Determine the advice based on market conditions

        // BUY Advice
        if (buyRSI<30) {
            buyAdviceMessage = "Buy: RSI is Lower than the 30,  " + buyRSI + " <  30";
            System.out.println("Buy: RSI is Lower than the 30,  " + buyRSI + " <  30");
            buyAdvice = true;
        } else if (buyRSI<70&&buyRSI>30) {
            buyAdviceMessage = "Hold: RSI is Between 30 and 70,  30 < " + buyRSI + " < 70 ";
            System.out.println("Hold: RSI is Between 30 and 70,  30 < " + buyRSI + " < 70 ");
            buyAdvice = false;
        }
        else {
            buyAdviceMessage = "Don't buy RSI > 70, " + buyRSI + " >  70";
            System.out.println("Don't buy RSI > 70, " + buyRSI + " >  70");
            BuyAction = false;
        }

        //Sell Advice
        if (sellRSI > 70) {
            sellAdviceMessage = "Sell: Price is Higher than 70  " +  + sellRSI + " > 70" ;
            sellAdvice = true;
        }  else if (buyRSI<70 && buyRSI>30) {
            sellAdviceMessage = "Hold: RSI is Between 30 and 70,  30 < " + sellRSI + " < 70 ";
            sellAdvice = false;
        }
        else {
            sellAdviceMessage = "Don't Sell RSI <30, " + sellRSI + " < 30";
            SellAction = false;
        }



        // Step 3: Random excuses or actual execution
        if (random < 0.3) {
            System.out.println(randomExcuses());
            buyAction = randomExcuses();
        } else {
            if (BuyAction) {
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
            else{
                buyAction = "Don't Buy";
            }
        }

        // Sell Action
        if (random < 0.3) {
            System.out.println(randomExcuses());
            sellAction = randomExcuses();
        } else {
            if (SellAction) {
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
            else{
                sellAction = "Don't Sell";
            }
        }

        getBuy_Advice_VS_action().put(buyAdviceMessage, buyAction);
        getSell_Advice_VS_action().put(sellAdviceMessage, sellAction);

        // Step 4: Stop-loss and profit-grab logic
//        applyStopLossAndProfitGrab();

        // Step 5: Store advice and action in the hashmap
//        getBuy_Advice_VS_action().put(buyAdviceMessage, buyAction);
//        getSell_Advice_VS_action().put(sellAdviceMessage, sellAction);
    }

    // Helper method to process stop-loss and profit-grab logic
//    private void applyStopLossAndProfitGrab() {
//        Map<Stocks, Integer> portfolioCopy = new HashMap<>(getStockPortfolio());
//
//        for (Map.Entry<Stocks, Integer> entry : portfolioCopy.entrySet()) {
//            if (super().dailyTradeCount  >= MAX_TRADES_PER_DAY) break;
//
//            Stocks portfolioStock = entry.getKey();
//            int ownedQuantity = entry.getValue();
//            double currentPrice = portfolioStock.getPrice();
//            double purchasePrice = portfolioStock.getPriceHistory().get(0); // Assume first price is purchase price
//            double profitPercentage = (currentPrice - purchasePrice) / purchasePrice;
//
//            // Stop-loss logic
//            if (profitPercentage <= -STOP_LOSS_PERCENTAGE * 2) {
//                sell(portfolioStock, ownedQuantity, currentPrice);
//                super().dailyTradeCount ++;
//                System.out.println(this.getName() + ": Sold (Stop Loss) " + ownedQuantity + " units of " +
//                        portfolioStock.getSymbol() + " at price " + currentPrice);
//            }
//            // Profit-grab logic
//            else if (profitPercentage >= PROFIT_GRAB_PERCENTAGE / 2) {
//                sell(portfolioStock, ownedQuantity, currentPrice);
//                super().dailyTradeCount ++;
//                System.out.println(this.getName() + ": Sold (Profit Grab) " + ownedQuantity + " units of " +
//                        portfolioStock.getSymbol() + " at price " + currentPrice);
//            }
//        }
//    }


    @Override
    public String getName() {
        return super.getName() + " (RSI)";
    }
}
