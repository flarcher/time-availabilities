package name.larcher.fabrice;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Tests the method {@link EventUtil#availabilities(java.util.List, java.time.LocalDateTime, java.time.LocalDateTime, int) }.
 * Please also see the interesting test case of the file {@link Content}.
 * @see Content
 * @author fabrice
 */
public class AvailabilitiesTest {

	@Test
	public void testEmptyness() {
		List<Event> events = EventUtil.availabilities(Collections.emptyList(),
				LocalDateTime.MIN, LocalDateTime.MAX, 30);
		assertTrue(events.isEmpty());
	}

	@Test
	public void testOrdering() {
		LocalDateTime before = LocalDateTime.of(2017, Month.JULY, 1, 10, 30);
		LocalDateTime after = LocalDateTime.of(2017, Month.JULY, 1, 15, 30);
		assertTrue(before.plusHours(2).isBefore(after));

		List<Event> events = EventUtil.availabilities(
				Arrays.asList(
						new Event(true, false, after.minusHours(1), after),
						new Event(true, false, before, before.plusHours(1))
				),
				before.minusHours(1), after.plusHours(1), 60);

		assertFalse(events.isEmpty());
		assertEquals(2, events.size());
		assertEquals(before, events.get(0).getStartDate());
		assertEquals(after, events.get(1).getEndDate());
	}

	@Test
	public void testRecurringEvent() {
		int hour = 10;
		int minute = 30;
		int plusHour = 1;
		int plusMinute = 15;
		int duration = 30;
		assertTrue(plusHour * 60 + plusMinute > 2 * duration);
		assertTrue(plusHour * 60 + plusMinute < 3 * duration);
		LocalDateTime ldt = LocalDateTime.of(2017, Month.JULY, 1, hour, minute);
		List<Event> events = EventUtil.availabilities(
				Arrays.asList(new Event(
						true, // Opening
						true, // Recurring
						ldt,  // Start
						ldt.plusHours(plusHour).plusMinutes(plusMinute))), // End
				ldt, // Since
				ldt.plusMonths(1), // Until (includes several weeks)
				duration);
		assertEquals(10, events.size()); // Two times the week count (two instances per week)
		Event event;
		for (int i=0; i < events.size(); i++) {
			event = events.get(i);
			assertEquals(ldt.getDayOfWeek(), event.getStartDate().getDayOfWeek());
			if (i % 2 == 0) {
				assertEquals(hour, event.getStartDate().getHour());
				assertEquals(hour + ((minute + duration) / 60), event.getEndDate().getHour());
				assertEquals(minute, event.getStartDate().getMinute());
				assertEquals((minute + duration) % 60, event.getEndDate().getMinute());
			} else {
				assertEquals(hour + plusHour, event.getStartDate().getHour());
				assertEquals(hour + plusHour, event.getEndDate().getHour());
				assertEquals((minute + duration) % 60, event.getStartDate().getMinute());
				assertEquals((minute + (2*duration)) % 60, event.getEndDate().getMinute());
			}
		}
	}

}
