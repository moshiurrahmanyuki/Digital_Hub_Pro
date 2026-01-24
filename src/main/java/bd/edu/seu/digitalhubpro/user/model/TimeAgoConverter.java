package bd.edu.seu.digitalhubpro.user.model;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;

@Component
public class TimeAgoConverter {
    public TimeAgoConverter() {
    }

    public String convert(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        } else {
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(dateTime, now);
            Period period = Period.between(dateTime.toLocalDate(), now.toLocalDate());
            long seconds = duration.getSeconds();
            long minutes = duration.toMinutes();
            long hours = duration.toHours();
            long days = (long)period.getDays();
            long months = period.toTotalMonths();
            long years = (long)period.getYears();
            if (seconds < 60L) {
                return "just now";
            } else if (minutes < 60L) {
                return minutes + " minute" + (minutes == 1L ? "" : "s") + " ago";
            } else if (hours < 24L) {
                return hours + " hour" + (hours == 1L ? "" : "s") + " ago";
            } else if (days < 30L) {
                long totalDays = duration.toDays();
                if (totalDays < 7L) {
                    return totalDays + " day" + (totalDays == 1L ? "" : "s") + " ago";
                } else if (totalDays < 30L) {
                    long weeks = totalDays / 7L;
                    return weeks + " week" + (weeks == 1L ? "" : "s") + " ago";
                } else {
                    return totalDays + " day" + (totalDays == 1L ? "" : "s") + " ago";
                }
            } else {
                return months < 12L ? months + " month" + (months == 1L ? "" : "s") + " ago" : years + " year" + (years == 1L ? "" : "s") + " ago";
            }
        }
    }
}
