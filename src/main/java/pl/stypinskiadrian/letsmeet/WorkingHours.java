package pl.stypinskiadrian.letsmeet;

import org.jetbrains.annotations.NotNull;

public class WorkingHours {
    private Time start;
    private Time end;

    public WorkingHours(@NotNull Time start, @NotNull Time end) {
        if (end.minuteDifferenceBetween(start) < 0) {
            throw new TimeChronologyException("startTime should be before endTime!");
        }

        this.start = start;
        this.end = end;
    }

    public Time getStart() {
        return start;
    }

    public void setStart(@NotNull Time start) {
        this.start = start;
    }

    public Time getEnd() {
        return end;
    }

    public void setEnd(@NotNull Time end) {
        this.end = end;
    }
}
