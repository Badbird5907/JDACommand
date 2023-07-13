package dev.badbird.jdacommand.object;

import lombok.Data;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Optional;

@Data
public class ExecutionContext {
    private final Member member;
    private final SlashCommandInteractionEvent event;
    private final MessageChannel channel;
    private final Guild guild;

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
        event.getHook().sendMessage(message).queue();
    }

    public void reply(MessageEmbed embed, MessageEmbed... embeds) {
        event.getHook().sendMessageEmbeds(embed, embeds).queue();
    }

    public void setOriginal(String message) {
        event.getHook().editOriginal(message).queue();
    }

    public void setOriginal(MessageEmbed... embeds) {
        event.getHook().editOriginalEmbeds(embeds).queue();
    }
}
