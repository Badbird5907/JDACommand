package net.badbird5907.jdacommand;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandManager {
    public static void process(Command cmd, String[] args, MessageReceivedEvent e){
        CommandResult result = cmd.execute(args, new CommandEvent(args, e),e.getAuthor(),e.getGuild(),e.getChannel());
        if((result != CommandResult.SUCCESS) && (result != CommandResult.OTHER)){
            e.getChannel().sendMessage(result.getMessage());
        }
    }

}
