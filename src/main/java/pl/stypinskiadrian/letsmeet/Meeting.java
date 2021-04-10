package pl.stypinskiadrian.letsmeet;

import org.jetbrains.annotations.NotNull;

public class Meeting implements Comparable<Meeting> {

    private String start;
    private String end;

    public Meeting(String start, String end) {
        this.start = start;
        this.end = end;
    }

    public String getStart() {
        return start;
    }

    public void setStart(@NotNull String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(@NotNull String end) {
        this.end = end;
    }

    @Override
    public int compareTo(@NotNull Meeting o) {
        if (this == o) return 0;

        CalendarUtils.Time thisTime = CalendarUtils.getParsedTime(start);
        CalendarUtils.Time oTime = CalendarUtils.getParsedTime(o.start);

        if (thisTime.getHour() > oTime.getHour()) return 1;
        if (thisTime.getHour() < oTime.getHour()) return -1;

        return Integer.compare(thisTime.getMinutes(), oTime.getMinutes());
    }
}
