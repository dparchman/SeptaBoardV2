public class TripDetails extends Trip {
    private String scheduledArrival;

    public TripDetails(String tripId, String status, String delay, String scheduledArrival) {
        super(tripId, status, delay);
        this.scheduledArrival = scheduledArrival;
    }

    public String getScheduledArrival() {
        return scheduledArrival;
    }
}
