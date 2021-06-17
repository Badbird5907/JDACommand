package net.badbird5907.jdacommand;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public abstract class CommandBase {
    protected abstract CommandResult execute(String[] args, CommandEvent event, User author, Member member, Guild guild, MessageChannel channel);
}