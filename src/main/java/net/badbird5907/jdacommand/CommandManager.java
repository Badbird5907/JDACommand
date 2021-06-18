package net.badbird5907.jdacommand;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CommandManager {
    public static void process(Method cmd, String[] args, MessageReceivedEvent e, Object o){
        try {
            CommandResult result = (CommandResult) cmd.invoke(o,args, new CommandEvent(args, e),e.getAuthor(),e.getMember(),e.getGuild(),e.getChannel());
            if((result != CommandResult.SUCCESS) && (result != CommandResult.OTHER) && result != null){
                e.getChannel().sendMessage(result.getMessage());
            }
        } catch (IllegalAccessException | InvocationTargetException illegalAccessException) {
            illegalAccessException.printStackTrace();
        }
    }

}
