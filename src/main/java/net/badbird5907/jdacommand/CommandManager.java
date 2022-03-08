package net.badbird5907.jdacommand;

import net.badbird5907.jdacommand.annotation.Command;
import net.badbird5907.jdacommand.context.CommandContext;
import net.badbird5907.jdacommand.context.ParameterContext;
import net.badbird5907.jdacommand.events.CommandPreProcessEvent;
import net.badbird5907.jdacommand.provider.Provider;
import net.badbird5907.jdacommand.util.object.CommandWrapper;
import net.badbird5907.jdacommand.util.object.Pair;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.lang.reflect.Method;

public class CommandManager {
    public static void process(Method cmd, SlashCommandInteractionEvent e, Object o, Command command) {
        CommandPreProcessEvent event = new CommandPreProcessEvent(command.name(), e);
        JDACommand.getEventBus().post(event);
        if (event.isCancelled())
            return;
        try {
            CommandWrapper wrapper = JDACommand.getCommandMap().get(command.name().toLowerCase());
            Object[] params = new Object[wrapper.getParams().length];
            CommandContext context = new CommandContext(e.getMember(), e, e.getMessageChannel());
            for (Pair<ParameterContext, Provider<?>> parameter : wrapper.getParameters()) {
                try {
                    params[parameter.getValue0().getParameterIndex()] = parameter.getValue1().provide(context, parameter.getValue0());
                } catch (Exception ex) {
                    if (parameter.getValue1().failOnException()) {
                        System.err.println("Failed to provide parameter " + parameter.getValue0().getParameterIndex() + " for command " + command.name());
                        throw ex;
                    } else {
                        params[parameter.getValue0().getParameterIndex()] = parameter.getValue1().provideDefault(context, parameter.getValue0());
                    }
                }
            }
            Object r = cmd.invoke(o, params);
            if (r instanceof CommandResult) {
                CommandResult result = (CommandResult) r;
                if (JDACommand.getOverrideCommandResult().get(result) != null) {
                    Object obj = JDACommand.getOverrideCommandResult().get(result);
                    if (obj instanceof String) {
                        e.reply(String.valueOf(obj)).queue();
                    } else {
                        e.replyEmbeds((MessageEmbed) obj).queue();
                    }
                } else if ((result != CommandResult.SUCCESS) && (result != CommandResult.OTHER) && result != null) {
                    e.reply(result.getMessage()).queue();
                }
            }else if (r instanceof String) {
                e.reply(String.valueOf(r)).queue();
            }else if (r instanceof MessageEmbed) {
                e.replyEmbeds((MessageEmbed) r).queue();
            }
        } catch (Exception illegalAccessException) {
            illegalAccessException.printStackTrace();
        }
    }

}
