public class Event {
    // Event name
    private String eventName;

    // Event impact
    private double eventImpact;

    // Constructor
    public Event(String eventName, double eventImpact) {
        this.eventName = eventName;
        this.eventImpact = eventImpact;
    }

    // Get event name
    public String getName(){
        return eventName;
    }

    // Get event impact
    public double getImpact(){
        return eventImpact;
    }

    // String representation of event
    public String toString() {
        return "(EventName='" + eventName + "', impact=" + eventImpact + ")";
    }

}