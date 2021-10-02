package net.badbird5907.jdacommand.handler;

import net.badbird5907.jdacommand.Command;
import net.dv8tion.jda.api.entities.User;

public class SimpleMessageHandler implements MessageHandler {
    @Override
    public Object cooldownMessage(User author, Command command) {
        return null;
    }

    @Override
    public Object dmsOnlyMessage(User author, Command command) {
        return null;
    }

    @Override
    public Object permissionMessage(User author, Command command) {
        return null;
    }

    @Override
    public Object serverOnly(User author, Command command) {
        return null;
    }

    @Override
    public Object adminOnly(User author, Command command) {
        return null;
    }

    @Override
    public Object botOwnerOnlyMessage(User author, Command command) {
        return null;
    }

    @Override
    public Object serverOwnerOnlyMessage(User author, Command command) {
        return null;
    }

    @Override
    public Object errorCommandResult(User author, Command command) {
        return null;
    }

    @Override
    public Object successCommandResult(User author, Command command) {
        return null;
    }

    @Override
    public Object noPermsCommandResult(User author, Command command) {
        return null;
    }

    @Override
    public Object otherCommandResult(User author, Command command) {
        return null;
    }
}
