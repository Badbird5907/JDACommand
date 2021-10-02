package net.badbird5907.jdacommand.handler;

import net.badbird5907.jdacommand.Command;
import net.badbird5907.jdacommand.JDACommand;
import net.badbird5907.jdacommand.events.CommandPreProcessEvent;
import net.badbird5907.jdacommand.util.Cooldown;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;

import static net.badbird5907.jdacommand.JDACommand.getInstance;

public class MessageListener extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {

		if (!e.getMessage().getContentRaw().toLowerCase().startsWith(getInstance().prefix.toLowerCase()) || e.getMessage().getAuthor().isBot()) return;

		ArrayList<String> finalArgs = new ArrayList<>();
		int a = 0;
		for (String s : e.getMessage().getContentRaw().split("\\s+")) {
			a++;
			if (a == 1){
				continue;
			}
			finalArgs.add(s);

		}
		final String[] fargs = finalArgs.toArray(new String[0]);
		final String command = e.getMessage().getContentRaw().replaceFirst("(?i)" + getInstance().prefix, "").toLowerCase().split(" ")[0];

		JDACommand.getCommandMap().forEach((name, pair)->{
			if (command.equalsIgnoreCase(name)){ //TODO custom error messages
				Command c = pair.getValue0();
				if (c.disable())
					return;
				CommandPreProcessEvent event = new CommandPreProcessEvent(c.name(),fargs);
				JDACommand.getEventBus().post(event);
				if (event.isCancelled())
					return;
				User author = e.getAuthor();
				if (c.botOwnerOnly()) {
					if (!JDACommand.getInstance().isOwner(e.getAuthor())) {
						handle(JDACommand.getInstance().getMessageHandler().botOwnerOnlyMessage(author,c),e.getMessage());
						return;
					}
				}
				if (c.serverOwnerOnly())
					if (e.getChannelType() == ChannelType.TEXT && !e.getMember().isOwner()) {
						handle(JDACommand.getInstance().getMessageHandler().serverOwnerOnlyMessage(author,c),e.getMessage());
						return;
					}
				if (c.dmsOnly())
					if (e.getChannelType() != ChannelType.PRIVATE) {
						handle(JDACommand.getInstance().getMessageHandler().dmsOnlyMessage(author,c),e.getMessage());
						return;
					}
				if (c.serverOnly() || c.permission().length != 0)
					if (e.getChannelType() != ChannelType.TEXT) {
						handle(JDACommand.getInstance().getMessageHandler().serverOnlyMessage(author,c),e.getMessage());
						return;
					}
				if (c.adminOnly())
					if (e.getChannelType() == ChannelType.TEXT && !e.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
						handle(JDACommand.getInstance().getMessageHandler().adminOnly(author,c),e.getMessage());
						return;
					}
				if (c.permission().length != 0){
					if (e.getChannelType() == ChannelType.TEXT){
						Permission[] permissions = c.permission();
						if (!e.getMember().getPermissions().contains(permissions[0])) {
							handle(JDACommand.getInstance().getMessageHandler().permissionMessage(author,c),e.getMessage());
							return;
						}
					}else return;
				}
				if (c.cooldown() > 0){
					if (Cooldown.isOnCooldown(c.name().toLowerCase(),e.getAuthor().getIdLong())){
						handle(JDACommand.getInstance().getMessageHandler().cooldownMessage(author,c),e.getMessage());
						return;
					}
					Cooldown.addCooldown(c.name().toLowerCase(),e.getAuthor().getIdLong(),c.cooldown());
				}
				CommandManager.process(pair.getValue1(),fargs,e,JDACommand.getCommandMap().get(name.toLowerCase()).getValue2(),c);
			}
		});
	}
	private static void handle(Message message,Message command){
		if (message == null)
			return;
		command.reply(message).queue();
	}
	
}