public class Event {
    private String eventName;
    private double eventImpact;

    public Event(String eventName, double eventImpact) {
        this.eventName = eventName;
        this.eventImpact = eventImpact;
}


    public String getName(){
        return eventName;
    }

    public double getImpact(){
        return eventImpact;
    }
}