package net.badbird5907.jdacommand.message;

import lombok.Getter;
import lombok.Setter;
import net.badbird5907.jdacommand.context.CommandContext;
import net.badbird5907.jdacommand.util.object.Replyable;
import net.dv8tion.jda.api.entities.MessageEmbed;

@Getter
@Setter
public class MessageResponse {
    private String message;
    private MessageEmbed embed;
    private boolean ephemeral;

    public MessageResponse(String message, boolean ephemeral) {
        this.message = message;
        this.ephemeral = ephemeral;
    }

    public MessageResponse(MessageEmbed embed, boolean ephemeral) {
        this.embed = embed;
        this.ephemeral = ephemeral;
    }

    public MessageResponse() {
    }

    public void respondTo(Replyable replyable) {
        if (message != null) {
            if (ephemeral) {
                replyable.replyEphemeral(message);
                return;
            }
            replyable.reply(message);
        } else if (embed != null) {
            if (ephemeral) {
                replyable.replyEphemeral(embed);
                return;
            }
            replyable.reply(embed);
        }
    }
}
