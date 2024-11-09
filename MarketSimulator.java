import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MarketSimulator {
    private static ArrayList<Stocks> listStock;
    private static ArrayList<Event> listEvent;
    private static final String EVENT_CSV = "events.csv";
    private static final String STOCK_CSV = "stocks.csv";

    public MarketSimulator() {
        listStock = new ArrayList<>();
        listEvent = new ArrayList<>();
        loadFiles(EVENT_CSV);
        loadFiles(STOCK_CSV);
    }


    public static void loadFiles(String path){

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

}
