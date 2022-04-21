package net.badbird5907.jdacommand.util.object;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@RequiredArgsConstructor
public class SlashCommandReplyable implements Replyable {
    private final SlashCommandInteractionEvent event;

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
