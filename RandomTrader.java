import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomTrader extends Trader {

    private final Random random;


    public RandomTrader(String name, MarketSimulator market) {
        super(name, market);
        this.random = new Random();

    }

    @Override
    public double calculate(int period, List<Double> priceHistory) {
        return random.nextDouble(); // Random value for calculation
    }

    @Override
    public void execute(MarketSimulator market,Stocks stock, int quantity) {
        try {

            // Random decision to buy or sell
            boolean buyOrSell = new Random().nextBoolean();
            double randomValue = Math.random();

            // Market stocks and portfolio stocks
            List<Stocks> marketStocks = market.getListStock();
            List<Stocks> portfolioStocks = new ArrayList<>(stockPortfolio.keySet());

            // Check if portfolio is empty before selling
            if (!buyOrSell && portfolioStocks.isEmpty()) {
                System.out.println(this.getName() + ": No stocks available to sell.");
                return;
            }

            // Random excuse to skip trading
            if (randomValue < 0.5) {
                System.out.println(randomExcuses());
                return;
            }

            if (buyOrSell) {
                // Randomly select stock to buy
                Stocks buyStock = marketStocks.get(new Random().nextInt(marketStocks.size()));
                double buyPrice = buyStock.getPrice();

                // Buy logic
                if (getCash() >= quantity * buyPrice) {
                    buy(buyStock, quantity, buyPrice);
                    System.out.println(this.getName() + ": Bought " + quantity + " units of " + buyStock.getSymbol() +
                            " at price " + buyPrice);
                } else {
                    System.out.println(this.getName() + ": Insufficient cash to buy stock " + buyStock.getSymbol());
                }
            } else {
                // Randomly select stock to sell
                Stocks sellStock = portfolioStocks.get(new Random().nextInt(portfolioStocks.size()));
                double sellPrice = sellStock.getPrice();

                // Sell logic
                if (stockPortfolio.get(sellStock) >= quantity) {
                    sell(sellStock, quantity, sellPrice);
                    System.out.println(this.getName() + ": Sold " + quantity + " units of " + sellStock.getSymbol() +
                            " at price " + sellPrice);
                } else {
                    System.out.println(this.getName() + ": Not enough stock to sell.");
                }
            }

            // Increment daily trade count
            dailyTradeCount++;
        } catch (Exception e) {
            System.out.println(this.getName() + ": Error occurred during trade execution.");
            e.printStackTrace();
        }
    }


    public String getName() {
        return super.getName() + " (Random)";
    }


}
