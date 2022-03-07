package net.badbird5907.jdacommand;

import lombok.Getter;
import lombok.SneakyThrows;
import net.badbird5907.jdacommand.annotation.Command;
import net.badbird5907.jdacommand.annotation.Disable;
import net.badbird5907.jdacommand.context.ParameterContext;
import net.badbird5907.jdacommand.provider.Provider;
import net.badbird5907.jdacommand.util.object.CommandWrapper;
import net.badbird5907.jdacommand.util.object.Pair;
import net.badbird5907.lightning.EventBus;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.System.out;

public class JDACommand {
    @Getter
    private static Map<String, CommandWrapper> commandMap = new ConcurrentHashMap<>();
    @Getter
    private static Map<CommandResult, Object> overrideCommandResult = new ConcurrentHashMap<>();

    private static List<Provider<?>> providers = new CopyOnWriteArrayList<>();

    @Getter
    private static EventBus eventBus = new EventBus();

    private static JDACommand instance;
    public String prefix;
    public JDA jda;

    @Getter
    private Set<Long> owners = new HashSet<>();
    private List<Object> alreadyInit = new ArrayList<>();

    /**
     * Instantiate {@link JDACommand} with just prefix and {@link JDA} instance.
     *
     * @param prefix Bot prefix
     * @param jda    Pass-through the {@link JDA} instance
     */
    public JDACommand(String prefix, JDA jda) {
        if (instance != null)
            throw new IllegalStateException("Cannot instantiate more than one JDACommand instance.");
        this.prefix = prefix;
        this.jda = jda;
        init();
    }

    /**
     * Instantiate {@link JDACommand} with just prefix and {@link JDA} instance.
     *
     * @param prefix Bot prefix
     * @param jda    Pass-through the {@link JDA} instance
     * @param owners {@link Long} array of owners
     */
    public JDACommand(String prefix, JDA jda, Long[] owners) {
        if (instance != null)
            throw new IllegalStateException("Cannot instantiate more than one JDACommand instance.");
        this.prefix = prefix;
        this.jda = jda;
        Collections.addAll(this.owners, owners);
        init();
    }

    /**
     * override the message sent for every {@link CommandResult}
     * for example: {@link CommandResult#ERROR} would usually return "There was an error processing the command!"
     * you can override the message and send a string, or an {@link MessageEmbed}
     *
     * @param commandResult
     * @param message
     * @return
     */
    public JDACommand overrideCommandResultMessage(CommandResult commandResult, String message) {
        overrideCommandResult.remove(commandResult);
        overrideCommandResult.put(commandResult, message);
        return this;
    }

    /**
     * override the message sent for every {@link CommandResult}
     * for example: {@link CommandResult#ERROR} would usually return "There was an error processing the command!"
     * you can override the message and send a string, or an {@link MessageEmbed}
     *
     * @param commandResult
     * @param message
     * @return
     */
    public JDACommand overrideCommandResultMessage(CommandResult commandResult, MessageEmbed message) {
        overrideCommandResult.remove(commandResult);
        overrideCommandResult.put(commandResult, message);
        return this;
    }

    /**
     * unset a command result override {@link JDACommand#overrideCommandResultMessage)}
     *
     * @param commandResult
     * @return
     */
    public JDACommand unsetCommandResultOverride(CommandResult commandResult) {
        overrideCommandResult.remove(commandResult);
        return this;
    }

    /**
     * Get the {@link JDACommand instance.}
     *
     * @return instance
     */
    public static JDACommand getInstance() {
        return instance;
    }

    private void init() {
        instance = this;
        jda.addEventListener(new CommandListener());
    }

    /**
     * print all registered commands to console (for debug purposes).
     */
    public void printAllRegisteredCommands() {
        commandMap.forEach((name, pair) -> out.println("Command: Name: " + name + " Annotation:" + pair.getCommand() + " Method: " + pair.getMethod()));
    }

    /**
     * Register a command
     * Example: registerCommand(MyCommand.class);
     *
     * @param o Class
     */
    public void registerCommand(Object o) {
        if (alreadyInit.contains(o)) {
            return;
        }
        for (Method m : o.getClass().getDeclaredMethods()) {
            if (m.getAnnotation(Command.class) != null && m.getReturnType() == CommandResult.class) {
                Command command = m.getAnnotation(Command.class);
                registerCommand(command, command.name(), m, o);
                for (String alias : command.aliases()) {
                    registerCommand(command, alias, m, o);
                }

                alreadyInit.add(o);
            }
        }
    }

    /**
     * Register all commands in a package, <b>also affects sub-packages</b>
     *
     * @param p Package name, eg: my.package.commands
     */
    @SneakyThrows
    public void registerCommandsInPackage(String p) {
        Reflections reflections = new Reflections(p);
        Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);
        for (Class<?> aClass : classes) {
            if (aClass.isAnnotationPresent(Disable.class))
                return;
            Object instance = null;
            for (Method method : aClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Command.class)) {
                    Command command = method.getAnnotation(Command.class);
                    if (command.name().equals("")) {
                        throw new IllegalArgumentException("Command name cannot be empty!");
                    }
                    if (Modifier.isPrivate(method.getModifiers()) || Modifier.isProtected(method.getModifiers())) {
                        if (instance == null) {
                            instance = aClass.newInstance();
                        }
                    }
                    registerCommand(command, command.name(), method, instance);
                }
            }
        }
    }

    private void registerCommand(Command command, String name, Method method, Object o) {
        if (command.disable() || method.isAnnotationPresent(Disable.class))
            return;
        if (commandMap.containsKey(name) || name.isEmpty()) {
            return;
        }
        CommandCreateAction action = jda.upsertCommand(name.toLowerCase(), command.description());
        Parameter[] params = method.getParameters();
        List<Pair<ParameterContext, Provider<?>>> parameters = new ArrayList<>();
        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];
            Provider<?> provider = getProvider(param);
            if (provider == null) {
                throw new IllegalArgumentException("Could not find a Parameter Provider for " + param.getType().getName() + " in " + method.getName());
            }
            ParameterContext pCtx = new ParameterContext(params, i, param, param.getDeclaredAnnotations());
            action.addOptions(provider.getOptionData(pCtx));
            parameters.add(new Pair<>(pCtx, provider));
        }
        CommandWrapper wrapper = new CommandWrapper(command, name.toLowerCase(), method, o, params, parameters);
        commandMap.put(name, wrapper);
    }

    private Provider<?> getProvider(Parameter parameter) {
        return providers.stream().filter(p -> p.getType().equals(parameter.getType())).findFirst().orElse(null);
    }

    /**
     * Add a owner
     * See {@link JDACommand#isOwner(User)}
     *
     * @param l owner id
     */
    public void addOwner(long l) {
        owners.add(l);
    }

    /**
     * Returns if a {@link User} is defined as a owner. See {@link JDACommand#addOwner(long)}
     *
     * @param user user
     * @return true - if the user is a owner | false - if the user is not a owner.
     */
    public boolean isOwner(User user) {
        return owners.contains(user.getIdLong());
    }
}
