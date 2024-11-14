import java.util.ArrayList;
import java.util.List;

public class Stocks{

    private String symbol;
    private String companyName;
    private double price;
    private List<Double> priceHistory;

    public Stocks(String symbol, String companyName, double price){
        this.symbol = symbol;
        this.companyName = companyName;
        this.price = price;
        this.priceHistory = new ArrayList<>();

    }

    public double getPrice(){
        return this.price;
}

    public List<Double> getPriceHistory() {
        return new ArrayList<>(priceHistory);
    }

    public String getSymbol(){
        return this.symbol;
    }

    
    public String getCompanyName(){
        return this.companyName;
    }

    public void setPrice(double price) {
        this.price = price;
        this.priceHistory.add(price);
    }
    public String toString() {
        return "Stock{" +
                "symbol='" + symbol + '\'' +
                ", price=" + price +
                '}';
    }



}
