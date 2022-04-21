package net.badbird5907.jdacommand.message;

import net.badbird5907.jdacommand.CommandResult;
import net.badbird5907.jdacommand.annotation.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

public class DefaultMessageHandler implements MessageHandler {
    @Override
    public MessageResponse onCooldown(Member member, Command command, double timeLeft) {
        return new MessageResponse("This command is on cooldown for another " + timeLeft + " seconds!", true);
    }

    @Override
    public MessageResponse noPermission(Member member, Command command, Permission permission) {
        return new MessageResponse("You do not have permission to use this command!", true);
    }
    @Override
    public MessageResponse noPermission(Member member, Command command) {
        return new MessageResponse("You do not have permission to use this command!", true);
    }

    @Override
    public MessageResponse error(Member member, Command command, Throwable throwable) {
        return new MessageResponse("An error occurred while executing this command!", true);
    }

    @Override
    public MessageResponse commandResult(Member member, Command command, CommandResult result) {
        switch (result) {
            case SUCCESS:
            case OTHER:
                return null;
            case NO_PERMS:
                return noPermission(member, command);
            case ERROR:
                return error(member, command, null);
        }
        return null;
    }
}
