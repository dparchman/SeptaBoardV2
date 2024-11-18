public class Trip {
    private String tripId;
    private String status;
    private String delay;

    public Trip(String tripId, String status, String delay) {
        this.tripId = tripId;
        this.status = status;
        this.delay = delay;
    }

    public String getTripId() {
        return tripId;
    }

    public String getStatus() {
        return status;
    }

    public String getDelay() {
        return delay;
    }
}
