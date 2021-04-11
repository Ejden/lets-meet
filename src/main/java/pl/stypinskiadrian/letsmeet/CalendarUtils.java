package pl.stypinskiadrian.letsmeet;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class CalendarUtils {

    /**
     *
     * @param requestedMeetingDuration in minutes
     * @param calendars calendars in which we want to search for free time
     * @return proposal free time for a meeting
     */
    public static List<FreeTime> findProposalMeetingTimes(int requestedMeetingDuration, Calendar... calendars) {
        Calendar joinedCalendar = joinCalendars(calendars);
        intersectWorkingHours(joinedCalendar, calendars);

        return findFreeTimeInCalendar(joinedCalendar, requestedMeetingDuration);
    }

    /**
     *
     * @param joinedCalendar calendar with all meetings connected together
     * @param calendars array of calendars in which we're searching for the latest start of working hours
     *                  and earliest end of working hours
     */
    private static void intersectWorkingHours(@NotNull Calendar joinedCalendar, Calendar... calendars) {
        // If there is no provided calendars just return joinedCalendar
        if (calendars.length == 0) return;

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

        // Modifying working time, now it will only contain common working hours
        joinedCalendar.getWorkingHours().setStart(latest);
        joinedCalendar.getWorkingHours().setEnd(earliest);
    }

    /**
     *
     * @param calendar in which you're searching for free time
     * @param requestedFreeTime in minutes
     * @return list of common free time that is at least as long as requestedFreeTime
     */
    private static List<FreeTime> findFreeTimeInCalendar(Calendar calendar, int requestedFreeTime) {
        List<FreeTime> freeTimes = new ArrayList<>();
        Time startTime = calendar.getWorkingHours().getStart();
        Time endTime = calendar.getWorkingHours().getEnd();

        // We don't want to work on original meetings, so we're copying them
        List<Meeting> meetings = calendar.getPlannedMeetings()
                .stream()
                .map(meeting -> new Meeting(meeting.getStart(), meeting.getEnd()))
                .collect(Collectors.toList());

        meetings = removeMeetingsOffWorkingHours(calendar.getWorkingHours().getStart(), calendar.getWorkingHours().getEnd(), meetings);

        if (meetings.size() == 0) {
            // There is no meetings in any of calendars
            if (endTime.minuteDifferenceBetween(startTime) >= requestedFreeTime) {
                freeTimes.add(new FreeTime(startTime, endTime));
            }

            return freeTimes;
        }

        for (int i = 0; ; i++) {
            if (i >= meetings.size()) {
                break;
            }

            boolean hasPrevious = i-1 >= 0;
            Meeting previous = (hasPrevious) ? meetings.get(i-1) : null;
            Meeting meeting = meetings.get(i);

            if (!hasPrevious) {
                if (meeting.getStart().minuteDifferenceBetween(startTime) >= requestedFreeTime) {
                    // There is enough time before first meeting and starting hour
                    freeTimes.add(new FreeTime(startTime, meeting.getStart()));
                }
            }

            if (i+1 >= meetings.size()) {
                // This is the last meeting in calendar
                // Check if there is enough time after previous meeting
                if (hasPrevious) {
                    if (meeting.getStart().minuteDifferenceBetween(previous.getEnd()) >= requestedFreeTime) {
                        freeTimes.add(new FreeTime(previous.getEnd(), meeting.getStart()));
                    }
                }

                // Check if there is enough time before end of working hours and end of current meeting
                if (endTime.minuteDifferenceBetween(meeting.getEnd()) >= requestedFreeTime) {
                    // There is enough time after last meeting and end of working time
                    freeTimes.add(new FreeTime(meeting.getEnd(), endTime));
                }

                break;
            }

            if (hasPrevious) {
                if (meeting.getStart().minuteDifferenceBetween(previous.getEnd()) >= requestedFreeTime) {
                    // There is enough time between two meetings
                    freeTimes.add(new FreeTime(previous.getEnd(), meeting.getStart()));
                }
            }
        }

        return freeTimes;
    }

    /**
     *
     * @param workingFrom start of working hours
     * @param workingTo end of working hours
     * @param meetings
     * @return meetings that are between workingFrom and workingTo
     */
    private static List<Meeting> removeMeetingsOffWorkingHours(Time workingFrom, Time workingTo, List<Meeting> meetings) {
        // We don't want to work on original meetings, so we're copying them
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

        // Copying all meetings, we don't want to modify original meetings
        Arrays.stream(calendars)
                .forEach(calendar -> calendar.getPlannedMeetings()
                        .forEach(meeting -> meetings.add(new Meeting(meeting.getStart().clone(), meeting.getEnd().clone())))
                );


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
