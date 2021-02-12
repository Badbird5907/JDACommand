package net.badbird5907.jdacommand;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Getter
@Setter
public class CommandEvent {
	private String[] args;
	private MessageChannel channel;
	private Message message;
	private MessageReceivedEvent event;

	public CommandEvent(final String[] args, final MessageChannel channel, final Message message, final MessageReceivedEvent event) {
		this.args = args;
		this.channel = channel;
		this.message = message;
		this.event = event;
	}
}