package net.badbird5907.jdacommand;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public abstract class Command {
    protected String name = "null";
    protected String desc = "null";
    private JDA jda = JDACommand.getInstance().jda;
    public Command(String name, String desc){
        this.name = name;
        this.desc = desc;
    }
    protected abstract void execute(String[] args, CommandEvent event, User author, Guild guild);
}
