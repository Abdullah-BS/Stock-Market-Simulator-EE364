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

    public MainApp(){
        this.marketSimulator = new MarketSimulator();
        this.listOfTraders = new ArrayList<>();
        createListOfTraders();

    }

    public void createListOfTraders(){

        for (String traderName : traderNames){

        Random random = new Random();
        int randomNum = random.nextInt(3);
        if (randomNum == 0){
            listOfTraders.add(new RandomTrader(traderName, initialCash));
        }

        else if (randomNum == 1){
            listOfTraders.add(new MovingAverageTrader(traderName, initialCash, random.nextInt(10) + 1));
        }

        else {
            listOfTraders.add(new RSITrader(traderName, initialCash, random.nextInt(10) + 1));
        }

        }
    }

    public void simulateDay(){
        Random random = new Random();
        dayCounter++;
        System.out.println("\n--- Day " + dayCounter + " ---");
        List<String> dailyReport = marketSimulator.simulateDay();
        for (String event : dailyReport) {
            System.out.println(event);
        }

        for (Trader trader : listOfTraders){
            List<Stocks> listOfStocks = marketSimulator.getListStock();
            Stocks randomStock = listOfStocks.get(random.nextInt(listOfStocks.size()));

            trader.execute(randomStock, random.nextInt(10));
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
        }
    }

    public static void main (String[] args){
            MainApp app = new MainApp();
            app.runSimulation();

    }

}
