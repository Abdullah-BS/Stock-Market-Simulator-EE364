import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MainApp {


    private static MarketSimulator marketSimulator;
    private static ArrayList<Trader> listOfTraders;
    private static String[] traderNames = {"Abdullah", "Ahmed", "Yamin", "Saud", "Mohanned"};
    private static double initialCash = 10000;
    private static int dayCounter = 0;

    public MainApp(){
        this.marketSimulator = new MarketSimulator();
        this.listOfTraders = new ArrayList<>();
        createListOfTraders();
        runSimulation();

    }

    public static void createListOfTraders(){

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
        dayCounter++;
        marketSimulator.applyEventToStock();
        System.out.println("\n--- Day " + dayCounter + " ---");
        for (Trader trader : listOfTraders){
            trader.execute();
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
        }

    }

    public static void main (String[] args){
            MainApp app = new MainApp();

    }

}
