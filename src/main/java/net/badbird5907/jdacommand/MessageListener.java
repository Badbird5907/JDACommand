package net.badbird5907.jdacommand;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.Locale;

import static net.badbird5907.jdacommand.JDACommand.commands;
import static net.badbird5907.jdacommand.JDACommand.getInstance;

public class MessageListener extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
        /*
        System.out.println(e.getMessage().getContentRaw());
        if(e.getMessage().getContentRaw().startsWith(JDACommand.getInstance().prefix)){
            System.out.println(true);
            String[] args1 = e.getMessage().getContentRaw().split(" ");
            String a = e.getMessage().getContentRaw().replaceFirst(args1[0], "");
            String[] args = a.split(" ");
            System.out.println("Foreach");
            JDACommand.commands.forEach(cmd ->{
                System.out.println(cmd.name);
                if(cmd.name.equalsIgnoreCase(JDACommand.getInstance().prefix + args1[0].replaceFirst(JDACommand.getInstance().prefix, ""))){
                    cmd.execute(args, new CommandEvent(args, e.getChannel(), e.getMessage(), e));
                    System.out.println("Executing: \nArgs: ");
                    Arrays.asList(args).forEach((s)->{
                        System.out.println(s);
                    });
                    System.out.println("--");
                }
            });
        }
         */
		if (!e.getMessage().getContentRaw().startsWith(getInstance().prefix) || e.getMessage().getAuthor().isBot()) return;
		String[] args1 = e.getMessage().getContentRaw().split(" "), args = e.getMessage().getContentRaw().replaceFirst(args1[0], "").split(" ");
		/*
		lol i forgot how to use .filter
		commands.stream().filter(cmd ->
				cmd.name.equalsIgnoreCase(args1[0].toLowerCase().replaceFirst(getInstance().prefix, "")) || Arrays.asList(cmd.aliases).contains(args1[0].replaceFirst(getInstance().prefix,"")))
				.forEach(cmd -> CommandManager.process(cmd,args,e));
		 */
		for (int i = -1; i < commands.size(); i++) {
			Command cmd = commands.get(i);
			if(cmd.name.equalsIgnoreCase(args1[0].toLowerCase().replaceFirst(getInstance().prefix,""))){
				CommandManager.process(cmd,args,e);
				break;
			}
			else {
				for (String alias : cmd.aliases) {
					if(alias.equalsIgnoreCase(args1[0].toLowerCase().replaceFirst(getInstance().prefix,""))){
						CommandManager.process(cmd,args,e);
					}
				}

			}
		}
	}
}