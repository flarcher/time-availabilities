package name.larcher.fabrice;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Initial test provided with the problem description.
 * Tests the method {@link EventUtil#availabilities(java.util.List, java.time.LocalDateTime, java.time.LocalDateTime, int) }.
 * Please also see the file {@link AvailabilitiesTest} for other tests.
 * @see AvailabilitiesTest
 * 
 * @author fabrice
 */
public class Content {

	private static final int DURATION = 30; // In minutes

	private static void assert8thJuly(LocalDateTime ts) {
		assertEquals(Month.JULY, ts.getMonth());
		assertEquals(8, ts.getDayOfMonth());
	}

	private static void assertHoursMinutes(List<Event> events, int index, int hour, int minute) {
		Event event = events.get(index);
		LocalDateTime start = event.getStartDate();
		LocalDateTime end = event.getEndDate();
		assertEquals(DURATION, (end.toEpochSecond(ZoneOffset.UTC) - start.toEpochSecond(ZoneOffset.UTC)) / 60);
		assertEquals(hour, start.getHour());
		assertEquals(minute, start.getMinute());
	}

	@Test
	public void initialTest() throws ParseException {

		LocalDateTime start = LocalDateTime.of(2016, Month.JULY, 1, 10, 30); // July 1st, 10:30
		LocalDateTime end = LocalDateTime.of(2016, Month.JULY, 1, 14, 0); // July 1st, 14:00
		Event recurring = new Event(true, true, start, end); // weekly recurring opening in calendar

		start = LocalDateTime.of(2016, Month.JULY, 8, 11, 30); // July 8th, 11:30
		end = LocalDateTime.of(2016, Month.JULY, 8, 12, 30); // July 8th, 12:30
		Event busy = new Event(false, false, start, end); // intervention scheduled
		
		LocalDateTime since = LocalDateTime.of(2016, Month.JULY, 4, 10, 0); // July 4th 10:00
		LocalDateTime until = LocalDateTime.of(2016, Month.JULY, 10, 10, 0); // July 10th 10:00
		List<Event> answer = EventUtil.availabilities(
				Arrays.asList(recurring, busy),
				since, until, DURATION); // When are you available between these dates ?
		
		/*
		 * Answer should be : 
		 * I'm available from July 8th, at 10:30, 11:00, 12:30, 13:00, and 13:30
		 * I'm not available any other time !
		 */
		assertEquals(5, answer.size());
		for (Event event : answer) {
			assert8thJuly(event.getStartDate());
			assert8thJuly(event.getEndDate());
		}
		int i = 0;
		assertHoursMinutes(answer, i++, 10, 30);
		assertHoursMinutes(answer, i++, 11,  0);
		assertHoursMinutes(answer, i++, 12, 30);
		assertHoursMinutes(answer, i++, 13,  0);
		assertHoursMinutes(answer, i++, 13, 30);
	}

}
