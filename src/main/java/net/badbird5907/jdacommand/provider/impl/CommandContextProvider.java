package net.badbird5907.jdacommand.provider.impl;

import net.badbird5907.jdacommand.context.CommandContext;
import net.badbird5907.jdacommand.context.ParameterContext;
import net.badbird5907.jdacommand.provider.Provider;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandContextProvider implements Provider<CommandContext> {

    @Override
    public CommandContext provide(CommandContext context, ParameterContext pContext) throws Exception {
        return context;
    }

    @Override
    public OptionData getOptionData(ParameterContext paramContext) {
        return null;
    }

    @Override
    public Class<?> getType() {
        return CommandContext.class;
    }
}
