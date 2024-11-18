import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TripUpdater {

    public static List<TripDetails> fetchTripUpdates(List<Trip> trips, String targetStopId) throws Exception {
        List<TripDetails> tripDetailsList = new ArrayList<>();

        for (Trip trip : trips) {
            String tripId = trip.getTripId();
            String apiUrl = "https://www3.septa.org/api/v2/trip-update/?trip_id=" + tripId;
            String jsonResponse = fetchJsonFromUrl(apiUrl);

            // Find the scheduled arrival time for the specified stop ID
            String scheduledArrival = findScheduledArrivalTime(jsonResponse, targetStopId);

            // Add details to the list
            tripDetailsList.add(new TripDetails(tripId, trip.getStatus(), trip.getDelay(), scheduledArrival));
        }

        return tripDetailsList;
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

    private static String findScheduledArrivalTime(String jsonResponse, String targetStopId) {
        jsonResponse = jsonResponse.trim();
        if (jsonResponse.startsWith("{")) jsonResponse = jsonResponse.substring(1, jsonResponse.length() - 1);

        String[] stops = jsonResponse.split("\\},\\{");

        for (String stop : stops) {
            stop = cleanJsonObject(stop);

            String stopId = extractValue(stop, "\"stop_id\":");
            if (stopId.equals(targetStopId)) {
                String arrivalTime = extractValue(stop, "\"arrival_time\":");

                // Normalize single-digit hour format to HH:mm:ss
                if (arrivalTime.matches("\\d:\\d{2}:\\d{2}")) {
                    arrivalTime = "0" + arrivalTime;
                }

                return arrivalTime;
            }
        }

        return "Scheduled arrival time not found";
    }

    private static String cleanJsonObject(String jsonObject) {
        if (!jsonObject.startsWith("{")) jsonObject = "{" + jsonObject;
        if (!jsonObject.endsWith("}")) jsonObject = jsonObject + "}";
        return jsonObject;
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
