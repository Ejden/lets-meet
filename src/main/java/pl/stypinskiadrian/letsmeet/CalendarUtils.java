package pl.stypinskiadrian.letsmeet;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class CalendarUtils {

    public static List<FreeTime> findProposalMeetingTimes(int requestedMeetingDuration, Calendar... calendars) {
        Calendar joinedCalendar = joinCalendars(calendars);
        intersectWorkingHours(joinedCalendar, calendars);

        return findFreeTimeInCalendar(joinedCalendar, requestedMeetingDuration);
    }

    private static Calendar intersectWorkingHours(@NotNull Calendar joinedCalendar, Calendar... calendars) {
        // If there is no provided calendars just return joinedCalendar
        if (calendars.length == 0) return joinedCalendar;

        // Search for the latest start working hours
        Time latest = calendars[0].getWorkingHours().getStart();

        for (Calendar calendar : calendars) {
            Time time = calendar.getWorkingHours().getStart();

            if (latest.compareTo(time) < 0) {
                latest = time;
            }
        }

        // Search for the earliest working hours end
        Time earliest = calendars[0].getWorkingHours().getEnd();
        for (Calendar calendar : calendars) {
            Time time = calendar.getWorkingHours().getEnd();

            if (earliest.compareTo(time) > 0) {
                earliest = time;
            }
        }

        joinedCalendar.getWorkingHours().setStart(latest);
        joinedCalendar.getWorkingHours().setEnd(earliest);

        return joinedCalendar;
    }

    /**
     *
     * @param calendar in which you're searching for free time
     * @param requestedFreeTime in minutes
     * @return
     */
    private static List<FreeTime> findFreeTimeInCalendar(Calendar calendar, int requestedFreeTime) {
        List<FreeTime> freeTimes = new ArrayList<>();
        Time startTime = calendar.getWorkingHours().getStart();
        Time endTime = calendar.getWorkingHours().getEnd();

        List<Meeting> meetings = calendar.getPlannedMeetings()
                .stream()
                .map(meeting -> new Meeting(meeting.getStart(), meeting.getEnd()))
                .collect(Collectors.toList());

        for (int i = 0; ; i++) {
            boolean hasPrevious = (meetings.size() > i && i-1 >= 0);
            Meeting previous = (hasPrevious) ? meetings.get(i-1) : null;
            Meeting meeting = meetings.get(i);

            // Getting rid of meetings that are not in the working hours
            if (meeting.getEnd().minuteDifferenceBetween(startTime) < 0) {
                meetings.remove(i--);
                continue;
            }

            if (!hasPrevious) {
                if (meeting.getStart().minuteDifferenceBetween(startTime) >= requestedFreeTime) {
                    // There is enough time before first meeting and starting hour
                    freeTimes.add(new FreeTime(startTime, meeting.getStart()));
                }

                continue;
            }

            if (i+1 >= meetings.size()) {
                // This is the last meeting in calendar
                // Check if there is enough time after previous meeting
                if (meeting.getStart().minuteDifferenceBetween(previous.getEnd()) >= requestedFreeTime) {
                    freeTimes.add(new FreeTime(previous.getEnd(), meeting.getStart()));
                }

                // Check if there is enough time before end of working hours and end of current meeting
                if (endTime.minuteDifferenceBetween(meeting.getEnd()) >= requestedFreeTime) {
                    // There is enough time after last meeting and end of working time
                    freeTimes.add(new FreeTime(meeting.getEnd(), endTime));
                }

                break;
            }

            if (meeting.getStart().minuteDifferenceBetween(previous.getEnd()) >= requestedFreeTime) {
                // There is enough time between two meetings
                freeTimes.add(new FreeTime(previous.getEnd(), meeting.getStart()));
            }
        }

        return freeTimes;
    }

    private List<Meeting> removeMeetingsOffWorkingHours(Time workingFrom, Time workingTo, List<Meeting> meetings) {
        List<Meeting> result = meetings.stream()
                .map(meeting -> new Meeting(meeting.getStart().clone(), meeting.getEnd().clone()))
                .collect(Collectors.toList());

        Iterator<Meeting> iterator = result.iterator();

        while (iterator.hasNext()) {
            Meeting meeting = iterator.next();

            if (meeting.getEnd().minuteDifferenceBetween(workingFrom) < 0) {
                iterator.remove();
                continue;
            }

            if (meeting.getStart().minuteDifferenceBetween(workingTo) > 0) {
                iterator.remove();
            }
        }

        return result;
    }

    /**
     *
     * @param calendars that should be joined
     * @return calendar that connects all calendars meetings in a one whole calendar without specifying individual meetings
     * If there is a two meetings at the same time, or meetings that collides witch each other this function will connect
     * those meetings into one bigger meeting.
     * In result, if there are no meetings at specific time in any joined calendar, result calendar will have empty space in that time
     */
    private static Calendar joinCalendars(Calendar... calendars) {
        // User didn't provide any calendars, so we just simply return empty calendar with no meetings
        if (calendars.length == 0) return new Calendar(new WorkingHours(new Time("00:00"), new Time("23:59")));

        Calendar joinCalendar = new Calendar(new WorkingHours(new Time("00:00"), new Time("23:59")));

        // Adding all meetings from first calendar to joinedCalendar
        List<Meeting> meetings = new ArrayList<>();

        Arrays.stream(calendars)
                .forEach(calendar -> meetings.addAll(calendar.getPlannedMeetings()));

        // We're sorting meetings by meeting start hour
        meetings.sort(Comparator.naturalOrder());

        // Connecting calendars into one big calendar
        for (int j = 1; ; j++) {
            if (j >= meetings.size()) {
                break;
            }

            Time currMeetStart = meetings.get(j).getStart();
            Time currMeetEnd = meetings.get(j).getEnd();
            Time prevMeetStart = meetings.get(j-1).getStart();
            Time prevMeetEnd = meetings.get(j-1).getEnd();

            // Meetings starts at the same time
            if (currMeetStart.compareTo(prevMeetStart) == 0) {
                // Extend meeting to the end time of the longest meeting

                if (currMeetEnd.compareTo(prevMeetEnd) < 0) {
                    meetings.remove(j--);
                } else {
                    meetings.remove(--j);
                }

                continue;
            }

            if (currMeetStart.compareTo(prevMeetEnd) <= 0) {
                if (currMeetEnd.compareTo(prevMeetEnd) > 0) {
                    meetings.get(j - 1).setEnd(meetings.get(j).getEnd());
                }
                meetings.remove(j--);
            }
        }

        meetings.forEach(joinCalendar::addMeeting);

        return joinCalendar;
    }
}
