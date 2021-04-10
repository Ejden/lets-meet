package pl.stypinskiadrian.letsmeet;

public class Main {
    public static void main(String[] args) {
        Calendar cal1 = new Calendar(new WorkingHours("09:00", "19:55"));
        cal1.addMeeting(new Meeting("09:00", "10:30"));
        cal1.addMeeting(new Meeting("12:00", "13:00"));
        cal1.addMeeting(new Meeting("16:00", "18:00"));
        Calendar cal2 = new Calendar(new WorkingHours("10:00", "18:30"));
        cal2.addMeeting(new Meeting("10:00", "11:30"));
        cal2.addMeeting(new Meeting("12:30", "14:30"));
        cal2.addMeeting(new Meeting("14:30", "15:00"));
        cal2.addMeeting(new Meeting("16:00", "17:00"));

        System.out.println(CalendarUtils.findProposalMeetingTimes(30, cal1, cal2));
    }
}
