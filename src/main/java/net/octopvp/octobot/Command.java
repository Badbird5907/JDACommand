package net.octopvp.octobot;

public abstract class Command {
    protected String name = "null";
    protected String desc = "null";
    public Command(String name, String desc){
        this.name = name;
        this.desc = desc;
    }
    protected abstract void execute(String[] args, CommandEvent event);
}
