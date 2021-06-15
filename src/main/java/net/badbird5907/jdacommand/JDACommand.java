package net.badbird5907.jdacommand;

import net.dv8tion.jda.api.JDA;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class JDACommand {
	public static List<Command> commands = new ArrayList<>();
	private static JDACommand instance;
	public String prefix;
	public JDA jda;

	public JDACommand(String prefix, JDA jda) {
		this.prefix = prefix;
		this.jda = jda;
	}

	public static JDACommand getInstance() {
		return instance;
	}

	public void init() {
		instance = this;
		jda.addEventListener(new MessageListener());
	}

	public void debugPrintCommands() {
		commands.forEach(cmd -> out.println(cmd.name));
	}

	public void registerCommand(Command command) {
		commands.add(command);
	}
}