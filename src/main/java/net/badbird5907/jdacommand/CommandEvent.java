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
    private String[] args = null;
    private MessageChannel channel;
    private Message message;
    private MessageReceivedEvent event;
    private User author;
    private Guild guild;

    public CommandEvent(final String[] args, final MessageChannel channel, final Message message, final MessageReceivedEvent event,final User author,final Guild guild){
        this.args = args;
        this.channel = channel;
        this.message = message;
        this.event = event;
        this.guild = guild;
        this.author = author;
    }
}
