public class RandomTrader extends Trader{

    private double randomNumber;

    public RandomTrader(String name, double cash) {
        super(name, cash);
        this.randomNumber = Math.random();
    }

    public double getRandomNumber() {return randomNumber;}

    public void setRandomNumber(double randomNumber) {this.randomNumber = randomNumber;}


    public double calculate(int period) {
        return randomNumber;
    }

    public void execute() {}

    public String getName() {
        return "Random Trader";
    }
}
