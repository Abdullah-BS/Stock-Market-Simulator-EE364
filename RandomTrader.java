import java.util.List;

public class RandomTrader extends Trader{

    private double randomNumber;

    public RandomTrader(String name, double cash) {
        super(name, cash);
        this.randomNumber = Math.random();
    }

    public double getRandomNumber() {return randomNumber;}

    public void setRandomNumber(double randomNumber) {this.randomNumber = randomNumber;}

    //  What is the use of Calculate in randomTrader??
    public double calculate(int period) {
        return randomNumber;
    }

    // what to do with the period in Random????
    public void execute(int period,Stocks stock, int quantity) {
        List<Double> priceHistory = stock.getPriceHistory();
        // Check if the period is within the limit of the array then Execute
        if (stock.getPriceHistory().size() < period + 1) {

            double value = calculate(period);
            double currentPrice = stock.getPrice();

            // needs editing
            // buy and sell method must be boolean!!
            if (buy(stock.getSymbol(), quantity, currentPrice)) {

                System.out.println("buying stock");
                buy(stock.getSymbol(), quantity, currentPrice);
            }
            else if (sell(stock.getSymbol(), quantity, currentPrice)) {

                System.out.println("selling stock");
                sell(stock.getSymbol(), quantity, currentPrice);

            }
            // editing ends here

        } else {
            System.out.println("Error");
        }
    }

    public String getName() {
        return " Random Trader ";
    }
}
