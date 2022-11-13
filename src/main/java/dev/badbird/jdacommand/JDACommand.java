package dev.badbird.jdacommand;

import dev.badbird.jdacommand.impl.JDACommandImpl;
import dev.badbird.jdacommand.object.CommandInfo;
import dev.badbird.jdacommand.provider.Provider;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;
import java.util.Map;

public interface JDACommand {
    static JDACommand createDefault(JDA jda) {
        return new JDACommandImpl(jda);
    }

    JDACommand registerCommands(Object... commands);

    default JDACommand registerCommand(Object o) {
        return registerCommands(o);
    }

    JDA getJDA();

    JDACommand registerServerCommands(Guild guild);

    JDACommand registerGlobalCommands();

    default JDACommand registerAllServerCommands() {
        for (Guild guild : getJDA().getGuilds()) {
            registerServerCommands(guild);
        }
        return this;
    }

    Map<String, CommandInfo> getCommandMap();

    Map<Class<?>, Provider<?>> getProviderMap();

    JDACommand registerProvider(Class<?> type, Provider<?> provider);
}
