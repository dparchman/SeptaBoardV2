# SeptaBoard Giga

An Arduino sketch for the **Arduino Giga R1 WiFi** that fetches SEPTA trip information over HTTPS and prints the results to the serial monitor.

## Prerequisites
- [Arduino IDE 2.x](https://www.arduino.cc/en/software) or `arduino-cli`
- **Giga R1 WiFi** board package (Arduino Mbed OS boards)
- Libraries:
  - `WiFi` (bundled with the board package)
  - `HTTPClient` or `ArduinoHttpClient`
  - `ArduinoJson`

## Building and Flashing
1. Clone this repository and open the `SeptaBoardGiga` folder in the Arduino IDE.
2. Use **Sketch → Include Library → Manage Libraries** to install the required libraries.
3. Edit `SeptaBoardGiga.ino` and replace `YOUR_SSID` and `YOUR_PASSWORD` with your Wi-Fi credentials.
4. Connect the Giga R1 WiFi via USB and select **Tools → Board → Arduino Giga R1 WiFi** and the correct port.
5. Press **Upload** to compile and flash the sketch.
6. Open the Serial Monitor at 115200 baud to view the trip data.

The sketch performs an HTTPS GET request to `https://api.septa.org/trainview/`, parses the JSON using `ArduinoJson`, and prints the first trip's information. The example uses a `StaticJsonDocument<1024>` to balance memory usage with expected payload size. Increase or decrease this value as needed for your application.
