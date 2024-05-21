package dev.badbird.jdacommand;

import dev.badbird.jdacommand.annotation.*;
import dev.badbird.jdacommand.inject.InjectorManager;
import dev.badbird.jdacommand.object.CommandListener;
import dev.badbird.jdacommand.object.ExecutionContext;
import dev.badbird.jdacommand.object.JDACommandSettings;
import dev.badbird.jdacommand.object.ParameterInfo;
import dev.badbird.jdacommand.object.command.BaseCommandInfo;
import dev.badbird.jdacommand.object.command.impl.CommandInfo;
import dev.badbird.jdacommand.object.command.impl.SubGroupInfo;
import dev.badbird.jdacommand.provider.Provider;
import dev.badbird.jdacommand.provider.impl.*;
import dev.badbird.jdacommand.session.ExecutionSessionHandler;
import dev.badbird.jdacommand.util.Primitives;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class JDACommand {
    private final Map<String, CommandInfo> commandMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, Provider<?>> argumentProviders = new ConcurrentHashMap<>();
    private final List<Class<?>> registerLast = new ArrayList<>();
    private boolean autoRegisteredGlobal = false;
    private Pair<SlashCommandData[], CommandInfo[]> cachedGuildCommands;
    private List<String> registered = new ArrayList<>(); // prevent stack overflow
    @Getter
    private JDACommandSettings settings;

    public JDACommand(JDACommandSettings settings) {
        this.settings = settings;
        if (settings.getShardManager() != null)
            settings.getShardManager().addEventListener(new CommandListener(this));
        else if (settings.getJda() != null)
            settings.getJda().addEventListener(new CommandListener(this));
        if (settings.isRegisterDefaultProviders()) {
            registerDefaultProviders();
        }
        if (settings.isWaitForJda() && settings.getJda() != null) {
            try {
                settings.getJda().awaitReady();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (settings.getDependencyInjector() != null) {
            InjectorManager.getInstance().handleDIFramework(settings.getDependencyInjector());
        }
        ExecutionSessionHandler.INSTANCE.init(this);
    }

    public JDACommand registerPackage(String packageName) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(Scanners.SubTypes.filterResultsBy(new FilterBuilder()))
                .addUrls(ClasspathHelper.forPackage(packageName))
                .filterInputsBy(new FilterBuilder().add(s -> s.startsWith(packageName))));
        for (Class<?> aClass : reflections.getSubTypesOf(Object.class)) {
            registerCmd(aClass);
        }
        return this;
    }

    public JDACommand registerDefaultProviders() {
        registerProvider(String.class, new StringProvider());
        registerProvider(Boolean.class, new BooleanProvider());
        registerProvider(ExecutionContext.class, new ExecutionContext.Provider());
        NumberProvider.registerAll(this);
        AllMentionablesProvider.registerAll(this);
        MiscProviders.registerAll(this);
        return this;
    }

    public JDACommand registerProvider(Class<?> clazz, Provider<?> provider) {
        argumentProviders.put(Primitives.wrap(clazz), provider);
        return this;
    }

    public JDACommand registerCmd(Class<?> clazz) {
        if (clazz == null) return this;
        registerLast.remove(clazz);
        Map<Class<? extends Annotation>, Annotation> distributedAnnotations = new HashMap<>();
        boolean classHasMainCommand = clazz.isAnnotationPresent(SlashCommand.class);
        ExtendParent extendParent = clazz.getDeclaredAnnotation(ExtendParent.class);
        SubGroup subGroup = clazz.getDeclaredAnnotation(SubGroup.class);
        SlashCommand parent = null;
        boolean isSubClass = clazz.getDeclaringClass() != null;
        CommandInfo parentInfo = null;
        if (extendParent != null) {
            String name = extendParent.value();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Class " + clazz.getName() + " has an ExtendParent annotation, but no name was specified.");
            }
            parentInfo = commandMap.get(name.toLowerCase());
            if (parentInfo == null) { // hasn't been registered yet, defer
                registerLast.add(clazz);
                return this;
            }
            parent = parentInfo.getAnnotation();
            classHasMainCommand = true;
            if (extendParent.inheritAnnotations()) {
                distributedAnnotations.putAll(parentInfo.getAnnotations());
            }
        }
        if (subGroup != null) {
            classHasMainCommand = isSubClass && clazz.getDeclaringClass().isAnnotationPresent(SlashCommand.class);
            if (!classHasMainCommand && subGroup.name().isEmpty()) {
                throw new IllegalArgumentException("Class " + clazz.getName() + " has a SubGroup annotation, but isn't a subclass of a parent annotation, and doesn't declare a parent command.");
            }
            if (classHasMainCommand)
                parentInfo = commandMap.get(clazz.getDeclaringClass().getDeclaredAnnotation(SlashCommand.class).name().toLowerCase());
            else parentInfo = commandMap.get(subGroup.parent().toLowerCase());
            if (parentInfo == null) {
                registerLast.add(clazz);
                return this;
            }
            parent = parentInfo.getAnnotation();
            if (subGroup.inheritAnnotations()) {
                distributedAnnotations.putAll(parentInfo.getAnnotations());
            }
        }
        // annotations on the class itself take precedence over inherited annotations
        for (Annotation annotation : clazz.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(DistributeOnMethods.class)) {
                distributedAnnotations.put(annotation.annotationType(), annotation);
            }
        }
        if (classHasMainCommand && extendParent == null && subGroup == null) { // register root command
            boolean subCommandsFound = false;
            for (Method declaredMethod : clazz.getDeclaredMethods()) {
                if (declaredMethod.isAnnotationPresent(SlashCommand.class)) {
                    subCommandsFound = true;
                    break;
                }
            }
            if (!subCommandsFound) {
                throw new IllegalArgumentException("Class " + clazz.getName() + " has a SlashCommand/ExtendParent parent annotation, but no methods with SlashCommand annotations found.");
            }
            parent = clazz.getAnnotation(SlashCommand.class);
            Map<Class<? extends Annotation>, Annotation> annotations = new HashMap<>();
            for (Annotation declaredAnnotation : clazz.getDeclaredAnnotations()) {
                annotations.put(declaredAnnotation.annotationType(), declaredAnnotation);
            }
            annotations.putAll(distributedAnnotations);
            parentInfo = commandMap.get(parent.name().toLowerCase());
            if (parentInfo == null) {
                parentInfo = new CommandInfo(parent, clazz);
            }
            parentInfo.setAnnotations(annotations);
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(SlashCommand.class)) {
                SlashCommand slashCommand = method.getAnnotation(SlashCommand.class);
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
                CommandInfo commandInfo = new CommandInfo(slashCommand, clazz);
                commandInfo.setAnnotations(annotations);
                commandInfo.setMethod(method);
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
        for (Class<?> declaredClass : clazz.getDeclaredClasses()) {
            registerCmd(declaredClass);
        }
        return this;
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
        List<SlashCommandData> globalCommands = new ArrayList<>();
        List<CommandInfo> guildSpecific = new ArrayList<>();
        for (CommandInfo commandInfo : commandMap.values()) {
            if (!commandInfo.isRootCommand()) {
                continue;
            }
            if (!settings.isRegisterGlobal() || commandInfo.isAnnotationPresent(LimitToGuilds.class)) {
                guildSpecific.add(commandInfo);
                continue;
            }
            globalCommands.add(commandInfo.generateCommand());
        }
        return cachedGuildCommands = Pair.of(globalCommands.toArray(new SlashCommandData[0]), guildSpecific.toArray(new CommandInfo[0]));
    }

    public void commitCommands(JDA jda) {
        if (settings.isRegisterGlobal()) {
            if (!autoRegisteredGlobal) {
                commitGlobalCommands();
                autoRegisteredGlobal = true;
            }
        } else {
            try {
                jda.awaitReady();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for (Guild guild : jda.getGuilds()) {
                commitGuildCommands(guild);
            }
        }
    }

    public void commitCommands() {
        registerLastCommands();
        if (settings.getShardManager() != null) {
            settings.getShardManager().getShards().forEach(this::commitCommands);
        } else if (settings.getJda() != null) {
            commitCommands(settings.getJda());
        } else {
            throw new IllegalStateException("No shard manager or jda instance found");
        }
    }

    public void commitGuildCommands(Guild guild) { // actually register the commands to discord
        Pair<SlashCommandData[], CommandInfo[]> commands = generateGuildCommands();
        if (commands.getRight().length == 0) {
            return;
        }
        CommandListUpdateAction commandListUpdateAction = guild.updateCommands();
        for (SlashCommandData slashCommandData : commands.getLeft()) {
            commandListUpdateAction = commandListUpdateAction.addCommands(slashCommandData);
        }
        int i = 0;
        for (CommandInfo commandInfo : commands.getRight()) {
            if (!settings.isRegisterGlobal()) {
                commandListUpdateAction = commandListUpdateAction.addCommands(commandInfo.generateCommand());
                i++;
            } else if (commandInfo.isAnnotationPresent(LimitToGuilds.class)) {
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
                i++;
            }
        }
        int finalI = i;
        commandListUpdateAction.queue();
    }

    public void commitGlobalCommands() {
        Pair<SlashCommandData[], CommandInfo[]> commands = generateGuildCommands();
        JDA jda;
        if (settings.getShardManager() != null) {
            jda = settings.getShardManager().getShards().get(0);
        } else if (settings.getJda() != null) {
            jda = settings.getJda();
        } else {
            throw new IllegalStateException("No shard manager or jda instance found");
        }
        CommandListUpdateAction commandListUpdateAction = jda.updateCommands();
        for (CommandInfo globalCommand : commands.getRight()) {
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
