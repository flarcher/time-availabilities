package name.larcher.fabrice;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Utilities around the {@link Event} class.
 * 
 * @author fabrice
 */
abstract class EventUtil {

	/**
	 * @param input Liste des événements en entrée. Peut contenir des événements récurrents.
	 * @param since Début de la période à analyser.
	 * @param until Fin de la période à analyser.
	 * @param durationInMinutes Durée des créneaux en sortie.
	 * @return La liste des créneaux libres dans la période à analyser, ayant la durée indiquée en paramètre et triés
	 * dans l'ordre chronologique. Ces crénéaux ne sont jamais récurrents.
	 */
	static List<Event> availabilities(
		List<Event> input,
		LocalDateTime since,
		LocalDateTime until,
		int durationInMinutes) {

		// Checking arguments
		if (durationInMinutes < 1) {
			throw new IllegalArgumentException("Duration must be greater than 0");
		}
		Objects.requireNonNull(since);
		Objects.requireNonNull(until);
		if (input.isEmpty()) {
			return new ArrayList<>();
		}

		// Step 1 : Transforms recurrent events into regular events
		List<Event> result = new ArrayList<>(input.size());
		for (Event event : input) {
			if (event.isRecurring()) {
				LocalDateTime newStart = event.getStartDate();
				LocalDateTime newEnd = event.getEndDate();
				while (newStart.isBefore(until)) {
					Event newEvent = new Event(event.isOpening(), false, newStart, newEnd);
					if (filter(newEvent, since, until)) {
						result.add(newEvent);
					}
					newStart = newStart.plusWeeks(1);
					newEnd = newEnd.plusWeeks(1);
				}
			} else if (filter(event, since, until)) {
				result.add(event);
			}			
		}
		if (result.isEmpty()) {
			return result;
		}

		// Step 2 : Splits overlapping events and removes useless events
		Set<Long> openingSamples = new HashSet<>();
		Set<Long> busySamples = new HashSet<>();
		for (Event event : result) {
			long sampleEnd = toMinuteCount(event.getEndDate());
			long sampleStart = toMinuteCount(event.getStartDate());
			for (long ts = sampleStart; ts < sampleEnd; ts += durationInMinutes) {
				if ((sampleEnd - ts) < durationInMinutes) {
					continue;					
				}
				if (event.isBusy()) {
					busySamples.add(ts);
				} else {
					openingSamples.add(ts);
				}
			}
		}
		for (Long ts : busySamples) {
			openingSamples.remove(ts);
		}

		result.clear(); // Reset the content of the array
		long min = toMinuteCount(since), max = toMinuteCount(until);
		for (Long ts : openingSamples) {
			if (ts >= min && ts < max) {
				result.add(new Event(
					true, // Only opening events
					false, // Only non-recurrent events
					fromMinuteCount(ts),
					fromMinuteCount(Math.min(ts + durationInMinutes, max))));
			}
		}

		// Step 3 : Chronological ordering
		Collections.sort(result,
			(Event left, Event right) -> left.getStartDate().compareTo(right.getStartDate()));

		return result;
	}

	private static boolean filter(Event event, LocalDateTime since, LocalDateTime until) {
		return !event.getEndDate().isBefore(since) && !event.getStartDate().isAfter(until);
	}

	// The choice of the time offset is not important as long as the timestamps are used for comparisons between dates of the same timezones.
	// That variable is used for code consistency
	private static final ZoneOffset OFFSET = ZoneOffset.UTC;

	private static long toMinuteCount(LocalDateTime ldt) {
		return ldt.toInstant(OFFSET).getEpochSecond() / 60;
	}

	private static LocalDateTime fromMinuteCount(long minutes) {
		return LocalDateTime.ofEpochSecond(minutes * 60, 0, OFFSET);
	}

	/**
	 * @param events Event list that must :
	 * <ul>
	 * <li>be <b>chronologically ordered</b></li>
	 * <li>contains events of the <b>same duration</b></li>
	 * </ul>.
	 * @return A <i>JSON</i> representation of the {@link Event} list.
	 * @throws UnsupportedOperationException If an event lies from one day to another.
	 */
	static String jsonify(List<Event> events) {

		StringBuilder str = new StringBuilder(3 + (8 * events.size())) // Only a minimum size approximation
				.append('{').append('\n');
		String currentDate = null; // Current date considered during iteration
		for (Event event : events) {
			if (   event.getStartDate().getYear() != event.getEndDate().getYear()
			    || event.getStartDate().getDayOfYear() != event.getEndDate().getDayOfYear()) {
				// Clarifies limitations of the method
				throw new UnsupportedOperationException(
					"The method does not supports event across different days (including event " + event + ")");
			}
			// The start and the end are in the same day
			String date = JSON_DATE_FORMATTER.format(event.getStartDate());
			String time = JSON_TIME_FORMATTER.format(event.getStartDate());
			if (currentDate == null || !currentDate.equals(date)) {
				// the day has changed
				if (currentDate != null) { // There is a previous entry
					str.delete(str.length() - 1, str.length()); // Deletes the last comma
					str.append("],\n");
				}
				currentDate = date;
				str.append('\"').append(date).append("\": [");
			} else {
				str.append(' ');
			}
			str.append('\"').append(time).append("\",");
		}
		if (currentDate != null) { // Not empty
			str.delete(str.length() - 1, str.length());
			str.append("]\n");
		}
		return str.append('}').toString();
	}

	private static final DateTimeFormatter JSON_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter JSON_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

}
