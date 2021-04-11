package pl.stypinskiadrian.letsmeet;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TimeTest {

    @ParameterizedTest
    @MethodSource("provideTestCasesForShouldThrowIllegalArgumentException")
    void shouldThrowIllegalArgumentException(int hours, int minutes) {
        assertThrows(IllegalArgumentException.class, () -> new Time(hours, minutes));
    }

    @ParameterizedTest
    @MethodSource("provideStringTestCasesForShouldThrowIllegalArgumentException")
    void shouldThrowIllegalArgumentException(String hour) {
        assertThrows(IllegalArgumentException.class, () -> new Time(hour));
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForShouldParseTimeByString")
    void shouldParseTimeByString(String hour, int expectedHour, int expectedMinutes) {
        // When
        Time time = new Time(hour);

        // Then
        assertEquals(expectedHour, time.getHour());
        assertEquals(expectedMinutes, time.getMinutes());
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForShouldParseTimeByNumbers")
    void shouldParseTimeByNumbers(int hour, int minutes, int expectedHour, int expectedMinutes) {
        // When
        Time time = new Time(hour, minutes);

        // Then
        assertEquals(expectedHour, time.getHour());
        assertEquals(expectedMinutes, time.getMinutes());
    }

    private static Stream<Arguments> provideTestCasesForShouldThrowIllegalArgumentException() {
        return Stream.of(
                Arguments.of(-1, 0),
                Arguments.of(-10,0),
                Arguments.of(0, 500),
                Arguments.of(0, 60)
        );
    }

    private static Stream<Arguments> provideStringTestCasesForShouldThrowIllegalArgumentException() {
        return Stream.of(
                Arguments.of("-01:00"),
                Arguments.of("-10:0"),
                Arguments.of("00:500"),
                Arguments.of("0:60")
        );
    }

    private static Stream<Arguments> provideTestCasesForShouldParseTimeByString() {
        return Stream.of(
                Arguments.of("00:00", 0, 0),
                Arguments.of("00:01", 0, 1),
                Arguments.of("12:05", 12, 5),
                Arguments.of("23:59", 23, 59)
        );
    }

    private static Stream<Arguments> provideTestCasesForShouldParseTimeByNumbers() {
        return Stream.of(
                Arguments.of(0, 0, 0, 0),
                Arguments.of(0, 1, 0, 1),
                Arguments.of(12, 5, 12, 5),
                Arguments.of(23, 59, 23, 59)
        );
    }
}