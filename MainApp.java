import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainApp {

    // Default period and variables for the market simulation
    public int period=5;
    public MarketSimulator marketSimulator;
    public ArrayList<Trader> listOfTraders;
    public String[] traderNames = {"Abdullah", "Ahmed", "Yamin"};
    public int dayCounter = 0;
    public int quantity = 5;
    public Random random = new Random();

    // Constructor: Make the market, create the list of traders
    public MainApp(Boolean isPhase1){
        this.marketSimulator = new MarketSimulator();
        this.listOfTraders = new ArrayList<>();
        createListOfTraders(isPhase1);

    }

    // Create traders depending on phase
    public void createListOfTraders(Boolean isPhase1){
        // Assign different types of traders for phase 2
        if (isPhase1 == false) {
        int index=0;
            this.traderNames = new String[] {"Abdullah", "Ahmed", "Yamin", "Saud"};
            for (String traderName : traderNames){
                if (index == 0){
                    listOfTraders.add(new TradingBotTrader(traderName, period,this.marketSimulator));
                    index++;
                }
                else if (index == 1){
                    listOfTraders.add(new MovingAverageTrader(traderName, period,this.marketSimulator));
                    index++;
                }
                else if (index == 2){
                    listOfTraders.add(new RSITrader(traderName, period, this.marketSimulator));
                    index++;

                }
                else {
                    listOfTraders.add(new RandomTrader(traderName, this.marketSimulator));
                    index++;
                }

            }
        } else {
            // Assign different types of traders for phase 1
            int index = 0;
            for (String traderName : traderNames) {
                if (index == 0) {
                    listOfTraders.add(new RSITrader(traderName, period, this.marketSimulator));
                    index++;
                } else if (index == 1) {
                    listOfTraders.add(new MovingAverageTrader(traderName, period, this.marketSimulator));
                    index++;
                } else {
                    listOfTraders.add(new RandomTrader(traderName, this.marketSimulator));
                    index++;
            }
        }
    }
    }

    // Simulate one day of trading and return the daily report
    public List<String> simulateDay() {


        dayCounter++;
        System.out.println("\n--- Day " + dayCounter + " ---");

        // Reset daily trade counts for traders
        for (Trader trader : listOfTraders) {
            trader.resetDailyTradeCount();
        }


        // Simulate daily market events and collect them
        List<String> dailyReport = marketSimulator.simulateDay();       // this method return the daily report
        for (String event : dailyReport) {
            System.out.println(event);      // print the daily report
        }

        // Execute trades for all traders
        List<Stocks> listOfStocks = marketSimulator.getListStock();
        for (Trader trader : listOfTraders) {
            Stocks randomStock = listOfStocks.get(random.nextInt(listOfStocks.size()));
            trader.execute(marketSimulator, randomStock, quantity);

        }




        // Return the daily report for the GUI
        return dailyReport;
    }



    // Run the simulation with the user
    public void runSimulation() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nPress Enter to simulate a day, or type 'exit' to quit:");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                break; // Exit if the user types 'exit'
            }
            simulateDay(); // Simulate a day of trading
            printResults(); // Print results after each day
        }

    }

    // Print results of the simulation
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
            MainApp app = new MainApp(true);
            app.runSimulation();

    }

}
