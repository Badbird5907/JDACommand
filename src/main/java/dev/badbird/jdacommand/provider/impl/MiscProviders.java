package dev.badbird.jdacommand.provider.impl;

import dev.badbird.jdacommand.JDACommand;
import dev.badbird.jdacommand.object.ExecutionContext;
import dev.badbird.jdacommand.object.ParameterContext;
import dev.badbird.jdacommand.object.ParameterInfo;
import dev.badbird.jdacommand.object.command.impl.CommandInfo;
import dev.badbird.jdacommand.provider.Provider;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class MiscProviders {
    public static class AttachmentProvider implements Provider<Message.Attachment> {

        @Override
        public Message.Attachment provide(ExecutionContext context, ParameterContext parameterContext, CommandInfo commandInfo, ParameterInfo parameterInfo) {
            OptionMapping option = context.getOption(parameterContext.getArgName());
            if (option != null) {
                return option.getAsAttachment();
            }
            return null;
        }

        @Override
        public OptionType getOptionType(ParameterInfo parameter) {
            return OptionType.ATTACHMENT;
        }
    }
    public static void registerAll(JDACommand jdaCommand) {
        jdaCommand.registerProvider(Message.Attachment.class, new AttachmentProvider());
    }
}
