import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TripFetcher {

    public static List<Trip> fetchTrips(String apiUrl) throws Exception {
        String jsonResponse = fetchJsonFromUrl(apiUrl);

        // Remove outer brackets from the JSON array
        jsonResponse = jsonResponse.trim();
        if (jsonResponse.startsWith("[")) {
            jsonResponse = jsonResponse.substring(1, jsonResponse.length() - 1);
        }

        // Split the JSON response into individual trip objects
        String[] trips = jsonResponse.split("\\},\\{");
        List<Trip> tripList = new ArrayList<>();

        for (String trip : trips) {
            // Clean up the current trip JSON object
            if (!trip.startsWith("{")) trip = "{" + trip;
            if (!trip.endsWith("}")) trip = trip + "}";

            String tripId = extractValue(trip, "\"trip_id\":");
            String status = extractValue(trip, "\"status\":");
            String delay = extractValue(trip, "\"delay\":");
            String direction = extractValue(trip, "\"direction_name\":");

            // Skip canceled trips
            if (!isValidTrip(tripId, status)) {
                continue;
            }

            tripList.add(new Trip(tripId, status, delay));
        }

        return tripList;
    }

    private static boolean isValidTrip(String tripId, String status) {
        return !tripId.equals("Unknown") && !tripId.equals("None")
                && !status.equalsIgnoreCase("CANCELED");
    }

    private static String fetchJsonFromUrl(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }

    private static String extractValue(String json, String key) {
        int startIndex = json.indexOf(key) + key.length();
        if (startIndex == -1 + key.length()) return "Unknown";
        int endIndex = json.indexOf(",", startIndex);
        if (endIndex == -1) endIndex = json.indexOf("}", startIndex);
        if (endIndex == -1) endIndex = json.length();
        String value = json.substring(startIndex, endIndex).trim();
        if (value.startsWith("\"")) value = value.substring(1);
        if (value.endsWith("\"")) value = value.substring(0, value.length() - 1);
        return value.equals("null") ? "Unknown" : value;
    }
}
