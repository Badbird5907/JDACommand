package net.badbird5907.jdacommand;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.badbird5907.jdacommand.annotation.Command;
import net.badbird5907.jdacommand.annotation.Disable;
import net.badbird5907.jdacommand.context.ParameterContext;
import net.badbird5907.jdacommand.message.DefaultMessageHandler;
import net.badbird5907.jdacommand.message.MessageHandler;
import net.badbird5907.jdacommand.provider.Provider;
import net.badbird5907.jdacommand.provider.impl.*;
import net.badbird5907.jdacommand.util.object.CommandWrapper;
import net.badbird5907.jdacommand.util.object.Pair;
import net.badbird5907.lightning.EventBus;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import static java.lang.System.out;

public class JDACommand {
    @Getter
    private static final Map<String, CommandWrapper> commandMap = new ConcurrentHashMap<>();

    private static final List<Provider<?>> providers = new CopyOnWriteArrayList<>();

    @Getter
    private static final EventBus eventBus = new EventBus();

    private static JDACommand instance;
    public JDA jda;

    @Getter
    private final Set<Long> owners = new HashSet<>();

    @Getter
    private static boolean testing = false;

    @Getter
    @Setter
    private MessageHandler messageHandler = new DefaultMessageHandler();

    private List<net.dv8tion.jda.api.interactions.commands.Command> jdaCmds = new ArrayList<>();

    /**
     * For testing purposes
     */
    public interface ReturnCallBack {
        boolean shouldUpsertCommand(Guild guild);
    }

    @Setter
    private ReturnCallBack returnCallBack = null;

    private static final Pattern COMMAND_REGEX = Pattern.compile("^[\\w-]{1,32}$", Pattern.UNICODE_CHARACTER_CLASS);

    /**
     * Instantiate {@link JDACommand} with just prefix and {@link JDA} instance.
     *
     * @param jda Pass-through the {@link JDA} instance
     */
    public JDACommand(JDA jda) {
        if (instance != null)
            throw new IllegalStateException("Cannot instantiate more than one JDACommand instance.");
        this.jda = jda;
        init();
    }

    /**
     * Instantiate {@link JDACommand} with just prefix and {@link JDA} instance.
     *
     * @param jda    Pass-through the {@link JDA} instance
     * @param owners {@link Long} array of owners
     */
    public JDACommand(JDA jda, Long[] owners) {
        if (instance != null)
            throw new IllegalStateException("Cannot instantiate more than one JDACommand instance.");
        this.jda = jda;
        Collections.addAll(this.owners, owners);
        init();
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
        System.out.println("Initializing JDACommand...");
        try {
            jda.addEventListener(new CommandListener());
            for (Provider provider : new Provider[]{
                    new BooleanContextProvider(),
                    new ChannelContextProvider(),
                    new CommandContextProvider(),
                    new EventContextProvider(),
                    new GuildContextProvider(),
                    new IntContextProvider(),
                    new LongContextProvider(),
                    new MemberContextProvider(),
                    new RoleContextProvider(),
                    new StringContextProvider(),
                    new UserContextProvider()
            }) {
                registerProvider(provider);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * @param o1 Classes
     */
    public void registerCommand(Object... o1) {
        //if (alreadyInit.contains(o)) {
        for (Object o : o1) {
            for (Method m : o.getClass().getDeclaredMethods()) {
                if (m.getAnnotation(Command.class) != null) {
                    Command command = m.getAnnotation(Command.class);
                    registerCommand(command, command.name(), m, o);
                    for (String alias : command.aliases()) {
                        registerCommand(command, alias, m, o);
                    }

                    //alreadyInit.add(o);
                }
            }
        }
        finishRegister();
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
        finishRegister();
    }

    private void registerCommand(Command command, String name, Method method, Object o) {
        try {
            out.println("Registering command: " + name);
            if (command.disable() || method.isAnnotationPresent(Disable.class))
                return;
            if (!COMMAND_REGEX.matcher(name.toLowerCase()).matches()) {
                throw new IllegalArgumentException("Command name must match regex: " + COMMAND_REGEX.pattern() + " see https://discord.com/developers/docs/interactions/application-commands for more info");
            }
            if (commandMap.containsKey(name) || name.isEmpty()) {
                return;
            }

            List<CommandCreateAction> actions = new ArrayList<>();
            if (testing) {
                for (Guild guild : jda.getGuilds()) {
                    boolean upsert = returnCallBack == null || returnCallBack.shouldUpsertCommand(guild);
                    if (upsert) {
                        actions.add(guild.upsertCommand(name.toLowerCase(), command.description()));
                    }
                }
            } else {
                actions.add(jda.upsertCommand(name.toLowerCase(), command.description()));
            }

            Parameter[] params = method.getParameters();
            List<Pair<ParameterContext, Provider<?>>> parameters = new ArrayList<>();
            for (int i = 0; i < params.length; i++) {
                Parameter param = params[i];
                Provider<?> provider = getProvider(param);
                if (provider == null) {
                    throw new IllegalArgumentException("Could not find a Parameter Provider for " + param.getType().getName() + " in " + method.getName());
                }
                ParameterContext pCtx = new ParameterContext(params, i, param, param.getDeclaredAnnotations());
                if (provider.getOptionData(pCtx) != null) {
                    for (CommandCreateAction action : actions) {
                        OptionData option = provider.getOptionData(pCtx);
                        if (!option.isRequired() && pCtx.isRequired())
                            option.setRequired(true);
                        action.addOptions(option);
                    }
                }
                parameters.add(new Pair<>(pCtx, provider));
            }
            CommandWrapper wrapper = new CommandWrapper(command, name.toLowerCase(), method, o, params, parameters);
            for (CommandCreateAction action : actions) {
                action.queue((c) -> {
                    System.out.println("Registered command " + c);
                    commandMap.put(c.getName(), wrapper);
                });
            }
            System.out.println("Done Registering command " + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void finishRegister() {
        List<String> allCmds = new ArrayList<>();
        for (Map.Entry<String, CommandWrapper> entry : commandMap.entrySet()) {
            allCmds.add(entry.getKey());
            Collections.addAll(allCmds, entry.getValue().getCommand().aliases());
        }
        if (testing) {
            for (Guild guild : jda.getGuilds()) {
                guild.retrieveCommands().queue(list -> {
                    for (net.dv8tion.jda.api.interactions.commands.Command command : list) {
                        if (!allCmds.contains(command.getName())) {
                            command.delete().queue();
                            continue;
                        }

                    }
                });
            }
        } else {
            jda.retrieveCommands().queue(list -> {
                for (net.dv8tion.jda.api.interactions.commands.Command command : list) {
                    if (!allCmds.contains(command.getName())) {
                        command.delete().queue();
                        continue;
                    }
                }
            });
        }
    }

    private Provider<?> getProvider(Parameter parameter) {
        return providers.stream().filter(p -> p.getType().equals(parameter.getType()) || Arrays.asList(p.getExtraTypes()).contains(parameter.getType())).findFirst().orElse(null);
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

    public void registerProvider(Provider<?> provider) {
        providers.add(provider);
    }
}
