package net.badbird5907.jdacommand;

import net.badbird5907.jdacommand.annotation.Command;
import net.badbird5907.jdacommand.context.CommandContext;
import net.badbird5907.jdacommand.context.ParameterContext;
import net.badbird5907.jdacommand.events.CommandPreProcessEvent;
import net.badbird5907.jdacommand.message.MessageHandler;
import net.badbird5907.jdacommand.provider.Provider;
import net.badbird5907.jdacommand.util.Cooldown;
import net.badbird5907.jdacommand.util.object.CommandWrapper;
import net.badbird5907.jdacommand.util.object.Pair;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.lang.reflect.Method;

public class CommandManager {
    public static void process(Method cmd, SlashCommandInteractionEvent e, Object o, Command command) {
        CommandContext context = new CommandContext(e.getMember(), e, e.getMessageChannel(), e.getGuild());
        if (command.cooldown() > 0) {
            if (Cooldown.isOnCooldown(command.name(), e.getUser().getId())) {
                double time = Cooldown.getCooldownTimeInSeconds(command.name(), e.getUser().getId());
                MessageHandler.replyTo(context, JDACommand.getInstance().getMessageHandler().onCooldown(e.getMember(), command, time));
                return;
            }
        }
        CommandPreProcessEvent event = new CommandPreProcessEvent(command.name(), e, context);
        JDACommand.getEventBus().post(event);
        if (event.isCancelled())
            return;
        try {
            CommandWrapper wrapper = JDACommand.getCommandMap().get(command.name().toLowerCase());
            Object[] params = new Object[wrapper.getParams().length];
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
            if (command.cooldown() > 0) {
                Cooldown.addUser(command.name(), e.getUser().getId(), command.cooldown());
            }
            Object r = cmd.invoke(o, params);
            if (r instanceof CommandResult) {
                CommandResult result = (CommandResult) r;
                MessageHandler.replyTo(
                        context,
                        JDACommand.getInstance().getMessageHandler()
                                .commandResult(e.getMember(), command, result)
                );
            } else if (r instanceof String) {
                e.reply(String.valueOf(r)).queue();
            } else if (r instanceof MessageEmbed) {
                e.replyEmbeds((MessageEmbed) r).queue();
            }
        } catch (Exception illegalAccessException) {
            illegalAccessException.printStackTrace();
        }
    }

}
