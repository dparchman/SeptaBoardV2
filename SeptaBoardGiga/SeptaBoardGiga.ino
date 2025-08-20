#include <WiFi.h>
#include <WiFiClientSecure.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>

// Replace with your network credentials
const char* ssid = "YOUR_SSID";
const char* password = "YOUR_PASSWORD";

// Endpoint serving trip information
const char* apiEndpoint = "https://api.septa.org/trainview/";

// Structures mirroring the JVM project's Trip and TripDetails
struct Trip {
  String tripId;
  String status;
  String delay;
};

struct TripDetails : Trip {
  String scheduledArrival;
};

void connectWiFi();
void fetchTrips();

void setup() {
  Serial.begin(115200);
  while (!Serial) {
    ;
  }
  connectWiFi();
}

void loop() {
  fetchTrips();
  delay(60000); // Update every minute
}

void connectWiFi() {
  Serial.print("Connecting to WiFi");
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print('.');
  }
  Serial.println();
  Serial.print("Connected. IP address: ");
  Serial.println(WiFi.localIP());
}

void fetchTrips() {
  WiFiClientSecure client;
  client.setInsecure(); // For simplicity; load CA certificate in production

  HTTPClient https;
  if (!https.begin(client, apiEndpoint)) {
    Serial.println("HTTPS connection failed");
    return;
  }

  int httpCode = https.GET();
  if (httpCode <= 0) {
    Serial.printf("GET failed: %s\n", https.errorToString(httpCode).c_str());
    https.end();
    return;
  }

  String payload = https.getString();
  https.end();

  // Adjust size to match expected JSON and available memory
  StaticJsonDocument<1024> doc;
  DeserializationError err = deserializeJson(doc, payload);
  if (err) {
    Serial.print("JSON parse error: ");
    Serial.println(err.c_str());
    return;
  }

  JsonObject first = doc[0]; // Expecting an array of trips
  TripDetails trip;
  trip.tripId = first["trip_id"] | "";
  trip.status = first["status"] | "";
  trip.delay = first["delay"] | "";
  trip.scheduledArrival = first["sched_arrival"] | "";

  Serial.println("First Trip:");
  Serial.print("ID: "); Serial.println(trip.tripId);
  Serial.print("Status: "); Serial.println(trip.status);
  Serial.print("Delay: "); Serial.println(trip.delay);
  Serial.print("Scheduled Arrival: "); Serial.println(trip.scheduledArrival);
}
