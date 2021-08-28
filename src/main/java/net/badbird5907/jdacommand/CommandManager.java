package net.badbird5907.jdacommand;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CommandManager {
    public static void process(Method cmd, String[] args, MessageReceivedEvent e, Object o){
        try {
            CommandResult result = (CommandResult) cmd.invoke(o,args, new CommandEvent(args, e),e.getAuthor(),e.getMember(),e.getGuild(),e.getChannel());
            if (JDACommand.getOverrideCommandResult().get(result) != null){
                Object obj = JDACommand.getOverrideCommandResult().get(result);
                if (obj instanceof String){
                    e.getChannel().sendMessage(String.valueOf(obj)).queue();
                }else{
                    e.getChannel().sendMessage((MessageEmbed) obj).queue();
                }
            }else if((result != CommandResult.SUCCESS) && (result != CommandResult.OTHER) && result != null){
                e.getChannel().sendMessage(result.getMessage()).queue();
            }
        } catch (IllegalAccessException | InvocationTargetException illegalAccessException) {
            illegalAccessException.printStackTrace();
        }
    }

}
