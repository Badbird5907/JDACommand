package net.badbird5907.jdacommand.handler;

import net.badbird5907.jdacommand.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public interface MessageHandler {
    Object cooldownMessage(User author, Command command);

    Object dmsOnlyMessage(User author, Command command);

    Object serverOnly(User author, Command command);

    Object adminOnly(User author, Command command);

    Object botOwnerOnlyMessage(User author, Command command);

    Object serverOwnerOnlyMessage(User author, Command command);

    Object errorCommandResult(User author,Command command);
    Object successCommandResult(User author,Command command);
    Object noPermsCommandResult(User author,Command command);
    Object otherCommandResult(User author,Command command);
    Object unknownCommand(User author,String command);
}
