package name.larcher.fabrice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class for the Inch's test.
 * @author fabrice
 */
public final class InchTest {

	public static void main(String... args) {
		System.out.println("Hello !");
		Command.HELP.action(null); // Prints the help message

		List<Event> events = new ArrayList<>();
		// Java7 syntax for closing resource (no need of a finally block)
		try (BufferedReader console = new BufferedReader(
				new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
			
			String line = console.readLine();
			while (line != null) {
				// Using the 'command' design pattern with an enum
				for (Command cmd : Command.values()) {
					if (line.startsWith(cmd.name)) {
						String[] cmdArgs = line.length() > cmd.name.length() + 1 ?
							line.substring(cmd.name.length() + 1).split("\\s") : null;
						cmd.action(events, cmdArgs);
					}
				}
				line = console.readLine();
			}

		} catch (IOException e) {
			System.err.println("Oops " + e.getMessage());
			throw new IllegalStateException(e);
		} // Auto-closed
	}

}


