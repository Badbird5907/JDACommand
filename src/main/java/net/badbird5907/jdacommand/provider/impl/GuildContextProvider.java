package net.badbird5907.jdacommand.provider.impl;

import net.badbird5907.jdacommand.context.CommandContext;
import net.badbird5907.jdacommand.context.ParameterContext;
import net.badbird5907.jdacommand.provider.Provider;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class GuildContextProvider implements Provider<Guild> {
    @Override
    public Guild provide(CommandContext context, ParameterContext ctx) {
        return context.getMember() != null ? context.getMember().getGuild() : null;
    }

    @Override
    public OptionData getOptionData(ParameterContext ctx) {
        return null;
    }

    @Override
    public Class<?> getType() {
        return Guild.class;
    }
}
