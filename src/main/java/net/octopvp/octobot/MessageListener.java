package net.octopvp.octobot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        if(e.getMessage().getContentRaw().startsWith(OctoCommand.getInstance().prefix)){
            String[] args1 = e.getMessage().getContentRaw().split(" ");
            String a = e.getMessage().getContentRaw().replaceFirst(args1[0], "");
            String[] args = a.split(" ");
            OctoCommand.commands.forEach(cmd ->{
                if(cmd.name.equalsIgnoreCase(OctoCommand.getInstance().prefix + args1[0].replaceFirst(OctoCommand.getInstance().prefix, ""))){
                    cmd.execute(args, new CommandEvent(args, e.getChannel(), e.getMessage(), e));
                }
            });
        }
    }

}
