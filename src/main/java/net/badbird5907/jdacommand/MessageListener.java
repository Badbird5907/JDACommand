package net.badbird5907.jdacommand;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e){
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
        System.out.println(e.getMessage().getContentRaw());
        if(!e.getMessage().getContentRaw().startsWith(JDACommand.getInstance().prefix))
            return;
        String[] args1 = e.getMessage().getContentRaw().split(" ");
        String a = e.getMessage().getContentRaw().replaceFirst(args1[0], "");
        String[] args = a.split(" ");
        JDACommand.commands.forEach(cmd ->{
            if(cmd.name.equalsIgnoreCase(args1[0].replaceFirst(JDACommand.getInstance().prefix, ""))){
                cmd.execute(args, new CommandEvent(args, e.getChannel(), e.getMessage(), e));
            }
        });
    }

}
