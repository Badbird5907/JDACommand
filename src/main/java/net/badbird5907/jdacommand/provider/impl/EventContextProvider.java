package net.badbird5907.jdacommand.provider.impl;

import net.badbird5907.jdacommand.context.CommandContext;
import net.badbird5907.jdacommand.context.ParameterContext;
import net.badbird5907.jdacommand.provider.Provider;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class EventContextProvider implements Provider<SlashCommandInteractionEvent> {
    @Override
    public SlashCommandInteractionEvent provide(CommandContext context, ParameterContext pContext) throws Exception {
        return context.getEvent();
    }

    @Override
    public OptionData getOptionData(ParameterContext paramContext) {
        return null;
    }

    @Override
    public Class<?> getType() {
        return SlashCommandInteractionEvent.class;
    }
}
