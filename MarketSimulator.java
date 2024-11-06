import java.util.ArrayList;
public class MarketSimulator {
    private ArrayList<Stocks> listStock;
    private ArrayList<Event> listEvent;

    public MarketSimulator() {
        listStock = new ArrayList<>();
        listEvent = new ArrayList<>();
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
