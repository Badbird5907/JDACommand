package dev.badbird.jdacommand.object;

import dev.badbird.jdacommand.object.command.ExecutableCommand;
import dev.badbird.jdacommand.object.command.impl.CommandInfo;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.Optional;

@Getter
@Setter
public class ExecutionContext {
    private final Member member;
    private final SlashCommandInteractionEvent event;
    private final MessageChannel channel;
    private final Guild guild;
    private final ExecutableCommand executable;
    public ExecutionContext(SlashCommandInteractionEvent event, ExecutableCommand executable) {
        this.event = event;
        this.member = event.getMember();
        this.channel = event.getChannel();
        this.guild = event.getGuild();
        this.executable = executable;
    }

    public CommandInfo getCommandInfo() {
        if (executable instanceof CommandInfo) {
            return (CommandInfo) executable;
        } else throw new IllegalStateException("executable is not an instance of CommandInfo!");
    }

    public String getCommandName() {
        return getCommandInfo().getName();
    }

    public OptionMapping getOption(String name) {
        return event.getOption(name);
    }

    public Optional<OptionMapping> get(String name) {
        return Optional.ofNullable(event.getOption(name));
    }

    public boolean hasOption(String name) {
        return event.getOption(name) != null;
    }

    public void reply(String message) {
        if (event.isAcknowledged())
            event.getHook().sendMessage(message).queue();
        else event.reply(message).queue();
    }

    public void reply(MessageEmbed embed, MessageEmbed... embeds) {
        if (event.isAcknowledged())
            event.getHook().sendMessageEmbeds(embed, embeds).queue();
        else event.replyEmbeds(embed, embeds).queue();
    }

    public void setOriginal(String message) {
        event.getHook().editOriginal(message).queue();
    }

    public void setOriginal(MessageEmbed... embeds) {
        event.getHook().editOriginalEmbeds(embeds).queue();
    }

    public void deferReply() {
        event.deferReply().queue();
    }

    public static class Provider implements dev.badbird.jdacommand.provider.Provider<ExecutionContext> {

        @Override
        public ExecutionContext provide(ExecutionContext context, ParameterContext parameterContext, CommandInfo commandInfo, ParameterInfo parameterInfo) {
            return context;
        }

        @Override
        public OptionType getOptionType(ParameterInfo parameter) {
            return null;
        }
    }
}
