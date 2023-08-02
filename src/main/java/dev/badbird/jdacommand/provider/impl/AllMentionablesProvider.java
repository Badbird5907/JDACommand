package dev.badbird.jdacommand.provider.impl;

import dev.badbird.jdacommand.JDACommand;
import dev.badbird.jdacommand.annotation.Author;
import dev.badbird.jdacommand.annotation.DefaultAuthor;
import dev.badbird.jdacommand.object.ExecutionContext;
import dev.badbird.jdacommand.object.ParameterContext;
import dev.badbird.jdacommand.object.ParameterInfo;
import dev.badbird.jdacommand.object.command.impl.CommandInfo;
import dev.badbird.jdacommand.provider.Provider;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AllMentionablesProvider {
    public static class UserProvider implements Provider<User> {

        @Override
        public User provide(ExecutionContext context, ParameterContext parameterContext, CommandInfo commandInfo, ParameterInfo parameterInfo) {
            Author author = parameterContext.getAnnotation(Author.class);
            if (parameterInfo.isArgument()) {
                OptionMapping option = context.getOption(parameterContext.getArgName());
                if (option != null) {
                    return option.getAsUser();
                } else {
                    if (author != null || parameterContext.hasAnnotation(DefaultAuthor.class)) {
                        return context.getEvent().getUser();
                    }
                }
            }
            return null;
        }

        @Override
        public OptionType getOptionType(ParameterInfo parameter) {
            Author author = parameter.getParameter().getAnnotation(Author.class);
            if (author == null && parameter.isArgument()) {
                return OptionType.USER;
            }
            return null;
        }
    }

    public static class MemberProvider implements Provider<Member> {

        @Override
        public Member provide(ExecutionContext context, ParameterContext parameterContext, CommandInfo commandInfo, ParameterInfo parameterInfo) {
            Author author = parameterContext.getAnnotation(Author.class);
            if (parameterInfo.isArgument()) {
                OptionMapping option = context.getOption(parameterContext.getArgName());
                if (option != null) {
                    return option.getAsMember();
                } else {
                    if (author != null || parameterContext.hasAnnotation(DefaultAuthor.class)) {
                        return context.getEvent().getMember();
                    }
                }
            }
            return null;
        }

        @Override
        public OptionType getOptionType(ParameterInfo parameter) {
            Author author = parameter.getParameter().getAnnotation(Author.class);
            if (author == null && parameter.isArgument()) {
                return OptionType.USER;
            }
            return null;
        }
    }

    public static class ChannelProvider implements Provider<GuildChannelUnion> {
        @Override
        public GuildChannelUnion provide(ExecutionContext context, ParameterContext parameterContext, CommandInfo commandInfo, ParameterInfo parameterInfo) {
            OptionMapping option = context.getOption(parameterContext.getArgName());
            if (option != null) {
                return option.getAsChannel();
            }
            return null;
        }

        @Override
        public OptionType getOptionType(ParameterInfo parameter) {
            return OptionType.CHANNEL;
        }

        @Override
        public boolean matchWithInstanceOf() {
            return true;
        }
    }

    public static class RoleProvider implements Provider<Role> {
        @Override
        public Role provide(ExecutionContext context, ParameterContext parameterContext, CommandInfo commandInfo, ParameterInfo parameterInfo) {
            OptionMapping option = context.getOption(parameterContext.getArgName());
            if (option != null) {
                return option.getAsRole();
            }
            return null;
        }

        @Override
        public OptionType getOptionType(ParameterInfo parameter) {
            return OptionType.ROLE;
        }
    }

    public static void registerAll(JDACommand jdaCommand) {
        jdaCommand.registerProvider(User.class, new UserProvider());
        jdaCommand.registerProvider(Member.class, new MemberProvider());
        // get the return types of GuildChannelUnion
                /*new Class[]{GuildChannel.class, GuildChannelUnion.class, TextChannel.class, VoiceChannel.class,
                ThreadChannel.class, ForumChannel.class, Category.class, NewsChannel.class, StageChannel.class, GuildMessageChannel.class, AudioChannel.class,
                IThreadContainer.class};
                */
        for (Method declaredMethod : GuildChannelUnion.class.getDeclaredMethods()) {
            // if the method or one of it's parents extends GuildChannel
            if (GuildChannel.class.isAssignableFrom(declaredMethod.getReturnType())) {
                jdaCommand.registerProvider(declaredMethod.getReturnType(), new ChannelProvider());
            }
        }
        jdaCommand.registerProvider(Role.class, new RoleProvider());
    }
}
