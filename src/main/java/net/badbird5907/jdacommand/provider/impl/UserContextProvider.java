package net.badbird5907.jdacommand.provider.impl;

import net.badbird5907.jdacommand.context.CommandContext;
import net.badbird5907.jdacommand.context.ParameterContext;
import net.badbird5907.jdacommand.provider.Provider;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class UserContextProvider implements Provider<User> {
    @Override
    public User provide(CommandContext context, ParameterContext pContext) {
        if (pContext.isSender()) {
            return context.getMember().getUser();
        }
        if (pContext.isRequired())
            return context.getOption(pContext.getName()).getAsUser();
        else return context.hasOption(pContext.getName()) ? context.getOption(pContext.getName()).getAsUser() : null;
    }

    @Override
    public OptionData getOptionData(ParameterContext paramContext) {
        return paramContext.isSender() ? null : new OptionData(OptionType.MENTIONABLE, paramContext.getName(),"User",paramContext.isRequired());
    }

    @Override
    public Class<?> getType() {
        return User.class;
    }
}
