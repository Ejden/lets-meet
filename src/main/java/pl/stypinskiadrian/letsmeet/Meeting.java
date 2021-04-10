package pl.stypinskiadrian.letsmeet;

import org.jetbrains.annotations.NotNull;

public class Meeting implements Comparable<Meeting> {

    private Time start;
    private Time end;

    public Meeting(Time start, Time end) {
        this.start = start;
        this.end = end;
    }

    public Time getStart() {
        return start;
    }

    public Time getEnd() {
        return end;
    }

    public void setStart(Time start) {
        this.start = start;
    }

    public void setEnd(Time end) {
        this.end = end;
    }

    @Override
    public int compareTo(@NotNull Meeting o) {
        if (this == o) return 0;

        Time thisTime = start;
        Time oTime = o.start;

        if (thisTime.getHour() > oTime.getHour()) return 1;
        if (thisTime.getHour() < oTime.getHour()) return -1;

        return Integer.compare(thisTime.getMinutes(), oTime.getMinutes());
    }
}
