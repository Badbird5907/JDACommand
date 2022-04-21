package net.badbird5907.jdacommand.util.object;

import net.dv8tion.jda.api.entities.MessageEmbed;

public interface Replyable {
    void reply(String message);

    void reply(MessageEmbed embed, MessageEmbed... embeds);

    void replyEphemeral(String message);

    void replyEphemeral(MessageEmbed embed, MessageEmbed... embeds);

    void setOriginal(String message);

    void setOriginal(MessageEmbed embed, MessageEmbed... embeds);
}
