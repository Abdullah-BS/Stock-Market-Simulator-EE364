public class RandomTrader extends Trader{

    private int randomNumber;

    public RandomTrader(String name, double cash ,int randomNumber) {
        super(name, cash);
        this.randomNumber = randomNumber;
    }

    public int getRandomNumber() {return randomNumber;}

    public void setRandomNumber(int randomNumber) {this.randomNumber = randomNumber;}


    public double calculate(int period) {
        return randomNumber;
    }

    public void execute() {}

    public String getName() {
        return "Random Trader";
    }
}
