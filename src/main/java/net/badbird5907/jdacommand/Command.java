package net.badbird5907.jdacommand;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public abstract class Command {
	protected String name;
	protected String desc;
	protected String[] aliases;
	public Command(String name, String desc) {
		this.name = name;
		this.desc = desc;
		this.aliases = new String[]{};
	}
	public Command(String name, String desc, String[] aliases){
		this.name = name;
		this.desc = desc;
		this.aliases = aliases;
	}
    protected abstract CommandResult execute(String[] args, CommandEvent event, User author, Guild guild, MessageChannel channel);
}