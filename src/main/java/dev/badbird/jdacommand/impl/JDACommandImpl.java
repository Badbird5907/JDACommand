package dev.badbird.jdacommand.impl;

import dev.badbird.jdacommand.JDACommand;
import dev.badbird.jdacommand.object.CommandInfo;
import dev.badbird.jdacommand.provider.Provider;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JDACommandImpl implements JDACommand {
    private final JDA jda;

    private final Map<String, CommandInfo> commandMap = new HashMap<>();

    private final Map<Class<?>, Provider<?>> providerMap = new HashMap<>();

    @Override
    public JDACommand registerCommands(Object... commands) {
        return this;
    }

    @Override
    public JDA getJDA() {
        return jda;
    }

    @Override
    public JDACommand registerServerCommands(Guild guild) {
        return this;
    }

    @Override
    public JDACommand registerGlobalCommands() {
        return this;
    }

    @Override
    public Map<String, CommandInfo> getCommandMap() {
        return commandMap;
    }

    @Override
    public Map<Class<?>, Provider<?>> getProviderMap() {
        return providerMap;
    }

    @Override
    public JDACommand registerProviders(Provider<?>... providers) {

        return this;
    }
}
