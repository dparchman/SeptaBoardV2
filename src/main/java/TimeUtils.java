import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    // Formatters for parsing and displaying times
    private static final DateTimeFormatter FULL_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");


    public static LocalTime parseTime(String time) {
        try {
            if (time == null || time.isEmpty()) {
                throw new IllegalArgumentException("Invalid time format: null or empty");
            }
            return LocalTime.parse(time, FULL_TIME_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse time: " + time, e);
        }
    }

    public static String formatTime(LocalTime time) {
        return time.format(DISPLAY_FORMATTER);
    }

    public static LocalTime adjustTime(LocalTime scheduledTime, double delay) {
        int roundedDelay = (int) Math.round(delay);
        return roundedDelay >= 0
                ? scheduledTime.plusMinutes(roundedDelay)
                : scheduledTime.minusMinutes(Math.abs(roundedDelay));
    }

    public static long calculateMinutesUntil(LocalTime currentTime, LocalTime targetTime) {
        return Duration.between(currentTime, targetTime).toMinutes();
    }

    public static String normalizeTime(String time) {
        if (time.matches("\\d:\\d{2}:\\d{2}")) {
            return "0" + time; // Add leading zero for single-digit hours
        }
        return time;
    }
}
