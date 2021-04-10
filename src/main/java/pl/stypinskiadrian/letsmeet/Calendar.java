package pl.stypinskiadrian.letsmeet;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class Calendar {
    private WorkingHours workingHours;
    private final List<Meeting> plannedMeetings;
    public static Pattern hourPattern = Pattern.compile("^(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9])$");


    public Calendar(@NotNull WorkingHours workingHours) {
        this.workingHours = workingHours;
        plannedMeetings = new ArrayList<>();
    }

    public WorkingHours getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(@NotNull WorkingHours workingHours) {
        this.workingHours = workingHours;
    }

    public List<Meeting> getPlannedMeetings() {
        return Collections.unmodifiableList(plannedMeetings);
    }

    public void addMeeting(@NotNull Meeting meeting) {
        plannedMeetings.add(meeting);
    }
}
