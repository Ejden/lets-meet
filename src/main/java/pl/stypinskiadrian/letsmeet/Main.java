package pl.stypinskiadrian.letsmeet;

public class Main {
    public static void main(String[] args) {
        Calendar cal1 = new Calendar(new WorkingHours(new Time("09:00"), new Time("19:55")));
        cal1.addMeeting(new Meeting(new Time("09:00"), new Time("10:30")));
        cal1.addMeeting(new Meeting(new Time("12:00"), new Time("13:00")));
        cal1.addMeeting(new Meeting(new Time("16:00"), new Time("18:00")));
        Calendar cal2 = new Calendar(new WorkingHours(new Time("10:00"), new Time("18:30")));
        cal2.addMeeting(new Meeting(new Time("10:00"), new Time("11:30")));
        cal2.addMeeting(new Meeting(new Time("12:30"), new Time("14:30")));
        cal2.addMeeting(new Meeting(new Time("14:30"), new Time("15:00")));
        cal2.addMeeting(new Meeting(new Time("16:00"), new Time("17:00")));

        System.out.println(CalendarUtils.findProposalMeetingTimes(30, cal1, cal2));
    }
}
