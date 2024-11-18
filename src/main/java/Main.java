import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // Step 1: Define routes and stops
        List<RouteStop> routeStops = new ArrayList<>();
        routeStops.add(new RouteStop("4", "17168")); // Route 4, Stop ID 17168
        routeStops.add(new RouteStop("4", "17266")); // Route 4, Stop ID 17266
        routeStops.add(new RouteStop("16", "17168")); // Route 16, Stop ID 17168
        routeStops.add(new RouteStop("16", "17266")); // Route 16, Stop ID 17266
        routeStops.add(new RouteStop("43", "689")); // Route 43, Stop ID 689
        routeStops.add(new RouteStop("43", "681")); // Route 43, Stop ID 681

        // Step 2: Define formatters
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("HH:mm"); // For output without seconds

        while (true) { // Infinite loop for refreshing data
            try {
                String currentTimeFormatted = LocalDateTime.now().format(displayFormatter);
                LocalTime currentTime = LocalTime.now();
                System.out.println("Final Trip Details (as of " + currentTimeFormatted + "):");

                for (RouteStop routeStop : routeStops) {
                    System.out.println(routeStop); // Display current route and stop information

                    // Fetch trips and updates
                    String tripsApiUrl = "https://www3.septa.org/api/v2/trips/?route_id=" + routeStop.getRouteId();
                    List<Trip> trips = TripFetcher.fetchTrips(tripsApiUrl);
                    List<TripDetails> tripDetails = TripUpdater.fetchTripUpdates(trips, routeStop.getStopId());

                    // Process each trip detail
                    for (TripDetails detail : tripDetails) {
                        processTripDetail(detail, currentTime);
                    }
                }

                // Step 3: Wait for 30 seconds before refreshing
                Thread.sleep(30000);

            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void processTripDetail(TripDetails detail, LocalTime currentTime) {
        try {
            String scheduledArrival = detail.getScheduledArrival();
            String delay = detail.getDelay();

            // Skip the trip if scheduledArrival is invalid or missing
            if (scheduledArrival == null || scheduledArrival.equals("Scheduled arrival time not found")) {
                return; // Simply return without logging or printing anything
            }

            // Parse and adjust the scheduled time
            LocalTime scheduledTime = TimeUtils.parseTime(scheduledArrival);
            double delayValue = Double.parseDouble(delay.replaceAll("[^0-9.-]", ""));
            LocalTime adjustedTime = TimeUtils.adjustTime(scheduledTime, delayValue);

            // Print the trip details only if the adjusted time is in the future
            if (adjustedTime.isAfter(currentTime)) {
                long minutesUntilArrival = TimeUtils.calculateMinutesUntil(currentTime, adjustedTime);
                System.out.println("  Trip ID: " + detail.getTripId()
                        + ", Delay: " + delay
                        + ", Scheduled Arrival: " + TimeUtils.formatTime(scheduledTime)
                        + ", Adjusted Arrival: " + TimeUtils.formatTime(adjustedTime)
                        + ", Minutes Until Arrival: " + minutesUntilArrival);
            }
        } catch (Exception e) {
            // Silently skip trips that throw errors during processing
        }
    }
}
