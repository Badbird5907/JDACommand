package net.badbird5907.jdacommand.provider.impl;

import net.badbird5907.jdacommand.context.CommandContext;
import net.badbird5907.jdacommand.context.ParameterContext;
import net.badbird5907.jdacommand.provider.Provider;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class IMentionableProvider implements Provider<IMentionable> {
    @Override
    public IMentionable provide(CommandContext context, ParameterContext pContext) throws Exception {
        if (context.getOption(pContext.getName()) == null) {
            return provideDefault(context, pContext);
        }
        return context.getOption(pContext.getName()).getAsMentionable();
    }

    @Override
    public OptionData getOptionData(ParameterContext paramContext) {
        return new OptionData(OptionType.MENTIONABLE, paramContext.getArgName(), "Mentionable", paramContext.isRequired());
    }

    @Override
    public Class<?> getType() {
        return IMentionable.class;
    }
}
