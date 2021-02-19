package net.badbird5907.jdacommand;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Getter
@Setter
public class CommandEvent {
	private String[] args;
	private MessageChannel channel;
	private Message message;
	private MessageReceivedEvent event;
	private User author;
	private Guild guild;
	//args channel message event, author, guild
	public CommandEvent(final String[] args,final MessageReceivedEvent event) {
		this.args = args;
		this.channel = event.getChannel();
		this.message = event.getMessage();
		this.event = event;
		this.guild = event.getGuild();
		this.author = event.getAuthor();
	}
}