package dev.badbird.jdacommand;

import dev.badbird.jdacommand.object.CommandListener;
import dev.badbird.jdacommand.object.JDACommandSettings;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;

public class JDACommand {
    @Getter
    private final JDA jda;
    public JDACommand(JDACommandSettings settings) {
        jda = settings.getJda();
        jda.addEventListener(new CommandListener());
        if (settings.isWaitForJda()) {
            try {
                jda.awaitReady();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
