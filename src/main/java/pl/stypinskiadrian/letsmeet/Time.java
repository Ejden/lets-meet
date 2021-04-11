package pl.stypinskiadrian.letsmeet;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Time implements Comparable<Time> {
    public static Pattern timePattern = Pattern.compile("^(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9])$");

    private final int hour;
    private final int minutes;

    public Time(int hour, int minutes) {
        if (hour >= 24 || hour < 0 || minutes >= 60 || minutes < 0) {
            throw new IllegalArgumentException("Hour should be between 0-23, minutes should be between 0-59");
        }

        this.hour = hour;
        this.minutes = minutes;
    }

    public Time(@NotNull String time) {
        Matcher matcher = timePattern.matcher(time);
        if (matcher.matches()) {
            this.hour = Integer.parseInt(matcher.group(1));
            this.minutes = Integer.parseInt(matcher.group(2));
        } else {
            throw new IllegalArgumentException();
        }
    }

    public Time clone() {
        return new Time(this.hour, this.minutes);
    }

    public int getHour() {
        return hour;
    }

    public int getMinutes() {
        return minutes;
    }

    @Override
    public int compareTo(@NotNull Time o) {
        if (this == o) return 0;
        if (this.hour > o.hour) return 1;
        if (this.hour < o.hour) return -1;

        return Integer.compare(this.minutes, o.minutes);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        if (this.hour < 10) stringBuilder.append(0);
        stringBuilder.append(hour);
        stringBuilder.append(":");
        if (this.minutes < 10) stringBuilder.append(0);
        stringBuilder.append(minutes);

        return stringBuilder.toString();
    }

    public int minuteDifferenceBetween(@NotNull Time o) {
        return (this.minutes - o.getMinutes()) + (60 * (this.hour - o.getHour()));
    }
}
