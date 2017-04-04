package name.larcher.fabrice;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Tests the method {@link EventUtil#jsonify(java.util.List) }.
 * @author fabrice
 */
public class JsonifyTest {

	@Test
	public void testEmpty() {
		List<Event> events = Collections.emptyList();
		String json = EventUtil.jsonify(events);
		assertEquals(3, json.length());
		assertTrue(json.startsWith("{"));
		assertTrue(json.endsWith("}"));
		assertEquals("{\n}", json);
	}

	@Test
	public void testSingleEvent() {
		List<Event> events = Collections.singletonList(new Event(false, false,
				LocalDateTime.of(2017, Month.MARCH, 15, 12, 30),
				LocalDateTime.of(2017, Month.MARCH, 15, 13, 30)
			));
		String json = EventUtil.jsonify(events);
		assertTrue(3 < json.length());
		assertTrue(json.startsWith("{"));
		assertTrue(json.endsWith("}"));
		assertEquals("{\n" + "\"2017-03-15\": [\"12:30\"]\n" + "}", json);
	}

	@Test
	public void testSameDateFollowingEvents() {
		List<Event> events = Arrays.asList(
			new Event(false, false,
				LocalDateTime.of(2017, Month.MARCH, 15, 12, 30),
				LocalDateTime.of(2017, Month.MARCH, 15, 13, 30)
			),
			new Event(false, false,
				LocalDateTime.of(2017, Month.MARCH, 15, 13, 30),
				LocalDateTime.of(2017, Month.MARCH, 15, 15, 0)
			));
		String json = EventUtil.jsonify(events);
		assertTrue(3 < json.length());
		assertTrue(json.startsWith("{"));
		assertTrue(json.endsWith("}"));
		assertEquals("{\n\"2017-03-15\": [\"12:30\", \"13:30\"]\n}", json);
	}

	@Test
	public void testSameDateOverlappingEvents() {
		List<Event> events = Arrays.asList(
			new Event(false, false,
				LocalDateTime.of(2017, Month.MARCH, 15, 12, 30),
				LocalDateTime.of(2017, Month.MARCH, 15, 13, 30)
			),
			new Event(false, false,
				LocalDateTime.of(2017, Month.MARCH, 15, 13, 0),
				LocalDateTime.of(2017, Month.MARCH, 15, 15, 0)
			));
		String json = EventUtil.jsonify(events);
		assertTrue(3 < json.length());
		assertTrue(json.startsWith("{"));
		assertTrue(json.endsWith("}"));
		assertEquals("{\n\"2017-03-15\": [\"12:30\", \"13:00\"]\n}", json);
	}

	@Test
	public void testMultiDateEvents() {
		List<Event> events = Arrays.asList(
			new Event(false, false,
				LocalDateTime.of(2017, Month.MARCH, 15, 12, 30),
				LocalDateTime.of(2017, Month.MARCH, 15, 13, 30)
			),
			new Event(false, false,
				LocalDateTime.of(2017, Month.MARCH, 16, 13, 0),
				LocalDateTime.of(2017, Month.MARCH, 16, 15, 0)
			));
		String json = EventUtil.jsonify(events);
		assertTrue(3 < json.length());
		assertTrue(json.startsWith("{"));
		assertTrue(json.endsWith("}"));
		assertEquals("{\n\"2017-03-15\": [\"12:30\"],\n\"2017-03-16\": [\"13:00\"]\n}", json);
	}

	@Test
	public void testMultiDateSameDateMixOfEvents() {
		List<Event> events = Arrays.asList(
			new Event(false, false,
				LocalDateTime.of(2017, Month.MARCH, 15, 12, 30),
				LocalDateTime.of(2017, Month.MARCH, 15, 13, 30)
			),
			new Event(false, false,
				LocalDateTime.of(2017, Month.MARCH, 16, 11, 0),
				LocalDateTime.of(2017, Month.MARCH, 16, 12, 0)
			),
			new Event(false, false,
				LocalDateTime.of(2017, Month.MARCH, 16, 13, 0),
				LocalDateTime.of(2017, Month.MARCH, 16, 15, 0)
			));
		String json = EventUtil.jsonify(events);
		assertTrue(3 < json.length());
		assertTrue(json.startsWith("{"));
		assertTrue(json.endsWith("}"));
		assertEquals("{\n\"2017-03-15\": [\"12:30\"],\n\"2017-03-16\": [\"11:00\", \"13:00\"]\n}", json);
	}

	// Robustness : tests that we get an exception in this case
	@Test(expected = UnsupportedOperationException.class)
	public void testMultiDateEvent() {
		List<Event> events = Collections.singletonList(
			new Event(false, false,
				LocalDateTime.of(2017, Month.MARCH, 15, 12, 30),
				LocalDateTime.of(2017, Month.MARCH, 16, 13, 30)
			));
		EventUtil.jsonify(events);
	}
}
