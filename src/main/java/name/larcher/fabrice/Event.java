package name.larcher.fabrice;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an event.
 * Is immutable.
 * Was provided (~80%) by <i>Inch</i>
 * @author fabrice
 */
//@Immutable
public class Event {

	// Note1 : I prefer to use final attributes when possible
	/* (no setter is needed and makes the class immutable as long as the attributes are also immutables) */
	private final boolean opening;
	private final boolean recurring;

	// Note2 : `java.util.Date` is deprecated, I prefer to use java8 `LocalDateTime`.
	private final LocalDateTime startDate; // Never null
	private final LocalDateTime endDate; // Never null

	public Event(boolean opening, boolean recurring, LocalDateTime date, LocalDateTime endDate) {
		super();
		this.opening = opening;
		this.recurring = recurring;
		this.startDate = Objects.requireNonNull(date);
		this.endDate = Objects.requireNonNull(endDate);
	}

	public boolean isOpening(){
		return opening;
	}
	
	public boolean isBusy(){
		return !opening;
	}
	
	public boolean isRecurring(){
		return recurring;
	}

	// Note3 : I renamed that method for clarity `getDate` -> `getStartDate`
	public LocalDateTime getStartDate() {
		return startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public Duration getDuration() {
		return Duration.between(startDate, endDate);
	}

	// Note4 : Missing Object's methods overrides

	// Generated toString()
	@Override
	public String toString() {
		return "Event{" + "opening=" + opening + ", recurring=" + recurring + ", startDate=" + startDate + ", endDate=" + endDate + '}';
	}

	// Generated hashCode()
	@Override
	public int hashCode() {
		int hash = 5;
		hash = 61 * hash + (this.opening ? 1 : 0);
		hash = 61 * hash + (this.recurring ? 1 : 0);
		hash = 61 * hash + Objects.hashCode(this.startDate);
		hash = 61 * hash + Objects.hashCode(this.endDate);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Event other = (Event) obj;
		if (this.opening != other.opening) {
			return false;
		}
		if (this.recurring != other.recurring) {
			return false;
		}
		if (!this.recurring) {
			if (!Objects.equals(this.startDate, other.startDate)) {
				return false;
			}
			if (!Objects.equals(this.endDate, other.endDate)) {
				return false;
			}
		} else {
			// They might be equal even with different start/end dates
			// Test duration first
			if (!Objects.equals(this.getDuration(), other.getDuration())) {
				return false;
			}
			// If any non-week dependant variable is different, it is not equal
			if (
				!Objects.equals(this.startDate.getDayOfWeek(), other.startDate.getDayOfWeek()) ||
				!Objects.equals(this.startDate.getHour(), other.startDate.getHour()) ||
				!Objects.equals(this.startDate.getMinute(), other.startDate.getMinute()) ||
				!Objects.equals(this.endDate.getDayOfWeek(), other.endDate.getDayOfWeek())  ||
				!Objects.equals(this.endDate.getHour(), other.endDate.getHour()) ||
				!Objects.equals(this.endDate.getMinute(), other.endDate.getMinute())
					) {
				return false;
			}
		}
		return true;
	}

}
