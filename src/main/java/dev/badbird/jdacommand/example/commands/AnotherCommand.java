package dev.badbird.jdacommand.example.commands;

import dev.badbird.jdacommand.annotation.Arg;
import dev.badbird.jdacommand.annotation.DeferReply;
import dev.badbird.jdacommand.annotation.SlashCommand;
import dev.badbird.jdacommand.object.ExecutionContext;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

@DeferReply
public class AnotherCommand {
    @SlashCommand(name = "test", description = "test command")
    public void test(ExecutionContext ctx, @Arg(value = "i", description = "some value", required = false) VoiceChannel channel) {
        System.out.println("test");
        if (channel != null)
            ctx.reply("hi " + channel.getAsMention());
        else
            ctx.reply("hi ");
    }
}
