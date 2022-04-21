package net.badbird5907.jdacommand.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.badbird5907.jdacommand.util.object.Replyable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class CommandContext implements Replyable {
    private final Member member;
    private final SlashCommandInteractionEvent event;
    private final MessageChannel channel;
    private final Guild guild;

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

    @Override
    public void reply(String message) {
        if (event.isAcknowledged())
            event.getHook().sendMessage(message).queue();
        else event.reply(message).queue();
    }

    @Override
    public void replyEphemeral(String message) {
        if (event.isAcknowledged())
            event.getHook().sendMessage(message).setEphemeral(true).queue();
        else event.reply(message).setEphemeral(true).queue();
    }

    @Override
    public void reply(MessageEmbed embed, MessageEmbed... embeds) {
        if (event.isAcknowledged())
            event.getHook().sendMessageEmbeds(embed, embeds).queue();
        else event.replyEmbeds(embed, embeds).queue();
    }

    @Override
    public void replyEphemeral(MessageEmbed embed, MessageEmbed... embeds) {
        if (event.isAcknowledged())
            event.getHook().sendMessageEmbeds(embed, embeds).setEphemeral(true).queue();
        else event.replyEmbeds(embed, embeds).setEphemeral(true).queue();
    }


    @Override
    public void setOriginal(String message) {
        if (event.isAcknowledged())
            event.getHook().editOriginal(message).queue();
        else event.reply(message).queue();
    }

    @Override
    public void setOriginal(MessageEmbed embed, MessageEmbed... embeds) {
        if (event.isAcknowledged()) {
            MessageEmbed[] embeds1 = new MessageEmbed[embeds.length + 1];
            embeds1[0] = embed;
            System.arraycopy(embeds, 0, embeds1, 1, embeds.length);
            event.getHook().editOriginalEmbeds(embeds1).queue();
        } else event.replyEmbeds(embed, embeds).queue();
    }
}
