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

    public String getPriceHistory(){
        return this.priceHistory.toString();
    }

    public String getSymbol(){
        return this.symbol;
    }

    
    public String getCompanyName(){
        return this.companyName;
    }

    public void addPrice(double newPrice) {
        priceHistory.add(newPrice);
    }



}
