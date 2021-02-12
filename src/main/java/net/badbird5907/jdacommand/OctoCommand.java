package net.badbird5907.jdacommand;

import net.dv8tion.jda.api.JDA;

import java.util.ArrayList;

public class OctoCommand {
	public static final ArrayList<Command> commands = new ArrayList<>();
	private static OctoCommand instance;
	public final String prefix;
	public final JDA jda;

	public OctoCommand(String prefix, JDA jda) {
		this.prefix = prefix;
		this.jda = jda;
	}

	public static OctoCommand getInstance() {
		return instance;
	}

	public void init() {
		instance = this;
	}
}
