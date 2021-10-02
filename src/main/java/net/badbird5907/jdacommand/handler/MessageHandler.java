package net.badbird5907.jdacommand.handler;

import net.badbird5907.jdacommand.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public interface MessageHandler {
    Message cooldownMessage(User author, Command command);

    Message dmsOnlyMessage(User author, Command command);

    Message permissionMessage(User author, Command command);

    Message serverOnly(User author, Command command);

    Message adminOnly(User author, Command command);

    Message serverOnlyMessage(User author, Command command);

    Message botOwnerOnlyMessage(User author, Command command);

    Message serverOwnerOnlyMessage(User author, Command command);

    Message errorCommandResult(User author,Command command);
    Message successCommandResult(User author,Command command);
    Message noPermsCommandResult(User author,Command command);
    Message otherCommandResult(User author,Command command);
}
