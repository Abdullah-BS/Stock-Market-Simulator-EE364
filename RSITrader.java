import java.util.*;

public class RSITrader extends Trader implements knowledgeableTrader {

    private int period;

    public RSITrader(String name, int period, MarketSimulator market) {
        super(name, market);  // Initialize parent class
        this.period = period; // Set the RSI period

    }

    @Override
    public double calculate(int period, List<Double> priceHistory) {
        // Ensure there is enough data for calculation
        try {
            double gain = 0;
            double loss = 0;

            // Calculate gains and losses over the specified period
            for (int j = priceHistory.size() - period + 1; j < priceHistory.size(); j++) {
                double change = priceHistory.get(j) - priceHistory.get(j - 1);
                if (change > 0) {
                    gain += change;
                } else {
                    loss -= change;
                }
            }

            // Calculate average gain and loss
            double avgGain = gain / period;
            double avgLoss = loss / period;

            // Return RSI value (0-100)
            return avgLoss == 0 ? 100 : 100 - (100 / (1 + (avgGain / avgLoss)));
        } catch (Exception e) {
            System.out.println("Error calculating RSI: " + e.getMessage());
            return 50; // Return neutral RSI value on error
        }
    }

    @Override
    public void execute(MarketSimulator market,Stocks stock, int quantity) {
        // Check if daily trade limit is reached


        double random = Math.random();
        Random rand = new Random();

        // Select random stocks from market and portfolio
        List<Stocks> marketStocks = market.getListStock();
        Stocks buyStock = marketStocks.get(rand.nextInt(marketStocks.size()-1));
        List<Stocks> portStocks = new ArrayList<>(stockPortfolio.keySet());
        Stocks sellStock = portStocks.get(rand.nextInt(portStocks.size()-1));

        // Calculate RSI for buy and sell stocks
        double buyRSI = calculate(this.period, buyStock.getPriceHistory());
        buyRSI = Math.round(buyRSI*100.0)/100.0;
        double sellRSI = calculate(this.period, sellStock.getPriceHistory());
        sellRSI = Math.round(sellRSI*100.0)/100.0;
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

        // Determine buy and sell advice based on RSI levels
        if (buyRSI<30) {
            buyAdviceMessage = "Buy: RSI is Lower than the 30,  ( " + Math.round(buyRSI*100.0)/100.0 + " <  30 )";
            buyAdvice = true;
        } else if (buyRSI<70&&buyRSI>30) {
            buyAdviceMessage = "Hold: RSI is Between 30 and 70, ( 30 < " + Math.round(buyRSI*100.0)/100.0 + " < 70 )";
            buyAdvice = false;
        }
        else {
            buyAdviceMessage = "Don't buy RSI > 70, ( " + Math.round(buyRSI*100.0)/100.0 + " >  70 )";
            BuyAction = false;
        }

        //Sell Advice
        if (sellRSI > 70) {
            sellAdviceMessage = "Sell: Price is Higher than 70,  ( " +  Math.round(sellRSI*100.0)/100.0 + " > 70 )" ;
            sellAdvice = true;
        }  else if (buyRSI<70 && buyRSI>30) {
            sellAdviceMessage = "Hold: RSI is Between 30 and 70, ( 30 < " + Math.round(sellRSI*100.0)/100.0 + " < 70 )";
            sellAdvice = false;
        }
        else {
            sellAdviceMessage = "Don't Sell RSI <30, ( " + Math.round(sellRSI*100.0)/100.0 + " < 30 )";
            SellAction = false;
        }



        // Execute random excuses or actual buy/sell actions
        if (random < 0.3) {
            System.out.println(randomExcuses());
            buyAction = randomExcuses();
        } else {
            if (BuyAction) {
                if (buyAdvice) {
                    // Buy stock if enough cash is available
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

        // Sell stock if applicable
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

        // Store the advice vs action in a map for later analysis
        getBuy_Advice_VS_action().put(buyAdviceMessage, buyAction);
        getSell_Advice_VS_action().put(sellAdviceMessage, sellAction);

    }




    @Override
    public String getName() {
        return super.getName() + " (RSI)";
    }
}
