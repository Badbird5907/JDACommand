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
    private List<String> packageScan;
    private boolean waitForJda = true;
    private boolean registerDefaultProviders = true;

    public static JDACommandSettingsBuilder builder(JDA jda) {
        return _builder().jda(jda);
    }

    public static class JDACommandSettingsBuilder {
        public void scanPackage(String s) {
            packageScan.add(s);
        }
        public JDACommandSettings create() { // workaround for the accesslevel set to private
            return build();
        }
    }
}
