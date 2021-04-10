package pl.stypinskiadrian.letsmeet;

public class FreeTime {
    private final Time startTime;
    private final Time endTime;

    public FreeTime(Time startTime, Time endTime) {
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
