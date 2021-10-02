package net.badbird5907.jdacommand.handler;

import net.badbird5907.jdacommand.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public class SimpleMessageHandler implements MessageHandler {
    @Override
    public Message cooldownMessage(User author, Command command) {
        return null;
    }

    @Override
    public Message dmsOnlyMessage(User author, Command command) {
        return null;
    }

    @Override
    public Message permissionMessage(User author, Command command) {
        return null;
    }

    @Override
    public Message serverOnly(User author, Command command) {
        return null;
    }

    @Override
    public Message adminOnly(User author, Command command) {
        return null;
    }

    @Override
    public Message serverOnlyMessage(User author, Command command) {
        return null;
    }

    @Override
    public Message botOwnerOnlyMessage(User author, Command command) {
        return null;
    }

    @Override
    public Message serverOwnerOnlyMessage(User author, Command command) {
        return null;
    }

    @Override
    public Message errorCommandResult(User author, Command command) {
        return null;
    }

    @Override
    public Message successCommandResult(User author, Command command) {
        return null;
    }

    @Override
    public Message noPermsCommandResult(User author, Command command) {
        return null;
    }

    @Override
    public Message otherCommandResult(User author, Command command) {
        return null;
    }
}
