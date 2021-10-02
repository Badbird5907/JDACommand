package net.badbird5907.jdacommand.events;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.*;
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
	private Member member;
	//args channel message event, author, guild
	public CommandEvent(final String[] args,final MessageReceivedEvent event) {
		this.args = args;
		this.channel = event.getChannel();
		this.message = event.getMessage();
		this.event = event;
		if (event.getChannelType().isGuild()) //fix NPE when dming bot
			this.guild = event.getGuild();
		this.author = event.getAuthor();
		this.member = event.getMember();
	}
	public void acknowledgeMessage(){
		getMessage().addReaction("\u2705").queue();
	}
}