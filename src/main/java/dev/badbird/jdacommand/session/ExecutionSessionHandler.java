package dev.badbird.jdacommand.session;

import com.google.common.cache.*;
import dev.badbird.jdacommand.JDACommand;
import dev.badbird.jdacommand.inject.InjectorManager;
import dev.badbird.jdacommand.object.ExecutionContext;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ExecutionSessionHandler {
    public static ExecutionSessionHandler INSTANCE = new ExecutionSessionHandler();

    private Cache<UUID, ExecutionSession> sessionCache;
    public void init(JDACommand cmd) {
        sessionCache = CacheBuilder.newBuilder()
                .maximumSize(cmd.getSettings().getMaxExecutionCacheSize())
                .expireAfterWrite(cmd.getSettings().getMaxExecutionMinutes(), TimeUnit.MINUTES)
                // .removalListener((RemovalListener<UUID, ExecutionSession>) notification -> {})
                .build();
    }

    public ExecutionSession newSession(Class<?> clazz, ExecutionContext ctx) {
        UUID sessionId = UUID.randomUUID();
        Object inst = null;
        try {
            inst = clazz.getDeclaredConstructor().newInstance();
            InjectorManager.getInstance().injectInstance(inst);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create new instance of class " + clazz.getName(), e);
        }
        ExecutionSession session = new ExecutionSession(ctx, inst, sessionId);
        ctx.setSession(session);
        sessionCache.put(sessionId, session);
        return session;
    }

    public void removeSession(UUID sessionId) {
        sessionCache.invalidate(sessionId);
    }

    public ExecutionSession getSession(String executionId) {
        return sessionCache.getIfPresent(UUID.fromString(executionId));
    }
}
