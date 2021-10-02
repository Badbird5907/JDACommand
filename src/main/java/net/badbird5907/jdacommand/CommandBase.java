package net.badbird5907.jdacommand;

import net.badbird5907.jdacommand.events.CommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public abstract class CommandBase {
    public CommandBase(){ //using something random for registering
        JDACommand.getInstance().registerCommand(this);
    }
    public abstract CommandResult execute(String[] args, CommandEvent event, User author, Member member, Guild guild, MessageChannel channel);
}