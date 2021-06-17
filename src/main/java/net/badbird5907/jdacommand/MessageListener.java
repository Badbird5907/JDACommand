package net.badbird5907.jdacommand;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static net.badbird5907.jdacommand.JDACommand.commands;
import static net.badbird5907.jdacommand.JDACommand.getInstance;

public class MessageListener extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {

		if (!e.getMessage().getContentRaw().startsWith(getInstance().prefix) || e.getMessage().getAuthor().isBot()) return;

		ArrayList<String> finalArgs = new ArrayList<>();
		int a = 0;
		for (String s : e.getMessage().getContentRaw().split(" ")) {
			a++;
			if (a == 1){
				continue;
			}
			finalArgs.add(s);
		}
		final String[] fargs = finalArgs.toArray(new String[0]);
		final String command = e.getMessage().getContentRaw().replaceFirst(getInstance().prefix, "").toLowerCase().split(" ")[0];

		for (int i = 0; i < commands.size(); i++) {
			Command cmd = commands.get(i);
			if(cmd.name.equalsIgnoreCase(command)){
				CommandManager.process(cmd,fargs,e);
			}
			else {
				for (String alias : cmd.aliases) {
					if(alias.equalsIgnoreCase(command)){
						CommandManager.process(cmd,fargs,e);
					}
				}
			}
		}

	}
}