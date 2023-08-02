package dev.badbird.jdacommand;

import dev.badbird.jdacommand.annotation.*;
import dev.badbird.jdacommand.object.CommandListener;
import dev.badbird.jdacommand.object.ExecutionContext;
import dev.badbird.jdacommand.object.JDACommandSettings;
import dev.badbird.jdacommand.object.ParameterInfo;
import dev.badbird.jdacommand.object.command.BaseCommandInfo;
import dev.badbird.jdacommand.object.command.impl.CommandInfo;
import dev.badbird.jdacommand.object.command.impl.SubGroupInfo;
import dev.badbird.jdacommand.provider.Provider;
import dev.badbird.jdacommand.provider.impl.*;
import dev.badbird.jdacommand.util.Primitives;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.internal.utils.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
public class JDACommand {
    private final JDA jda;
    private final Map<String, CommandInfo> commandMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, Provider<?>> argumentProviders = new ConcurrentHashMap<>();
    private final List<Object> registerLast = new ArrayList<>();
    private boolean autoRegisteredGlobal = false;
    private Pair<SlashCommandData[], CommandInfo[]> cachedGuildCommands;
    private List<String> registered = new ArrayList<>(); // prevent stack overflow
    private JDACommandSettings settings;

    public JDACommand(JDACommandSettings settings) {
        this.settings = settings;
        jda = settings.getJda();
        jda.addEventListener(new CommandListener(this));
        if (settings.isRegisterDefaultProviders()) {
            registerDefaultProviders();
        }
        if (settings.isWaitForJda()) {
            try {
                jda.awaitReady();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void registerDefaultProviders() {
        registerProvider(String.class, new StringProvider());
        registerProvider(Boolean.class, new BooleanProvider());
        registerProvider(ExecutionContext.class, new ExecutionContext.Provider());
        NumberProvider.registerAll(this);
        AllMentionablesProvider.registerAll(this);
        MiscProviders.registerAll(this);
    }

    public void registerProvider(Class<?> clazz, Provider<?> provider) {
        argumentProviders.put(Primitives.wrap(clazz), provider);
    }

    public void registerCmd(Object object) {
        if (object == null) return;
        // if this is a class, instantiate it
        if (object instanceof Class<?>) {
            Class<?> clazz = (Class<?>) object;
            if (shouldAutoInstantiate(clazz)) {
                try {
                    object = clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                throw new IllegalArgumentException("Class " + clazz.getName() + " is not annotated with AutoInstantiate, and cannot be registered.");
            }
        }
        registerLast.remove(object);
        Map<Class<? extends Annotation>, Annotation> distributedAnnotations = new HashMap<>();
        boolean classHasMainCommand = object.getClass().isAnnotationPresent(SlashCommand.class);
        ExtendParent extendParent = object.getClass().getDeclaredAnnotation(ExtendParent.class);
        SubGroup subGroup = object.getClass().getDeclaredAnnotation(SubGroup.class);
        SlashCommand parent = null;
        boolean isSubClass = object.getClass().getDeclaringClass() != null;
        CommandInfo parentInfo = null;
        if (extendParent != null) {
            String name = extendParent.value();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Class " + object.getClass().getName() + " has an ExtendParent annotation, but no name was specified.");
            }
            parentInfo = commandMap.get(name.toLowerCase());
            if (parentInfo == null) { // hasn't been registered yet, defer
                registerLast.add(object);
                return;
            }
            parent = parentInfo.getAnnotation();
            classHasMainCommand = true;
            if (extendParent.inheritAnnotations()) {
                distributedAnnotations.putAll(parentInfo.getAnnotations());
            }
        }
        if (subGroup != null) {
            System.out.println("Registering subgroup " + subGroup.name());
            classHasMainCommand = isSubClass && object.getClass().getDeclaringClass().isAnnotationPresent(SlashCommand.class);
            if (!classHasMainCommand && subGroup.name().isEmpty()) {
                throw new IllegalArgumentException("Class " + object.getClass().getName() + " has a SubGroup annotation, but isn't a subclass of a parent annotation, and doesn't declare a parent command.");
            }
            if (classHasMainCommand)
                parentInfo = commandMap.get(object.getClass().getDeclaringClass().getDeclaredAnnotation(SlashCommand.class).name().toLowerCase());
            else parentInfo = commandMap.get(subGroup.parent().toLowerCase());
            if (parentInfo == null) {
                registerLast.add(object);
                return;
            }
            parent = parentInfo.getAnnotation();
            if (subGroup.inheritAnnotations()) {
                distributedAnnotations.putAll(parentInfo.getAnnotations());
            }
        }
        // annotations on the class itself take precedence over inherited annotations
        for (Annotation annotation : object.getClass().getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(DistributeOnMethods.class)) {
                System.out.println("Distributing annotation " + annotation.annotationType().getName());
                distributedAnnotations.put(annotation.annotationType(), annotation);
            }
        }
        if (classHasMainCommand && extendParent == null && subGroup == null) { // register root command
            boolean subCommandsFound = false;
            for (Method declaredMethod : object.getClass().getDeclaredMethods()) {
                if (declaredMethod.isAnnotationPresent(SlashCommand.class)) {
                    subCommandsFound = true;
                    break;
                }
            }
            if (!subCommandsFound) {
                throw new IllegalArgumentException("Class " + object.getClass().getName() + " has a SlashCommand/ExtendParent parent annotation, but no methods with SlashCommand annotations found.");
            }
            parent = object.getClass().getAnnotation(SlashCommand.class);
            Map<Class<? extends Annotation>, Annotation> annotations = new HashMap<>();
            for (Annotation declaredAnnotation : object.getClass().getDeclaredAnnotations()) {
                annotations.put(declaredAnnotation.annotationType(), declaredAnnotation);
            }
            annotations.putAll(distributedAnnotations);
            parentInfo = commandMap.get(parent.name().toLowerCase());
            if (parentInfo == null) {
                parentInfo = new CommandInfo(parent, object);
            }
            parentInfo.setAnnotations(annotations);
        }
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(SlashCommand.class)) {
                SlashCommand slashCommand = method.getAnnotation(SlashCommand.class);
                System.out.println("Found command " + slashCommand.name());
                if (slashCommand.name().contains(" ")) { // TODO: register as subcommand/group
                    throw new IllegalArgumentException("Command " + slashCommand.name() + " contains spaces, which is not allowed.");
                }
                List<ParameterInfo> parameters = new ArrayList<>();
                for (Parameter parameter : method.getParameters()) {
                    parameters.add(new ParameterInfo(parameter, this));
                }
                Map<Class<? extends Annotation>, Annotation> annotations = new HashMap<>(distributedAnnotations);
                for (Annotation declaredAnnotation : method.getDeclaredAnnotations()) {
                    annotations.put(declaredAnnotation.annotationType(), declaredAnnotation);
                }
                CommandInfo commandInfo = new CommandInfo(slashCommand, object);
                commandInfo.setAnnotations(annotations);
                commandInfo.setMethod(method);
                commandInfo.setInstance(object);
                commandInfo.setParameters(parameters);
                if (subGroup == null) {
                    if (classHasMainCommand) {
                        commandInfo.setParent(parentInfo);
                        parentInfo.getSubCommands().put(slashCommand.name().toLowerCase(), commandInfo);
                    } else {
                        commandMap.put(slashCommand.name().toLowerCase(), commandInfo);
                    }
                } else {
                    BaseCommandInfo subGroupInfo = parentInfo.getSubCommands().get(subGroup.name());
                    if (subGroupInfo == null) {
                        subGroupInfo = new SubGroupInfo(subGroup, parentInfo);
                        parentInfo.getSubCommands().put(subGroup.name().toLowerCase(), subGroupInfo);
                    } else if (!(subGroupInfo instanceof SubGroupInfo)) {
                        throw new IllegalArgumentException("Found conflicting subcommands/groups named " + subGroupInfo.getName());
                    }
                    commandInfo.setParent(subGroupInfo);
                    subGroupInfo.getSubCommands().put(slashCommand.name().toLowerCase(), commandInfo);
                }
            }
        }
        if (parentInfo != null) {
            commandMap.put(parentInfo.getName().toLowerCase(), parentInfo);
        }
        for (Class<?> declaredClass : object.getClass().getDeclaredClasses()) {
            if (shouldAutoInstantiate(declaredClass)) {
                try {
                    registerCmd(declaredClass.getDeclaredConstructor().newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean shouldAutoInstantiate(Class<?> clazz) { // TODO add a configurable callback for this
        if (clazz.isAnnotationPresent(AutoInstantiate.class)) {
            return clazz.getDeclaredAnnotation(AutoInstantiate.class).value();
        }
        boolean should = false;
        for (Annotation declaredAnnotation : clazz.getAnnotations()) {
            if (declaredAnnotation.annotationType().isAnnotationPresent(AutoInstantiate.class)) {
                AutoInstantiate autoInstantiate = declaredAnnotation.annotationType().getDeclaredAnnotation(AutoInstantiate.class);
                if (autoInstantiate.value()) {
                    should = true;
                }
                if (autoInstantiate.forceStop()) {
                    return false;
                }
            }
        }
        return should;
    }

    public void registerLastCommands() {
        if (!registerLast.isEmpty()) {
            new ArrayList<>(registerLast).forEach(this::registerCmd);
            for (Object o : registerLast) {
                System.err.println("Failed to register command class " + o.getClass().getName());
            }
        }
    }

    public Pair<SlashCommandData[], CommandInfo[]> generateGuildCommands() {
        if (cachedGuildCommands != null) return cachedGuildCommands;
        List<SlashCommandData> slashCommands = new ArrayList<>();
        List<CommandInfo> guildSpecific = new ArrayList<>();
        System.out.println("Generating guild commands - " + commandMap.size());
        for (CommandInfo commandInfo : commandMap.values()) {
            if (!commandInfo.isRootCommand() || !commandInfo.getAnnotation().guildOnly()) {
                System.out.println("Skipping command " + commandInfo.getName());
                continue;
            }
            if (commandInfo.isAnnotationPresent(LimitToGuilds.class)) {
                guildSpecific.add(commandInfo);
                continue;
            }
            slashCommands.add(commandInfo.generateCommand());
        }
        return cachedGuildCommands = Pair.of(slashCommands.toArray(new SlashCommandData[0]), guildSpecific.toArray(new CommandInfo[0]));
    }

    public void commitCommands(Guild guild) { // actually register the commands to discord
        registerLastCommands();
        System.out.println("Committing commands for guild " + guild.getName());
        CommandListUpdateAction commandListUpdateAction = guild.updateCommands();
        Pair<SlashCommandData[], CommandInfo[]> commands = generateGuildCommands();
        System.out.println("Found " + commands.getLeft().length + " commands and " + commands.getRight().length + " guild specific commands");
        for (SlashCommandData slashCommandData : commands.getLeft()) {
            commandListUpdateAction = commandListUpdateAction.addCommands(slashCommandData);
        }
        for (CommandInfo commandInfo : commands.getRight()) {
            if (commandInfo.isAnnotationPresent(LimitToGuilds.class)) {
                LimitToGuilds limitToGuilds = commandInfo.getAnnotation(LimitToGuilds.class);
                long[] guilds = limitToGuilds.value();
                boolean found = false;
                for (long l : guilds) {
                    if (l == guild.getIdLong()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    continue;
                }
                commandListUpdateAction = commandListUpdateAction.addCommands(commandInfo.generateCommand());
            }
        }
        commandListUpdateAction.queue(e -> {
            System.out.println("Successfully registered commands for guild " + guild.getName());
        }, e -> {
            System.err.println("Failed to register commands for guild " + guild.getName());
        });
    }

    public void commitCommands() {
        for (Guild guild : jda.getGuilds()) {
            commitCommands(guild);
        }
        if (!autoRegisteredGlobal) {
            commitGlobal();
            autoRegisteredGlobal = true;
        }
    }

    public void commitGlobal() {
        List<CommandInfo> globalCommands = commandMap.values().stream().filter(
                cmd -> !cmd.getAnnotation().guildOnly()
        ).collect(Collectors.toList());
        CommandListUpdateAction commandListUpdateAction = jda.updateCommands();
        for (CommandInfo globalCommand : globalCommands) {
            commandListUpdateAction = commandListUpdateAction.addCommands(globalCommand.generateCommand());
        }
        commandListUpdateAction.queue();
    }

    public BaseCommandInfo resolveCommand(String[] searchList) {
        BaseCommandInfo commandInfo = null;
        for (String s : searchList) {
            if (commandInfo == null) {
                commandInfo = commandMap.get(s.toLowerCase());
            } else {
                commandInfo = commandInfo.getSubCommands().get(s.toLowerCase());
            }
            if (commandInfo == null) {
                break;
            }
        }
        return commandInfo;
    }
}
