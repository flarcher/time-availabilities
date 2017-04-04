package name.larcher.fabrice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalQueries;
import java.util.List;

/**
 * Each instance represents an action.
 * Follow the <i>command</i> design pattern.
 * 
 * @author fabrice
 */
enum Command {

	/**
	 * Prints the help message.
	 */
	HELP("help") {

		@Override
		String usage() {
			return "Provides the current information about usage of this program.";
		}

		@Override
		void action(List<Event> events, String... args) {

			for (Command cmd : Command.values()) {
				System.out.println("`" + cmd.name + "`:\n\t" + cmd.usage() + "\n");
			}
		}
	},

	/**
	 * Quits the application.
	 */
	QUIT("quit") {

		@Override
		String usage() {
			return "Quits the program.";
		}

		@Override
		void action(List<Event> events, String... args) {
			System.out.println("Good bye");
			System.exit(0);			
		}

	},

	/**
	 * Adds an opening event.
	 */
	ADD_OPENING("open") {

		@Override
		String usage() {
			return "Requires 3 arguments `{start} {end} {weekly}`. Adds a time slot (or many when it is periodic) of availability.\n\tNeeds the start instant of the time slot in ISO format `yyyy-MM-ddTHH:mm`.\n\tAlso needs the instant of the end of the time slot in the same format.\n\tIf the third argument is provided with a value of `true`, then the time slot gets created on a regular basis for each week (weekly).";
		}

		@Override
		void action(List<Event> events, String... args) {
			events.add(createEvent(true, args));
		}
	},

	/**
	 * Adds a busy event.
	 */
	ADD_BUSY("busy") {

		@Override
		String usage() {
			return "Requires 3 arguments `{start} {end} {weekly}`. Adds a time slot (or many when it is periodic) of unavailability.\n\tNeeds the start instant of the time slot in ISO format `yyyy-MM-ddTHH:mm`.\n\tAlso needs the instant of the end of the time slot in the same format.\n\tIf the third argument is provided with a value of `true`, then the time slot gets created on a regular basis for each week (weekly).";
		}

		@Override
		void action(List<Event> events, String... args) {
			events.add(createEvent(false, args));
		}
	},

	/**
	 * Prints available time slots in a JSON format.
	 */
	DISPLAY("out") {

		@Override
		String usage() {
			return "Requires 3 arguments `{start} {end} {duration}`. Prints time slots of availability in a JSON format where time slots are grouped by date and chronologically ordered. \n\tNeeds the start instant of the time slot in ISO format `yyyy-MM-ddTHH:mm`.\n\tAlso needs the instant of the end of the time slot in the same format.\n\tThe third argument is the duration of time slots in minutes. The last argument is optional and gets a value of 30 minutes by default.";
		}

		@Override
		void action(List<Event> events, String... args) {
			if (args.length < 2) {
				throw new IllegalArgumentException("Not enough argument");
			}
			int duration = args.length >= 3 ? Integer.valueOf(args[2]) : 30;
			LocalDateTime since = parseDate(args[0]);
			LocalDateTime until = parseDate(args[1]);
			
			List<Event> availabilities = EventUtil.availabilities(events, since, until, duration);
			System.out.println(EventUtil.jsonify(availabilities));
		}
	}

	;

	Command(String name) {
		this.name = name;
	}

	/** The command name as it should be in the command prompt. */
	final String name;

	/** The action related to the command. */
	abstract void action(List<Event> events, String... args);

	/** Provides documentation about the usage. */
	abstract String usage();

	private static Event createEvent(boolean opening, String... args) {
		if (args.length < 2) {
			throw new IllegalArgumentException("Not enough argument");
		}
		boolean weekly = args.length >= 3 ? Boolean.valueOf(args[2]) : false;
		return new Event(opening, weekly, parseDate(args[0]), parseDate(args[1]));
	}

	// For information, the DateTimeFormatter is thread safe (althrough if it is not needed there ...)
	private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

	private static LocalDateTime parseDate(String arg) {
		LocalDate date = formatter.parse(arg, TemporalQueries.localDate());
		LocalTime time = formatter.parse(arg, TemporalQueries.localTime());
		return LocalDateTime.of(date, time);
	}
}
