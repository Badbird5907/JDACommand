package net.badbird5907.jdacommand;

public abstract class Command {
	protected final String name;
	protected final String desc;

	public Command(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}

	protected abstract void execute(String[] args, CommandEvent event);
}