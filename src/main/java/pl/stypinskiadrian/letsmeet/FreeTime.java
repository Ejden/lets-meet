package pl.stypinskiadrian.letsmeet;

import org.jetbrains.annotations.NotNull;

public class FreeTime {
    private final Time startTime;
    private final Time endTime;

    public FreeTime(@NotNull Time startTime, @NotNull Time endTime) {
        if (endTime.minuteDifferenceBetween(startTime) < 0) {
            throw new TimeChronologyException("startTime should be before endTime!");
        }

        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "{" +
                "startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
