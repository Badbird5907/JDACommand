package net.badbird5907.jdacommand;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.badbird5907.jdacommand.annotation.Command;
import net.badbird5907.jdacommand.annotation.Description;
import net.badbird5907.jdacommand.annotation.Disable;
import net.badbird5907.jdacommand.context.ParameterContext;
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
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.System.err;
import static java.lang.System.out;

public class JDACommand {
    @Getter
    private static final Map<String, CommandWrapper> commandMap = new ConcurrentHashMap<>();
    @Getter
    private static final Map<CommandResult, Object> overrideCommandResult = new ConcurrentHashMap<>();

    private static final List<Provider<?>> providers = new CopyOnWriteArrayList<>();
    @Getter
    private static final EventBus eventBus = new EventBus();
    private static final Pattern COMMAND_REGEX = Pattern.compile("^[-_\\p{L}\\p{N}\\p{sc=Deva}\\p{sc=Thai}]{1,32}$", Pattern.UNICODE_CHARACTER_CLASS);
    private static Map<String, List<SubcommandData>> subMap = new HashMap<>();
    private static JDACommand instance;
    @Getter
    private final Set<Long> owners = new HashSet<>();
    public JDA jda;
    @Setter
    private ReturnCallBack returnCallBack = null;

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
                    new UserContextProvider(),
                    new IMentionableProvider()
            }) {
                registerProvider(provider);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pruneUnusedCommands() {
        for (Guild guild : jda.getGuilds()) {
            guild.retrieveCommands().queue(commands -> {
                for (net.dv8tion.jda.api.interactions.commands.Command command : commands) {
                    if (commandMap.containsKey(command.getName().toLowerCase())) continue;
                    command.delete().queue();
                }
            });
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
     * @param o Class
     */
    public void registerCommand(Object o) {
        //if (alreadyInit.contains(o)) {
        if (false) {
            return;
        }
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
        try {
            out.println("Registering command: " + name);
            if (command.disable() || method.isAnnotationPresent(Disable.class))
                return;
            if (commandMap.containsKey(name) || name.isEmpty()) {
                return;
            }
            boolean subCommand = command.name().contains(" ");
            String[] split;
            String parent = command.name(), sub = null;
            if (subCommand) {
                split = command.name().split(" ");
                parent = split[0].toLowerCase();
                sub = split[1].toLowerCase();
            }
            List<SubWrapper> actions = new ArrayList<>();
            for (Guild guild : jda.getGuilds()) {
                boolean upsert = returnCallBack == null || returnCallBack.shouldUpsertCommand(guild);
                if (upsert) {
                    if (subCommand) {
                        String desc = "Parent Command";
                        if (method.isAnnotationPresent(Description.class)) {
                            desc = method.getAnnotation(Description.class).value();
                        }
                        CommandCreateAction action = guild.upsertCommand(parent, desc);
                        out.println("Upserting subcommand: " + sub + " in command: " + parent);
                        String n = parent;
                        if (!subMap.containsKey(n)) {
                            ArrayList<SubcommandData> list = new ArrayList<>();
                            list.add(new SubcommandData(sub, command.description()));
                            subMap.put(n, list);
                        } else {
                            List<SubcommandData> data = subMap.get(n);
                            data.add(new SubcommandData(sub, command.description()));
                            subMap.put(n, data);
                        }
                        List<SubcommandData> list = subMap.get(n);
                        out.println("aaaaaa - " + list.stream().map(SubcommandData::getName).collect(Collectors.toList()) + " - " + subMap.size());
                        subMap.forEach((k, v) -> {
                            out.println("submap: " + k + " | " + v.stream().map(SubcommandData::getName).collect(Collectors.toList()));
                        });
                        for (SubcommandData subcommandData : list) {
                            out.println("Registering subcommand: " + subcommandData.getName() + " in command: " + parent);
                            if (action.getSubcommands().stream().anyMatch(subcommand -> subcommand.getName().equalsIgnoreCase(subcommandData.getName()))) {
                                continue;
                            }
                            action = action.addSubcommands(subcommandData);
                        }
                        err.println("bbbbbb - " + action.getSubcommands().stream().map(SubcommandData::getName).collect(Collectors.toList()) + " - " + list.size());
                        actions.add(new SubWrapper(action));
                        //actions.add(new SubWrapper(action.addSubcommandGroups(groupData).addSubcommands(data)));
                    } else {
                        CommandCreateAction action = guild.upsertCommand(name.toLowerCase(), command.description());
                        actions.add(new SubWrapper(action));
                    }
                }
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
                System.out.println("Registering parameter: " + pCtx.getName() + " in command: " + name + " with type " + param.getType());
                if (provider.getOptionData(pCtx) != null) {
                    for (SubWrapper subWrapper : actions) {
                        OptionData option = provider.getOptionData(pCtx);
                        if (!option.isRequired() && pCtx.isRequired())
                            option.setRequired(true);
                        if (subWrapper.commandCreateAction != null) {
                            CommandCreateAction action = subWrapper.commandCreateAction;
                            List<SubcommandData> data = action.getSubcommands();
                            if (data.isEmpty()) {
                                out.println("aaaa1");
                                action = action.addOptions(option);
                                subWrapper.commandCreateAction = action;
                            }
                            else {
                                out.println("aaaa2");
                                for (SubcommandData subcommand : action.getSubcommands()) {
                                    SubcommandData newCmd = subcommand.addOptions(option);
                                }
                            }
                        } else if (subWrapper.subcommandGroupData != null) {
                            SubcommandGroupData groupData = subWrapper.subcommandGroupData;
                            for (SubcommandData subcommand : groupData.getSubcommands()) {
                                if (subcommand.getName().equalsIgnoreCase(sub)) {
                                    subcommand.addOptions(option);
                                }
                            }
                        }
                    }
                }
                parameters.add(new Pair<>(pCtx, provider));
            }
            CommandWrapper wrapper = new CommandWrapper(command, name.toLowerCase(), method, o, params, parameters);
            for (SubWrapper subWrapper : actions) {
                if (subWrapper.commandCreateAction != null) {
                    CommandCreateAction action = subWrapper.commandCreateAction;
                    out.println("c");
                    String finalParent = parent;
                    String finalSub = sub;
                    action.queue((c) -> {
                        if (c.getSubcommands().isEmpty()) {
                            System.out.println("Registered command " + c);
                            commandMap.put(c.getName().toLowerCase(), wrapper);
                        } else {
                            String s = finalParent.toLowerCase() + " " + finalSub.toLowerCase();
                            System.out.println("Registered subcommand " + s + " | " + wrapper.getMethod().getName() + "()");
                            commandMap.put(s, wrapper);
                        }
                    });
                } else if (subWrapper.subcommandGroupData != null) {
                    SubcommandGroupData groupData = subWrapper.subcommandGroupData;
                    for (SubcommandData subcommand : groupData.getSubcommands()) {
                        //System.out.println("Registered subcommand " + subcommand);
                        commandMap.put(parent.toLowerCase() + " " + subcommand.getName().toLowerCase(), wrapper);
                    }
                }
            }
            System.out.println("Done Registering command " + name);
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * For testing purposes
     */
    public interface ReturnCallBack {
        boolean shouldUpsertCommand(Guild guild);
    }

    @Getter
    @Setter
    private class SubWrapper {
        private CommandCreateAction commandCreateAction;
        private SubcommandGroupData subcommandGroupData;

        public SubWrapper(CommandCreateAction commandCreateAction, SubcommandGroupData subcommandGroupData) {
            this.commandCreateAction = commandCreateAction;
            this.subcommandGroupData = subcommandGroupData;
        }

        public SubWrapper(CommandCreateAction commandCreateAction) {
            this.commandCreateAction = commandCreateAction;
        }

        public SubWrapper(SubcommandGroupData subcommandGroupData) {
            this.subcommandGroupData = subcommandGroupData;
        }
    }
}
