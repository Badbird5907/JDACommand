package net.badbird5907.jdacommand;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CommandManager {
    public static void process(Method cmd, String[] args, MessageReceivedEvent e){
        try {
            CommandResult result = (CommandResult) cmd.invoke(args, new CommandEvent(args, e),e.getAuthor(),e.getMember(),e.getGuild(),e.getChannel());
            if((result != CommandResult.SUCCESS) && (result != CommandResult.OTHER)){
                e.getChannel().sendMessage(result.getMessage());
            }
        } catch (IllegalAccessException illegalAccessException) {
            illegalAccessException.printStackTrace();
        } catch (InvocationTargetException invocationTargetException) {
            invocationTargetException.printStackTrace();
        }
    }

}
