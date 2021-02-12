package net.badbird5907.jdacommand;

import net.dv8tion.jda.api.JDA;

import java.util.ArrayList;

public class OctoCommand {
    public String prefix = null;
    public JDA jda = null;
    private static OctoCommand instance;
    public OctoCommand(String prefix, JDA jda){
        this.prefix = prefix;
        this.jda = jda;
    }
    public static ArrayList<Command> commands = new ArrayList<>();
    public void init() {
        instance = this;
    }

    public static OctoCommand getInstance() {
        return instance;
    }
}
