package net.badbird5907.jdacommand;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;

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

		JDACommand.getCommandMap().forEach((name,pair)->{
			if (command.equalsIgnoreCase(name)){ //TODO custom error messages
				Command c = pair.getValue0();
				if (c.disable())
					return;
				if (c.botOwnerOnly())
					if (!JDACommand.getInstance().isOwner(e.getAuthor())) {
						return;
					}
				if (c.serverOwnerOnly())
					if (e.getChannelType() == ChannelType.CATEGORY && !e.getMember().isOwner())
						return;
				if (c.dmsOnly())
					if (e.getChannelType() != ChannelType.PRIVATE)
						return;
				if (c.serverOnly())
					if (e.getChannelType() != ChannelType.CATEGORY)
						return;
				if (c.adminOnly())
					if (e.getChannelType() == ChannelType.CATEGORY && !e.getMember().getPermissions().contains(Permission.ADMINISTRATOR))
						return;
				if (c.permission().length != 0){
					if (e.getChannelType() == ChannelType.CATEGORY){
						Permission[] permissions = c.permission();
						if (!e.getMember().getPermissions().contains(permissions[0]))
							return;
					}
				}
				CommandManager.process(pair.getValue1(),fargs,e,JDACommand.getCommandMap().get(name.toLowerCase()).getValue2());
			}
		});
	}
}