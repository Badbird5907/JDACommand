package net.badbird5907.jdacommand.message;

import net.badbird5907.jdacommand.CommandResult;
import net.badbird5907.jdacommand.annotation.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

public class NoOpMessageHandler implements MessageHandler {
    @Override
    public MessageResponse onCooldown(Member member, Command command, double timeLeft) {
        return null;
    }

    @Override
    public MessageResponse noPermission(Member member, Command command, Permission permission) {
        return null;
    }

    @Override
    public MessageResponse noPermission(Member member, Command command) {
        return null;
    }

    @Override
    public MessageResponse error(Member member, Command command, Throwable throwable) {
        return null;
    }

    @Override
    public MessageResponse commandResult(Member member, Command command, CommandResult result) {
        return null;
    }
}
