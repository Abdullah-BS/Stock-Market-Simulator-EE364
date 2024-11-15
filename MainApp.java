import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.List;

public class MainApp {


    private MarketSimulator marketSimulator;
    private ArrayList<Trader> listOfTraders;
    private String[] traderNames = {"Abdullah", "Ahmed", "Yamin", "Saud", "Mohanned"};
    private double initialCash = 10000;
    private int dayCounter = 0;
    private int quantity;
    private Random random = new Random();

    public MainApp(){
        this.marketSimulator = new MarketSimulator();
        this.listOfTraders = new ArrayList<>();
        createListOfTraders();

    }

    public void createListOfTraders(){

        for (String traderName : traderNames){

        int randomNum = random.nextInt(3);
        randomNum = 10;         //temp

        if (randomNum == 0){
            listOfTraders.add(new RandomTrader(traderName, initialCash, this.marketSimulator));
        }

        else if (randomNum == 1){
            listOfTraders.add(new MovingAverageTrader(traderName, initialCash, random.nextInt(10) + 1,this.marketSimulator));
        }

        else {
            listOfTraders.add(new RSITrader(traderName, initialCash, random.nextInt(10) + 1, this.marketSimulator));
        }

        }
    }

    public void simulateDay(){

        quantity = random.nextInt(10);

        dayCounter++;
        System.out.println("\n--- Day " + dayCounter + " ---");
        List<String> dailyReport = marketSimulator.simulateDay();
        for (String event : dailyReport) {
            System.out.println(event);
        }

        for (Trader trader : listOfTraders){
            List<Stocks> listOfStocks = marketSimulator.getListStock();
            Stocks randomStock = listOfStocks.get(random.nextInt(listOfStocks.size()));

            trader.execute(randomStock, quantity);
        }

    }


    public void runSimulation() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nPress Enter to simulate a day, or type 'exit' to quit:");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            simulateDay();
            printResults();
        }

    }

    private void printResults() {
        System.out.println("\nInvestor Net Worth:");
        for (Trader trader : listOfTraders) {
            System.out.println(String.format("%s: $%.2f", trader.getName(), trader.calculateNetWorth(trader.getStockPortfolio())));
            System.out.println(trader.getNetWorth());
            System.out.println(trader.getWorthHistory());
        }
    }

    public static void main (String[] args){
            MainApp app = new MainApp();
            app.runSimulation();

    }

}
