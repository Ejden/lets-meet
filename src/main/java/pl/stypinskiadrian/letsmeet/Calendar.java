package pl.stypinskiadrian.letsmeet;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Calendar {
    private final WorkingHours workingHours;
    private final List<Meeting> plannedMeetings;


    public Calendar(@NotNull WorkingHours workingHours) {
        this.workingHours = workingHours;
        plannedMeetings = new ArrayList<>();
    }

    public WorkingHours getWorkingHours() {
        return workingHours;
    }

    public List<Meeting> getPlannedMeetings() {
        return Collections.unmodifiableList(plannedMeetings);
    }

    public void addMeeting(@NotNull Meeting meeting) {
        plannedMeetings.add(meeting);
    }
}
