import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        int index=0;
        for (String traderName : traderNames){


               //temp

        if (index == 2){
            listOfTraders.add(new RandomTrader(traderName, this.marketSimulator));
            index++;
        }

        else if (index == 1){
            listOfTraders.add(new MovingAverageTrader(traderName, 3,this.marketSimulator));
            index++;
        }
        else if (index == 0){
            listOfTraders.add(new TradingBotTrader(traderName, 3, this.marketSimulator));
            index++;

        }
        else {
            listOfTraders.add(new RSITrader(traderName, 3, this.marketSimulator));
            index++;
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
            if(trader instanceof TradingBotTrader){
                trader.execute(marketSimulator,randomStock, quantity);
                ((TradingBotTrader) trader).resetDailyTradeFlag();


            }
            trader.execute(marketSimulator,randomStock, quantity);
            try {
                List<Stocks> stocksList = new ArrayList<Stocks>(trader.stockPortfolio.keySet());
                Stocks portStock = stocksList.get(random.nextInt(listOfStocks.size()));
                trader.execute(marketSimulator,portStock, quantity);
            }catch (Exception e){
                System.out.println("NO Stock in portfolio");
            }

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

//    private void printWithDelay(String message) {
//        System.out.println(message);
//        try {
//            TimeUnit.MILLISECONDS.sleep(500); // 0.5 second delay
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//    }

    // initialize the app



    public static void main (String[] args){
            MainApp app = new MainApp();
            app.runSimulation();

    }

}
