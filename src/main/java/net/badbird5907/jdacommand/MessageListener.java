package net.badbird5907.jdacommand;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static net.badbird5907.jdacommand.OctoCommand.*;

public class MessageListener extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (e.getMessage().getContentRaw().startsWith(getInstance().prefix)) {
			String[] args1 = e.getMessage().getContentRaw().split(" ");
			String[] args = e.getMessage().getContentRaw().replaceFirst(args1[0], "").split(" ");
			commands.stream().filter(cmd -> cmd.name.equalsIgnoreCase(getInstance().prefix + args1[0].replaceFirst(getInstance().prefix, ""))).forEach(cmd -> cmd.execute(args, new CommandEvent(args, e.getChannel(), e.getMessage(), e)));
		}
	}
}