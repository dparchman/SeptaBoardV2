public class RouteStop {
    private final String routeId;
    private final String stopId;

    public RouteStop(String routeId, String stopId) {
        this.routeId = routeId;
        this.stopId = stopId;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getStopId() {
        return stopId;
    }

    @Override
    public String toString() {
        return "Route: " + routeId + ", Stop ID: " + stopId;
    }
}
