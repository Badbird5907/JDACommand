package dev.badbird.jdacommand.example;

import dev.badbird.jdacommand.JDACommand;
import dev.badbird.jdacommand.example.commands.TestCommand;
import dev.badbird.jdacommand.object.JDACommandSettings;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TestBot {
    public static void main(String[] args) throws IOException {
        String token = new String(Files.readAllBytes(new File("token.txt").toPath()));
        JDA jda = JDABuilder.createDefault(token).build();
        JDACommand jdaCommand = new JDACommand(JDACommandSettings.builder(jda).create());
        jdaCommand.registerCmd(new TestCommand());
    }
}
