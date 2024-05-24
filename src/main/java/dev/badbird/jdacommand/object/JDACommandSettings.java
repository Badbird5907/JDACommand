package dev.badbird.jdacommand.object;

import dev.badbird.jdacommand.inject.DIFramework;
import dev.badbird.jdacommand.util.ReturnableTypeCallback;
import lombok.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

@Getter
@Setter
@Builder(builderMethodName = "")
public class JDACommandSettings {
    @Builder.Default
    private boolean waitForJda = true;
    @Builder.Default
    private boolean registerGlobal = false; // public commands take up to an hour to register, use guild commands for testing
    @Builder.Default
    private boolean registerDefaultProviders = true;
    @Builder.Default
    private boolean commitOnJoin = true;
    private JDA jda;
    private ShardManager shardManager; // if you want to use the shard manager instead of the jda instance
    @Builder.Default
    private List<ReturnableTypeCallback<Boolean, ExecutionContext>> preProcessors = new ArrayList<>(), postProcessors = new ArrayList<>();
    private DIFramework dependencyInjector;
    @Builder.Default
    private int maxExecutionCacheSize = 1500;
    @Builder.Default
    private int maxExecutionMinutes = 30;
    @Builder.Default
    private ReturnableCallback<ExecutorService> executorService = () -> Executors.newFixedThreadPool(5);

    public static JDACommandSettingsBuilder builder(JDA jda) {
        return new JDACommandSettingsBuilder().jda(jda);
    }
    public static JDACommandSettingsBuilder builder(ShardManager shardManager) {
        return new JDACommandSettingsBuilder().shardManager(shardManager);
    }

    public static class JDACommandSettingsBuilder {
        public JDACommandSettings create() { // workaround for the accesslevel set to private
            return build();
        }
        private static JDACommandSettingsBuilder __builder() {
            return new JDACommandSettingsBuilder();
        }
        public JDACommandSettingsBuilder addPreProcessor(ReturnableTypeCallback<Boolean, ExecutionContext> preProcessor) {
            if (preProcessors$value == null)
                preProcessors$value = new ArrayList<>();
            preProcessors$value.add(preProcessor);
            preProcessors$set = true;
            return this;
        }
        public JDACommandSettingsBuilder addPostProcessor(ReturnableTypeCallback<Boolean, ExecutionContext> postProcessor) {
            if (postProcessors$value == null)
                postProcessors$value = new ArrayList<>();
            postProcessors$value.add(postProcessor);
            postProcessors$set = true;
            return this;
        }
    }

    public static interface ReturnableCallback<R> {
        R run();
    }
}
