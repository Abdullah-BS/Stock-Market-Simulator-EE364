import java.util.ArrayList;
import java.util.List;

public class Stocks{

    private String symbol; // Stock symbol (e.g., "AAPL")
    private String companyName; // Name of the company (e.g., "Apple Inc.")
    private double price; // Current price of the stock
    private List<Double> priceHistory; // List of historical prices for the stock

    // Constructor to initialize stock details
    public Stocks(String symbol, String companyName, List<Double> priceHistory){
        this.symbol = symbol;
        this.companyName = companyName;
        this.priceHistory = new ArrayList<>(priceHistory);
        this.price= (getPriceHistory().getLast());


    }

    // Method to get the current price of the stock (rounded to 2 decimal places)
    public double getPrice(){
        return Math.round(this.price * 100.0) / 100.0;
}

    // Method to get a copy of the price history
    public List<Double> getPriceHistory() {
        return new ArrayList<>(priceHistory);
    }

    // Method to get the stock symbol
    public String getSymbol(){
        return this.symbol;
    }


    // Method to get the company name
    public String getCompanyName(){
        return this.companyName;
    }

    // Method to update the stock price and add the new price to the history
    public void setPrice(double price) {
        this.price = price;
        this.priceHistory.add(price);
    }

    // Method to get a string representation of the stock object
    public String toString() {
        return "Stock{" +
                "symbol='" + symbol + '\'' +
                ", price=" + price +
                '}';
    }



}
