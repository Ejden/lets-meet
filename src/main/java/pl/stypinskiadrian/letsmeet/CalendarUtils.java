package pl.stypinskiadrian.letsmeet;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
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
        Time latest = getParsedTime(calendars[0].getWorkingHours().getStart());

        for (Calendar calendar : calendars) {
            Time time = getParsedTime(calendar.getWorkingHours().getStart());

            if (latest.compareTo(time) < 0) {
                latest = time;
            }
        }

        // Search for the earliest working hours end
        Time earliest = getParsedTime(calendars[0].getWorkingHours().getEnd());
        for (Calendar calendar : calendars) {
            Time time = getParsedTime(calendar.getWorkingHours().getEnd());

            if (earliest.compareTo(time) > 0) {
                earliest = time;
            }
        }

        joinedCalendar.getWorkingHours().setStart(latest.toString());
        joinedCalendar.getWorkingHours().setEnd(earliest.toString());

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
        Time startTime = getParsedTime(calendar.getWorkingHours().getStart());
        Time endTime = getParsedTime(calendar.getWorkingHours().getEnd());

        List<Meeting> meetings = calendar.getPlannedMeetings()
                .stream()
                .map(meeting -> new Meeting(meeting.getStart(), meeting.getEnd()))
                .collect(Collectors.toList());

        for (int i = 0; ; i++) {
            boolean hasPrevious = (meetings.size() > i && i-1 >= 0);
            Meeting previous = (hasPrevious) ? meetings.get(i-1) : null;
            Meeting meeting = meetings.get(i);

            // Getting rid of meetings that are not in the working hours
            if (getParsedTime(meeting.getEnd()).minuteDifferenceBetween(startTime) < 0) {
                meetings.remove(i--);
                continue;
            }

            if (!hasPrevious) {
                if (getParsedTime(meeting.getStart()).minuteDifferenceBetween(startTime) >= requestedFreeTime) {
                    // There is enough time before first meeting and starting hour
                    freeTimes.add(new FreeTime(startTime.toString(), getParsedTime(meeting.getStart()).toString()));
                }

                continue;
            }

            if (i+1 >= meetings.size()) {
                // This is the last meeting in calendar
                // Check if there is enough time after previous meeting
                if (getParsedTime(meeting.getStart()).minuteDifferenceBetween(getParsedTime(previous.getEnd())) >= requestedFreeTime) {
                    freeTimes.add(new FreeTime(getParsedTime(previous.getEnd()).toString(), getParsedTime(meeting.getStart()).toString()));
                }

                // Check if there is enough time before end of working hours and end of current meeting
                if (endTime.minuteDifferenceBetween(getParsedTime(meeting.getEnd())) >= requestedFreeTime) {
                    // There is enough time after last meeting and end of working time
                    freeTimes.add(new FreeTime(getParsedTime(meeting.getEnd()).toString(), endTime.toString()));
                }

                break;
            }

            if (getParsedTime(meeting.getStart()).minuteDifferenceBetween(getParsedTime(previous.getEnd())) >= requestedFreeTime) {
                // There is enough time between two meetings
                freeTimes.add(new FreeTime(getParsedTime(previous.getEnd()).toString(), getParsedTime(meeting.getStart()).toString()));
            }
        }

        return freeTimes;
    }

    private List<Meeting> removeMeetingsOffWorkingHours(Time workingFrom, Time workingTo, List<Meeting> meetings) {
        Iterator<Meeting> iterator = meetings.iterator();

        while (iterator.hasNext()) {

        }

        return null;
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
        if (calendars.length == 0) return new Calendar(new WorkingHours("00:00", "23:59"));

        Calendar joinCalendar = new Calendar(new WorkingHours("00:00", "23:59"));

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

            Time currMeetStart = getParsedTime(meetings.get(j).getStart());
            Time currMeetEnd = getParsedTime(meetings.get(j).getEnd());
            Time prevMeetStart = getParsedTime(meetings.get(j-1).getStart());
            Time prevMeetEnd = getParsedTime(meetings.get(j-1).getEnd());

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

    public static Time getParsedTime(@NotNull String time) {
        Matcher matcher = Calendar.hourPattern.matcher(time);
        if (matcher.matches()) {
            return new Time(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
        }

        throw new IllegalArgumentException();
    }

    public static class Time implements Comparable<Time> {
        private final int hour;
        private final int minutes;

        public Time(int hour, int minutes) {
            this.hour = hour;
            this.minutes = minutes;
        }

        public int getHour() {
            return hour;
        }

        public int getMinutes() {
            return minutes;
        }

        @Override
        public int compareTo(@NotNull CalendarUtils.Time o) {
            if (this == o) return 0;
            if (this.hour > o.hour) return 1;
            if (this.hour < o.hour) return -1;

            return Integer.compare(this.minutes, o.minutes);
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();

            if (this.hour < 10) stringBuilder.append(0);
            stringBuilder.append(hour);
            stringBuilder.append(":");
            if (this.minutes < 10) stringBuilder.append(0);
            stringBuilder.append(minutes);

            return stringBuilder.toString();
        }

        public int minuteDifferenceBetween(Time o) {
            return (this.minutes - o.getMinutes()) + (60 * (this.hour - o.getHour()));
        }
    }
}
