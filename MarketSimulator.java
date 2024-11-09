import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MarketSimulator {
    private ArrayList<Stocks> listStock;
    private ArrayList<Event> listEvent;
    private static final String EVENT_CSV = "events.csv";
    private static final String STOCK_CSV = "stocks.csv";

    public MarketSimulator() {
        listStock = new ArrayList<>();
        listEvent = new ArrayList<>();
        loadStock();
    }


    public void loadStock(){
        try (BufferedReader reader = new BufferedReader(new FileReader(STOCK_CSV))) {

            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(",");
                listStock.add(new Stocks(parts[0], parts[1], Double.parseDouble(parts[2])));

            }

        } catch (IOException e) {
            System.out.println("Error reading stock file: " + e.getMessage());
        }

    }

    public void intializeEvent(String name, double impact){
        Event event = new Event(name, impact);
        listEvent.add(event);
    }
     public void applyEventToStock(Stocks stock, Event event){
        double random = Math.random();
        double priceChange = event.getImpact() *(event.getImpact()+random);
        stock.setPrice(stock.getPrice() + priceChange);
     }

}
