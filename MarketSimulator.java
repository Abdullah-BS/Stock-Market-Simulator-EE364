import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MarketSimulator {
    private static ArrayList<Stocks> listStock;
    private static ArrayList<Event> listEvent;
    private Random random = new Random();
    private static final String EVENT_CSV = "events.csv";
    private static final String STOCK_CSV = "stocks.csv";
    private static final int DAILY_EVENTS = 5;
    private static final int MIN_AFFECTED_STOCKS = 3;
    private static final int MAX_AFFECTED_STOCKS = 6;

    public MarketSimulator() {
        listStock = new ArrayList<>();
        listEvent = new ArrayList<>();
        loadFiles(EVENT_CSV);
        loadFiles(STOCK_CSV);
    }


    public static void loadFiles(String path){

//        listStock.clear();
//        listEvent.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                
                String[] parts = line.split(",");

                if (parts.length == 3) {
                listStock.add(new Stocks(parts[0], parts[1], Double.parseDouble(parts[2])));
            }
                else {
                    listEvent.add(new Event(parts[0], Double.parseDouble(parts[1])));
                }

            }
            
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        
    }

    
    public void applyEventToStock(Stocks stock, Event event){
        double random = Math.random();
        double priceChange = stock.getPrice() *(event.getImpact()+random);
        stock.setPrice(stock.getPrice() + priceChange);
     }


    public List<String> simulateDay() {
        List<String> dailyReport = new ArrayList<>();
        dailyReport.add("Daily events:");

        for (int i = 0; i < DAILY_EVENTS; i++) {
            Event dailyEvent = listEvent.get(random.nextInt(listEvent.size()));
            int affectedStocksCount = random.nextInt(MAX_AFFECTED_STOCKS - MIN_AFFECTED_STOCKS + 1) + MIN_AFFECTED_STOCKS;
            List<Stocks> affectedStocks = getRandomStocks(affectedStocksCount);

            StringBuilder eventReport = new StringBuilder();
            eventReport.append((i + 1)).append(". ").append(dailyEvent.getName())
                    .append(" (Affected stocks: ");

            for (int j = 0; j < affectedStocks.size(); j++) {
                Stocks stock = affectedStocks.get(j);
                applyEventToStock(stock, dailyEvent);
                eventReport.append(stock.getSymbol());
                if (j < affectedStocks.size() - 1) {
                    eventReport.append(", ");
                }
            }
            eventReport.append(")");
            dailyReport.add(eventReport.toString());
        }

        return dailyReport;
    }


    public List<Stocks> getRandomStocks(int count) {
        List<Stocks> shuffledStocks = new ArrayList<>(listStock);
        Collections.shuffle(shuffledStocks);
        return shuffledStocks.subList(0, count);
    }

//    public void saveStocksToFile() {
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STOCK_CSV))) {
//            for (Stocks stock : listStock) {
//                writer.write(String.format("%s,%s,%.2f%n", stock.getSymbol(), stock.getCompanyName(), stock.getPrice()));
//            }
//        } catch (IOException e) {
//            System.err.println("Error writing stock file: " + e.getMessage());
//        }
//    }

    public List<Stocks> getListStock() {
        return listStock;
    }

}