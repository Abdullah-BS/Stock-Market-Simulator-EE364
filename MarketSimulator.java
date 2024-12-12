import java.io.*;
import java.util.*;

public class MarketSimulator {
    private static ArrayList<Stocks> listStock;
    private static ArrayList<Event> listEvent;
    private Random random = new Random();
    private static final String EVENT_CSV = "events.csv";
    private static final String STOCK_CSV = "stocks.csv";
    private static final int DAILY_EVENTS = 5;
    private static final int MIN_AFFECTED_STOCKS = 2;
    private static final int MAX_AFFECTED_STOCKS = 3;
    private static final int MIN_EVENT_DURATION = 5;
    private static final int MAX_EVENT_DURATION = 10;

    // Map to track ongoing events: key is the stock, value is a list of EventEffect
    private Map<Stocks, List<EventEffect>> ongoingEvents;

    public MarketSimulator() {
        // Initialize lists and map to store stocks, events, and ongoing events
        listStock = new ArrayList<>();
        listEvent = new ArrayList<>();
        ongoingEvents = new HashMap<>();

        // Load data from CSV files
        loadFiles(EVENT_CSV);
        loadFiles(STOCK_CSV);
    }

    public static void loadFiles(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                // Check if line represents stock data (more than 2 parts)
                if (parts.length > 2) {
                    List<Double> priceHistory = new ArrayList<>();

                    String symbol = parts[0];
                    String company = parts[1];

                    for (int i = 2; i < parts.length; i++) {
                        priceHistory.add(Double.parseDouble(parts[i]));
                    }

                    // Add stock to the list
                    listStock.add(new Stocks(symbol, company, priceHistory));

                } else {
                    // Add event data to the list
                    listEvent.add(new Event(parts[0], Double.parseDouble(parts[1])));
                }
            }

        } catch (IOException e) {
            // Handle file reading errors
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // Apply an event's effect to a stock with capping
    public void applyEventToStock(Stocks stock, Event event, double multiplier) {
        double originalPrice = stock.getPrice();
        double priceChange = originalPrice * event.getImpact() * multiplier;
        double newPrice = stock.getPrice() + priceChange;

        // Cap the new price to be at least 25% of the original price and at most 200% (double)
        double cappedPrice = Math.max(originalPrice * 0.25, Math.min(originalPrice * 2.0, newPrice));

        stock.setPrice(cappedPrice);
    }

    private double simulateTrendChange() {
        double dailyBias = 0.001; // Example: 0.1% daily trend up or down
        return random.nextBoolean() ? dailyBias : -dailyBias;
    }

    // Simulate a daily trend effect on a given stock
    public void applyDailyTrend(Stocks stock) {
        double currentPrice = stock.getPrice();
        double trendChange = simulateTrendChange();

        // Apply a small bias to simulate up or down market trend
        double newPrice = currentPrice * (1 + trendChange);
        stock.setPrice(newPrice);
    }

    // Simulate a single day
    public List<String> simulateDay() {
        List<String> dailyReport = new ArrayList<>();
        dailyReport.add("Daily events:");

        // Apply daily trends to all stocks
        for (Stocks stock : listStock) {
            applyDailyTrend(stock);
        }
        // Process ongoing events
        processOngoingEvents(dailyReport);

        // Set to track stocks already affected today
        Set<Stocks> affectedToday = new HashSet<>();

        // Add new daily events
        for (int i = 0; i < DAILY_EVENTS; i++) {
            Event dailyEvent = listEvent.get(random.nextInt(listEvent.size()));
            int affectedStocksCount = random.nextInt(MAX_AFFECTED_STOCKS - MIN_AFFECTED_STOCKS + 1) + MIN_AFFECTED_STOCKS;
            List<Stocks> affectedStocks = getRandomStocks(affectedStocksCount);

            StringBuilder eventReport = new StringBuilder();
            eventReport.append((i + 1)).append(". ").append(dailyEvent.getName())
                    .append(" (Affected stocks: ");

            for (int j = 0; j < affectedStocks.size(); j++) {
                Stocks stock = affectedStocks.get(j);

                // Skip if the stock is already affected by another event
                if (affectedToday.contains(stock)) {
                    continue;
                }

                // Apply the event for the first day
                applyEventToStock(stock, dailyEvent, 1.0);

                // Schedule the event for multiple days
                scheduleEvent(stock, dailyEvent);

                // Mark this stock as affected
                affectedToday.add(stock);

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


    // Process ongoing events
    private void processOngoingEvents(List<String> dailyReport) {
        for (Stocks stock : new ArrayList<>(ongoingEvents.keySet())) {
            List<EventEffect> effects = ongoingEvents.get(stock);
            effects.removeIf(effect -> {
                applyEventToStock(stock, effect.getEvent(), getFadingMultiplier(effect.getInitialDays() - effect.getDaysLeft()));
                effect.decrementDaysLeft();
                return effect.getDaysLeft() <= 0; // Remove if the event has ended
            });

            if (effects.isEmpty()) {
                ongoingEvents.remove(stock); // Remove stock if no more effects
            }
        }
    }


    // Schedule an event to affect a stock for multiple days
    private void scheduleEvent(Stocks stock, Event event) {
        int duration = random.nextInt(MAX_EVENT_DURATION - MIN_EVENT_DURATION + 1) + MIN_EVENT_DURATION;
        EventEffect effect = new EventEffect(event, duration);

        ongoingEvents.computeIfAbsent(stock, k -> new ArrayList<>()).add(effect);
    }

    // Get random stocks
    public List<Stocks> getRandomStocks(int count) {
        List<Stocks> shuffledStocks = new ArrayList<>(listStock);
        Collections.shuffle(shuffledStocks);
        return shuffledStocks.subList(0, count);
    }

    public ArrayList<Stocks> getListStock() {
        return listStock;
    }
    private double getFadingMultiplier(int elapsedDays) {
        double[] multipliers = {1.0, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1};
        return elapsedDays < multipliers.length ? multipliers[elapsedDays] : 0.1; // Default to 10% for days beyond the array
    }

    // Inner class to track ongoing event effects
    private static class EventEffect {
        private final Event event;
        private final int initialDays; // Tracks the original duration of the event
        private int daysLeft;

        public EventEffect(Event event, int daysLeft) {
            this.event = event;
            this.daysLeft = daysLeft;
            this.initialDays = daysLeft; // Store the initial duration
        }

        public Event getEvent() {
            return event;
        }

        public int getDaysLeft() {
            return daysLeft;
        }

        public void decrementDaysLeft() {
            daysLeft--;
        }

        public int getInitialDays() {
            return initialDays; // Return the original duration
        }
    }

}
