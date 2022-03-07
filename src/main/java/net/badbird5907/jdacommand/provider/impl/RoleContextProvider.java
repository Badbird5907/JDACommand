package net.badbird5907.jdacommand.provider.impl;

import net.badbird5907.jdacommand.context.CommandContext;
import net.badbird5907.jdacommand.context.ParameterContext;
import net.badbird5907.jdacommand.provider.Provider;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class RoleContextProvider implements Provider<Role> {
    @Override
    public Role provide(CommandContext context, ParameterContext pContext) {
        if (pContext.isRequired()) return context.getOption(pContext.getName()).getAsRole();
        else return context.hasOption(pContext.getName()) ? context.getOption(pContext.getName()).getAsRole() : null;
    }

    @Override
    public OptionData getOptionData(ParameterContext paramContext) {
        return new OptionData(OptionType.ROLE, paramContext.getName(), "Role", paramContext.isRequired());
    }

    @Override
    public Class<?> getType() {
        return Role.class;
    }
}
