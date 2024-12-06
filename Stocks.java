import java.util.ArrayList;
import java.util.List;

public class Stocks{

    private String symbol;
    private String companyName;
    private double price;
    private List<Double> priceHistory;

    public Stocks(String symbol, String companyName, List<Double> priceHistory){
        this.symbol = symbol;
        this.companyName = companyName;
        this.priceHistory = new ArrayList<>(priceHistory);
        this.price= (getPriceHistory().getLast());


    }

    public double getPrice(){
        return Math.round(this.price * 100.0) / 100.0;
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
        System.out.println(price);
        this.priceHistory.add(price);
    }
    public String toString() {
        return "Stock{" +
                "symbol='" + symbol + '\'' +
                ", price=" + price +
                '}';
    }



}
