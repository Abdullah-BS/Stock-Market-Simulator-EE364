import java.util.List;

public class Stocks{

    String symbol;
    String companyName;
    List<Double> priceHistory;
    double price;

    public Stocks(String symbol, String companyName, List<Double> priceHistory, double price){
        this.symbol = symbol;
        this.companyName = companyName;
        this.priceHistory = priceHistory;
        this.price = price;

    }

    double getPrice(){
        return this.price;
}

    String getPriceHistory(){
        return this.priceHistory.toString();
    }

    String getSymbol(){
        return this.symbol;
    }
    
    String getCompanyName(){
        return this.companyName;
    }


}
