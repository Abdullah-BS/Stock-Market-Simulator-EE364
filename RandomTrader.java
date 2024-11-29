import java.util.List;
import java.util.Random;

public class RandomTrader extends Trader{

    private double randomNumber;

    public RandomTrader(String name, MarketSimulator market) {
        super(name, market);
        this.randomNumber = Math.random();  // 0 - 0.99
    }

    public double getRandomNumber() {return randomNumber;}

    public void setRandomNumber(double randomNumber) {this.randomNumber = randomNumber;}

    //  What is the use of Calculate in randomTrader??
    public double calculate(int period, List<Double> priceHistory) {
        return randomNumber;
    }

    // what to do with the period in Random????
    public void execute(Stocks stock, int quantity) {
        List<Double> priceHistory = stock.getPriceHistory();
        Random random=new Random();
        boolean buyOrSell = random.nextBoolean() ;
        
            
        // Check if the period is within the limit of the array then Execute

//            double value = calculate(10, priceHistory);
            double currentPrice = stock.getPrice();
        
            // needs editing
            // buy and sell method must be boolean!!
            if (buyOrSell) {

                System.out.println("buying stock");
                buy(stock, quantity, currentPrice);
            }
            else {

                System.out.println("selling stock");
                sell(stock, quantity, currentPrice);

            }
    }


    public String getName() {
        return super.getName() + "(Random Strategy)";
    }
}
