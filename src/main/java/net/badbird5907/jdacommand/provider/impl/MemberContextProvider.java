package net.badbird5907.jdacommand.provider.impl;

import net.badbird5907.jdacommand.context.CommandContext;
import net.badbird5907.jdacommand.context.ParameterContext;
import net.badbird5907.jdacommand.provider.Provider;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class MemberContextProvider implements Provider<Member> {
    @Override
    public Member provide(CommandContext context, ParameterContext pContext) {
        if (pContext.isSender()) {
            return context.getMember();
        }
        if (pContext.isRequired())
            return context.getOption(pContext.getName()).getAsMember();
        else return context.hasOption(pContext.getName()) ? context.getOption(pContext.getName()).getAsMember() : null;
    }

    @Override
    public OptionData getOptionData(ParameterContext paramContext) {
        return paramContext.isSender() ? null : new OptionData(OptionType.MENTIONABLE, paramContext.getName(), "Member", paramContext.isRequired());
    }

    @Override
    public Class<?> getType() {
        return Member.class;
    }
}
