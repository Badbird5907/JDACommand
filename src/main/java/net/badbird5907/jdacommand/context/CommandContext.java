package net.badbird5907.jdacommand.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class CommandContext {
    private final Member member;
    private final SlashCommandInteractionEvent event;
    private final MessageChannel channel;

    public OptionMapping getOption(String name) {
        return event.getOption(name);
    }
    public Optional<OptionMapping> get(String name) {
        Optional<OptionMapping> option = Optional.ofNullable(event.getOption(name));
        return option;
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
