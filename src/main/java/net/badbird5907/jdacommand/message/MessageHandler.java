package net.badbird5907.jdacommand.message;

import net.badbird5907.jdacommand.CommandResult;
import net.badbird5907.jdacommand.annotation.Command;
import net.badbird5907.jdacommand.context.CommandContext;
import net.badbird5907.jdacommand.util.object.Replyable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

public interface MessageHandler {
    MessageResponse onCooldown(Member member, Command command, double timeLeft);

    MessageResponse noPermission(Member member, Command command, Permission permission);

    MessageResponse noPermission(Member member, Command command);

    MessageResponse error(Member member, Command command, Throwable throwable);

    MessageResponse commandResult(Member member, Command command, CommandResult result);

    static void replyTo(Replyable ctx, MessageResponse response) {
        if (response == null)
            return;
        response.respondTo(ctx);
    }
}
