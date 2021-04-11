package pl.stypinskiadrian.letsmeet;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CalendarUtilsTest {

    @ParameterizedTest
    @MethodSource("provideTestCasesForShouldReturnPossibleHoursOfMeeting")
    void shouldReturnPossibleHoursOfMeeting(List<Calendar> calendars, int meetingDuration, List<FreeTime> expected) {
        // Given
        Calendar[] cals = new Calendar[calendars.size()];
        cals = calendars.toArray(cals);

        // When
        List<FreeTime> result = CalendarUtils.findProposalMeetingTimes(meetingDuration, cals);

        // Then
        assertEquals(expected.size(), result.size());

        for (int i = 0; i < expected.size(); i++) {
            FreeTime expectedFreeTime = expected.get(i);
            FreeTime actualFreeTime = result.get(i);

            assertEquals(expectedFreeTime.getStartTime().getHour(), actualFreeTime.getStartTime().getHour());
            assertEquals(expectedFreeTime.getStartTime().getMinutes(), actualFreeTime.getStartTime().getMinutes());
            assertEquals(expectedFreeTime.getEndTime().getMinutes(), actualFreeTime.getEndTime().getMinutes());
            assertEquals(expectedFreeTime.getEndTime().getMinutes(), actualFreeTime.getEndTime().getMinutes());
        }
    }

    private static Stream<Arguments> provideTestCasesForShouldReturnPossibleHoursOfMeeting() {
        return Stream.of(
            Arguments.of(
                    List.of(
                        generateCalendar("09:00", "19:55", generateMeetings(
                                TimeArgument.of("09:00", "10:30"),
                                TimeArgument.of("12:00", "13:00"),
                                TimeArgument.of("16:00", "18:00")
                                )
                        ),
                        generateCalendar("10:00", "18:30", generateMeetings(
                                TimeArgument.of("10:00", "11:30"),
                                TimeArgument.of("12:30", "14:30"),
                                TimeArgument.of("14:30", "15:00"),
                                TimeArgument.of("16:00", "17:00")
                                )
                        )
                    ),
                    30,
                    List.of(
                            new FreeTime(new Time("11:30"), new Time("12:00")),
                            new FreeTime(new Time("15:00"), new Time("16:00")),
                            new FreeTime(new Time("18:00"), new Time("18:30"))
                    )
            ),
            Arguments.of(
                    List.of(),
                    30,
                    List.of(new FreeTime(new Time("00:00"), new Time("23:59")))
            ),
            Arguments.of(
                    List.of(
                            generateCalendar("09:00", "19:55", List.of()),
                            generateCalendar("10:00", "18:30", List.of())
                    ),
                    30,
                    List.of(new FreeTime(new Time("10:00"), new Time("18:30")))
            ),
            Arguments.of(
                    List.of(
                            generateCalendar("10:00", "17:30", generateMeetings(
                                    TimeArgument.of("10:00", "10:30"),
                                    TimeArgument.of("11:00", "12:00"),
                                    TimeArgument.of("12:30", "13:00"),
                                    TimeArgument.of("13:30", "15:30"),
                                    TimeArgument.of("16:00", "17:00")
                            )),
                            generateCalendar("10:00", "14:30", generateMeetings(
                                    TimeArgument.of("10:00", "11:00"),
                                    TimeArgument.of("11:30", "12:30"),
                                    TimeArgument.of("13:30", "14:00")
                            ))
                    ),
                    30,
                    List.of(new FreeTime(new Time("13:00"), new Time("13:30")))
            ),
            Arguments.of(
                    List.of(
                            generateCalendar("10:00", "17:30", generateMeetings(
                                    TimeArgument.of("10:00", "10:30"),
                                    TimeArgument.of("11:00", "12:00"),
                                    TimeArgument.of("12:30", "13:00"),
                                    TimeArgument.of("13:30", "15:30"),
                                    TimeArgument.of("16:00", "17:00")
                            )),
                            generateCalendar("10:00", "14:30", generateMeetings(
                                    TimeArgument.of("10:00", "11:00"),
                                    TimeArgument.of("11:30", "12:30"),
                                    TimeArgument.of("13:30", "14:00")
                            ))
                    ),
                    60,
                    List.of()
            ),
            Arguments.of(
                    List.of(),
                    60,
                    List.of(new FreeTime(new Time("00:00"), new Time("23:59")))
            ),
            Arguments.of(
                    List.of(generateCalendar("10:00", "17:30", generateMeetings())),
                    60,
                    List.of(new FreeTime(new Time("10:00"), new Time("17:30")))
            ),
            Arguments.of(
                    List.of(generateCalendar("10:00", "17:30", generateMeetings(
                            TimeArgument.of("09:00", "12:00")
                            ))
                    ),
                    120,
                    List.of(new FreeTime(new Time("12:00"), new Time("17:30")))
            ),
            Arguments.of(
                    List.of(generateCalendar("10:00", "18:00", generateMeetings(
                            TimeArgument.of("11:00", "13:00")
                            ))
                    ),
                    60,
                    List.of(
                            new FreeTime(new Time("10:00"), new Time("11:00")),
                            new FreeTime(new Time("13:00"), new Time("18:00"))
                    )
            ),
            Arguments.of(
                    List.of(generateCalendar("10:00", "18:00", generateMeetings(
                            TimeArgument.of("11:00", "13:00")
                            ))
                    ),
                    120,
                    List.of(
                            new FreeTime(new Time("13:00"), new Time("18:00"))
                    )
            ),
            Arguments.of(
                    List.of(
                            generateCalendar("10:00", "18:00", generateMeetings(
                                    TimeArgument.of("10:00", "11:00"),
                                    TimeArgument.of("11:30", "11:55")

                            )),
                            generateCalendar("11:00", "17:00", generateMeetings(
                                    TimeArgument.of("11:00", "12:00"),
                                    TimeArgument.of("13:30", "14:00")
                            )),
                            generateCalendar("12:00", "16:00", generateMeetings(
                                    TimeArgument.of("13:00", "15:00")
                            ))
                    ),
                    10,
                    List.of(
                            new FreeTime(new Time("12:00"), new Time("13:00")),
                            new FreeTime(new Time("15:00"), new Time("16:00"))
                    )
            )
        );
    }

    private static Calendar generateCalendar(String workingFrom, String workingTo, List<Meeting> meetings) {
        Calendar calendar = new Calendar(new WorkingHours(new Time(workingFrom), new Time(workingTo)));

        meetings.forEach(calendar::addMeeting);

        return calendar;
    }

    private static Meeting generateMeeting(TimeArgument timeArgument) {
        return new Meeting(new Time(timeArgument.getStartsAt()), new Time(timeArgument.getEndsAt()));
    }

    private static List<Meeting> generateMeetings(TimeArgument... timeArguments) {
        return Arrays.stream(timeArguments)
                .map(arg -> new Meeting(new Time(arg.getStartsAt()), new Time(arg.getEndsAt())))
                .collect(Collectors.toList());

    }

    private static class TimeArgument {
        private final String startsAt;
        private final String endsAt;

        private TimeArgument(String startsAt, String endsAt) {
            this.startsAt = startsAt;
            this.endsAt = endsAt;
        }

        public static TimeArgument of(String startsAt, String endsAt) {
            return new TimeArgument(startsAt, endsAt);
        }

        public String getStartsAt() {
            return startsAt;
        }

        public String getEndsAt() {
            return endsAt;
        }
    }
}