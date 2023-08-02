package dev.badbird.jdacommand.object;

import lombok.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.List;

@Getter
@Setter
@Builder(builderMethodName = "_builder", access = AccessLevel.PRIVATE)
public class JDACommandSettings {
    @Builder.Default
    private boolean waitForJda = true;
    @Builder.Default
    private boolean registerDefaultProviders = true;
    @Builder.Default
    private boolean commitOnJoin = true;
    private JDA jda;
    private ShardManager shardManager; // if you want to use the shard manager instead of the jda instance

    public static JDACommandSettingsBuilder builder(JDA jda) {
        return _builder().jda(jda);
    }
    public static JDACommandSettingsBuilder builder(ShardManager shardManager) {
        return _builder().shardManager(shardManager);
    }

    public static class JDACommandSettingsBuilder {
        public JDACommandSettings create() { // workaround for the accesslevel set to private
            return build();
        }
    }
}
