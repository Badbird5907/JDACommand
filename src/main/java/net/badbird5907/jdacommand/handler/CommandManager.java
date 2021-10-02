package net.badbird5907.jdacommand.handler;

import net.badbird5907.jdacommand.Command;
import net.badbird5907.jdacommand.events.CommandEvent;
import net.badbird5907.jdacommand.CommandResult;
import net.badbird5907.jdacommand.JDACommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CommandManager {
    public static void process(Method cmd, String[] args, MessageReceivedEvent e, Object o, Command command){
        try {
            CommandResult result = (CommandResult) cmd.invoke(o,args, new CommandEvent(args, e),e.getAuthor(),e.getMember(),e.getGuild(),e.getChannel());
            if (result == null)
                result = CommandResult.SUCCESS;
            switch (result){
                case SUCCESS:
                    handle(JDACommand.getInstance().getMessageHandler().successCommandResult(e.getAuthor(),command),e.getMessage());
                    break;
                case ERROR:
                    handle(JDACommand.getInstance().getMessageHandler().errorCommandResult(e.getAuthor(),command),e.getMessage());
                    break;
                case OTHER:
                    handle(JDACommand.getInstance().getMessageHandler().otherCommandResult(e.getAuthor(),command),e.getMessage());
                    break;
                case NO_PERMS:
                    handle(JDACommand.getInstance().getMessageHandler().noPermsCommandResult(e.getAuthor(),command),e.getMessage());
                    break;
            }
        } catch (IllegalAccessException | InvocationTargetException illegalAccessException) {
            illegalAccessException.printStackTrace();
        }
    }
    private static void handle(Object message, Message command){
        if (message == null)
            return;
        if (message instanceof MessageEmbed)
            command.reply((MessageEmbed) message).queue();
        else if (message instanceof Message)
            command.reply((Message) message).queue();
        else if (message instanceof String)
            command.reply((String) message).queue();
    }
}
