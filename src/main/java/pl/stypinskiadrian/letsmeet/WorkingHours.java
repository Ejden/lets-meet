package pl.stypinskiadrian.letsmeet;

public class WorkingHours {
    private Time start;
    private Time end;

    public WorkingHours(Time start, Time end) {
        this.start = start;
        this.end = end;
    }

    public Time getStart() {
        return start;
    }

    public void setStart(Time start) {
        this.start = start;
    }

    public Time getEnd() {
        return end;
    }

    public void setEnd(Time end) {
        this.end = end;
    }
}
