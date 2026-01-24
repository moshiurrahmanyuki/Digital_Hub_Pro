package bd.edu.seu.digitalhubpro.user.model;

public class DurationFormatter {
    public DurationFormatter() {
    }

    public static String formatDuration(Long totalSeconds) {
        if (totalSeconds != null && totalSeconds >= 0L) {
            long minutes = totalSeconds / 60L;
            long seconds = totalSeconds % 60L;
            return String.format("%02d:%02d", minutes, seconds);
        } else {
            return "00:00";
        }
    }
}
