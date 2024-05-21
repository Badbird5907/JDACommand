package dev.badbird.jdacommand.session;

import dev.badbird.jdacommand.object.ExecutionContext;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PACKAGE)
public class ExecutionSession {
    private ExecutionContext context;
    private Object instance;
    private UUID sessionId;

    public void execute(Object[] args, Method method) throws InvocationTargetException, IllegalAccessException {
        method.invoke(instance, args);
    }

    public void finishExecution() {
        ExecutionSessionHandler.INSTANCE.removeSession(sessionId);
    }

    protected void finalize() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
}
