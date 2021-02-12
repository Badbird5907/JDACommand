package net.badbird5907.jdacommand;

import net.dv8tion.jda.api.JDA;

import java.util.ArrayList;

public class JDACommand {
    public String prefix = null;
    public JDA jda = null;
    private static JDACommand instance;
    public JDACommand(String prefix, JDA jda){
        this.prefix = prefix;
        this.jda = jda;
    }
    public static ArrayList<Command> commands = new ArrayList<>();
    public void init() {
        instance = this;
        jda.addEventListener(new MessageListener());
    }
    public static void registerCommand(Command command){
        commands.add(command);
    }

    public static JDACommand getInstance() {
        return instance;
    }
}
