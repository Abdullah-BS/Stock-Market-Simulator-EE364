import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.List;

public class MainApp {


    public MarketSimulator marketSimulator;
    public ArrayList<Trader> listOfTraders;
    public String[] traderNames = {"Abdullah", "Ahmed", "Yamin", "Saud", "Mohanned"};
    public int dayCounter = 0;
    public int quantity;
    public Random random = new Random();

    // Constructor: Make the market, create the list of traders
    public MainApp(){
        this.marketSimulator = new MarketSimulator();
        this.listOfTraders = new ArrayList<>();
        createListOfTraders();

    }

    public void createListOfTraders(){

        for (String traderName : traderNames){

        int randomNum = random.nextInt(3);
               //temp

        if (randomNum == 0){
            listOfTraders.add(new RandomTrader(traderName, this.marketSimulator));
        }

        else if (randomNum == 1){
            listOfTraders.add(new MovingAverageTrader(traderName, random.nextInt(10) + 1,this.marketSimulator));
        }

        else {
            listOfTraders.add(new RSITrader(traderName, random.nextInt(10) + 1, this.marketSimulator));
        }

        }
    }

    public List<String> simulateDay() {
        quantity = random.nextInt(10);

        dayCounter++;
        System.out.println("\n--- Day " + dayCounter + " ---");

        // Simulate daily market events and collect them
        List<String> dailyReport = marketSimulator.simulateDay();       // this method return the daily report
        for (String event : dailyReport) {
            System.out.println(event);      // print the daily report
        }

        // Execute trades for all traders
        List<Stocks> listOfStocks = marketSimulator.getListStock();
        for (Trader trader : listOfTraders) {
            Stocks randomStock = listOfStocks.get(random.nextInt(listOfStocks.size()));
            trader.execute(randomStock, quantity);
        }

        // Return the daily report for the GUI
        return dailyReport;
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
    // initialize the app
    public static void main (String[] args){
            MainApp app = new MainApp();
            app.runSimulation();

    }

}
