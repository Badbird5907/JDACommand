package net.badbird5907.jdacommand;

import com.google.common.eventbus.EventBus;
import lombok.Getter;
import lombok.SneakyThrows;
import net.badbird5907.jdacommand.annotation.Arg;
import net.badbird5907.jdacommand.annotation.Command;
import net.badbird5907.jdacommand.annotation.Disable;
import net.badbird5907.jdacommand.annotation.Sender;
import net.badbird5907.jdacommand.provider.Provider;
import net.badbird5907.jdacommand.util.object.CommandObj;
import net.badbird5907.jdacommand.util.object.Triplet;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.OptionType;
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
	private static Map<String, Triplet<Command, Method,Object>> commandMap = new ConcurrentHashMap<>();
	@Getter
	private static Map<CommandResult,Object> overrideCommandResult = new ConcurrentHashMap<>();

	private static List<Provider> providers = new CopyOnWriteArrayList<>();

	private static JDACommand instance;
	public String prefix;
	public JDA jda;
	@Getter
	private static EventBus eventBus = new EventBus();

	@Getter
	private Set<Long> owners = new HashSet<>();
	private List<Object> alreadyInit = new ArrayList<>();

	/**
	 * Instantiate {@link JDACommand} with just prefix and {@link JDA} instance.
	 * @param prefix Bot prefix
	 * @param jda Pass-through the {@link JDA} instance
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
	 * @param prefix Bot prefix
	 * @param jda Pass-through the {@link JDA} instance
	 * @param owners {@link Long} array of owners
	 */
	public JDACommand(String prefix, JDA jda,Long[] owners) {
		if (instance != null)
			throw new IllegalStateException("Cannot instantiate more than one JDACommand instance.");this.prefix = prefix;
		this.jda = jda;
		Collections.addAll(this.owners, owners);
		init();
	}

	/**
	 * override the message sent for every {@link CommandResult}
	 * for example: {@link CommandResult#ERROR} would usually return "There was an error processing the command!"
	 * you can override the message and send a string, or an {@link MessageEmbed}
	 * @param commandResult
	 * @param message
	 * @return
	 */
	public JDACommand overrideCommandResultMessage(CommandResult commandResult,String message){
		overrideCommandResult.remove(commandResult);
		overrideCommandResult.put(commandResult,message);
		return this;
	}
	/**
	 * override the message sent for every {@link CommandResult}
	 * for example: {@link CommandResult#ERROR} would usually return "There was an error processing the command!"
	 * you can override the message and send a string, or an {@link MessageEmbed}
	 * @param commandResult
	 * @param message
	 * @return
	 */
	public JDACommand overrideCommandResultMessage(CommandResult commandResult, MessageEmbed message){
		overrideCommandResult.remove(commandResult);
		overrideCommandResult.put(commandResult,message);
		return this;
	}

	/**
	 * unset a command result override {@link JDACommand#overrideCommandResultMessage)}
	 * @param commandResult
	 * @return
	 */
	public JDACommand unsetCommandResultOverride(CommandResult commandResult){
		overrideCommandResult.remove(commandResult);
		return this;
	}

	/**
	 * Get the {@link JDACommand instance.}
	 * @return instance
	 */
	public static JDACommand getInstance() {
		return instance;
	}

	private void init() {
		instance = this;
		jda.addEventListener(new MessageListener());
	}

	/**
	 * print all registered commands to console (for debug purposes).
	 */
	public void printAllRegisteredCommands() {
		commandMap.forEach((name,pair)-> out.println("Command: Name: " + name + " Annotation:" + pair.getValue0() + " Method: " + pair.getValue1()));
	}

	/**
	 * Register a command
	 * Example: registerCommand(MyCommand.class);
	 * @param o Class
	 */
	public void registerCommand(Object o) {
		if (alreadyInit.contains(o)) {
			return;
		}
		for (Method m : o.getClass().getDeclaredMethods()){
			if (m.getAnnotation(Command.class) != null && m.getReturnType() == CommandResult.class) {
				Command command = m.getAnnotation(Command.class);
				//String[] args, CommandEvent event, User author, Member member, Guild guild, MessageChannel channel
				if (m.getParameterTypes().length != 6 && m.getParameterTypes()[0] != String[].class || m.getParameterTypes()[1] != CommandEvent.class || m.getParameterTypes()[2] != User.class
				|| m.getParameterTypes()[3] != Member.class	|| m.getParameterTypes()[4] != Guild.class || m.getParameterTypes()[5] != MessageChannel.class) {
					throw new IllegalArgumentException("Invalid method arguments for " + m.getName());
				}
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
	 * @param p Package name, eg: my.package.commands
	 */
	@SneakyThrows
	public void registerCommandsInPackage(String p){
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
					if (Modifier.isPrivate(method.getModifiers()) || Modifier.isProtected(method.getModifiers())){
						if (instance == null) {
							instance = aClass.newInstance();
						}
					}
					CommandObj commandObj = new CommandObj(command.name(), command, command.aliases(),instance);
					registerCommand(command, command.name(), method, instance,commandObj);
				}
			}
		}
	}
	private void registerCommand(Command command, String name,Method method,Object o,CommandObj commandObj) {
		if (command.disable() || method.isAnnotationPresent(Disable.class))
			return;
		if (commandMap.containsKey(name) || name.isEmpty()) {
			return;
		}

		commandMap.put(name.toLowerCase(),new Triplet<>(command,method, o));
		CommandCreateAction action = jda.upsertCommand(command.name(),command.description());
		Parameter[] params = method.getParameters();
		Map<Parameter, OptionType> options = new HashMap<>();
		for (Parameter parameter : params) {
			if (parameter.isAnnotationPresent(Arg.class) || parameter.isAnnotationPresent(Sender.class)) {
				providers.stream().filter(provider ->{
					//check if provider generic type is assignable from parameter type
					return provider.getClass().getGenericInterfaces()[0].getTypeParameters()[0].getTypeName().equals(parameter.getType().getTypeName());
				})
			}
		}

		for (int i = 0; i < params.length; i++) {

		}

		/*
		for (Guild guild : jda.getGuilds()) {
			String desc = (command.description() == null ? "Default Description" : command.description());
			guild.upsertCommand(command.name(),desc);
		}
		*/
	}

	/**
	 * Add a owner
	 * See {@link JDACommand#isOwner(User)}
	 * @param l owner id
	 */
	public void addOwner(long l){
		owners.add(l);
	}

	/**
	 * Returns if a {@link User} is defined as a owner. See {@link JDACommand#addOwner(long)}
	 * @param user user
	 * @return true - if the user is a owner | false - if the user is not a owner.
	 */
	public boolean isOwner(User user){
		return owners.contains(user.getIdLong());
	}
}