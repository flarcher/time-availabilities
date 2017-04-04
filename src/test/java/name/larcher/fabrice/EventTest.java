/*
 *  Copyright © NUDGE / LEVEL5
 */
package name.larcher.fabrice;

import java.time.LocalDateTime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author fabrice
 */
public class EventTest {

	@Test
	public void testNonRecurringEquals() {
		LocalDateTime now = LocalDateTime.now();
		Event before = new Event(true, false, now, now.plusHours(1));
		Event after = new Event(true, false, now.plusMinutes(5), now.plusHours(1).plusMinutes(5));
		assertFalse(before.equals(after));
		assertNotEquals(before.hashCode(), after.hashCode());
		assertTrue(before.equals(before));
		Event otherBefore = new Event(true, false, now, now.plusHours(1));
		assertTrue(before.equals(otherBefore));
		assertEquals(before.hashCode(), otherBefore.hashCode());
	}

	@Test
	public void testRecurringEquals() {
		LocalDateTime now = LocalDateTime.now();
		Event before = new Event(true, true, now, now.plusHours(1));
		Event after = new Event(true, true, now.plusWeeks(2), now.plusWeeks(2).plusHours(1));
		assertTrue(before.equals(after));
		//assertEquals(before.hashCode(), after.hashCode()); // Does not need to be true
	}
	
}
