package dev.badbird.jdacommand.object;

import lombok.*;
import net.dv8tion.jda.api.JDA;

import java.util.List;

@Getter
@Setter
@Builder(builderMethodName = "_builder", access = AccessLevel.PRIVATE)
public class JDACommandSettings {
    @NonNull
    private JDA jda;
    @Builder.Default
    private boolean waitForJda = true;
    @Builder.Default
    private boolean registerDefaultProviders = true;
    @Builder.Default
    private boolean commitOnJoin = true;

    public static JDACommandSettingsBuilder builder(JDA jda) {
        return _builder().jda(jda);
    }

    public static class JDACommandSettingsBuilder {
        public JDACommandSettings create() { // workaround for the accesslevel set to private
            return build();
        }
    }
}
