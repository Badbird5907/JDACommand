package net.badbird5907.jdacommand;

import com.google.common.eventbus.EventBus;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.badbird5907.jdacommand.events.CommandEvent;
import net.badbird5907.jdacommand.handler.MessageHandler;
import net.badbird5907.jdacommand.handler.MessageListener;
import net.badbird5907.jdacommand.handler.SimpleMessageHandler;
import net.badbird5907.jdacommand.util.Cooldown;
import net.badbird5907.jdacommand.util.object.Triplet;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.out;

public class JDACommand {
	@Getter
	private static Map<String, Triplet<Command, Method,Object>> commandMap = new ConcurrentHashMap<>();
	@Getter
	@Deprecated
	private static Map<CommandResult,Object> overrideCommandResult = new ConcurrentHashMap<>();
	private static JDACommand instance;
	public String prefix;
	public JDA jda;
	@Getter
	private static EventBus eventBus = new EventBus();

	@Getter
	private Set<Long> owners = new HashSet<>();
	private List<Object> alreadyInit = new ArrayList<>();

	@Getter
	@Setter
	private MessageHandler messageHandler = new SimpleMessageHandler();

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
	 * <b>Use the new {@link MessageHandler} or {@link SimpleMessageHandler}</b>
	 *
	 * override the message sent for every {@link CommandResult}
	 * for example: {@link CommandResult#ERROR} would usually return "There was an error processing the command!"
	 * you can override the message and send a string, or an {@link MessageEmbed}
	 * @param commandResult
	 * @param message
	 * @return
	 */
	@Deprecated
	public JDACommand overrideCommandResultMessage(CommandResult commandResult,String message){
		overrideCommandResult.remove(commandResult);
		overrideCommandResult.put(commandResult,message);
		return this;
	}
	/**
	 * <b>Use the new {@link MessageHandler} or {@link SimpleMessageHandler}</b>
	 * override the message sent for every {@link CommandResult}
	 * for example: {@link CommandResult#ERROR} would usually return "There was an error processing the command!"
	 * you can override the message and send a string, or an {@link MessageEmbed}
	 * @param commandResult
	 * @param message
	 * @return
	 */
	@Deprecated
	public JDACommand overrideCommandResultMessage(CommandResult commandResult, MessageEmbed message){
		overrideCommandResult.remove(commandResult);
		overrideCommandResult.put(commandResult,message);
		return this;
	}

	/**
	 * <b>Use the new {@link MessageHandler} or {@link SimpleMessageHandler}</b>
	 * unset a command result override {@link JDACommand#overrideCommandResultMessage)}
	 * @param commandResult
	 * @return
	 */
	@Deprecated
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
				if (m.getParameterTypes().length != 6 && m.getParameterTypes()[0] != String[].class || m.getParameterTypes()[1] != CommandEvent.class
						|| m.getParameterTypes()[2] != User.class
				|| m.getParameterTypes()[3] != Member.class	|| m.getParameterTypes()[4] != Guild.class || m.getParameterTypes()[5] != MessageChannel.class) {
					throw new IllegalArgumentException("Invalid method arguments for " + m.getName());
				}
				registerCommand(command, command.name(), m, o);
				if (command.cooldown() > 0){
					Cooldown.createCooldown(command.name().toLowerCase());
				}
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
		Set<Class<? extends CommandBase>> classes = reflections.getSubTypesOf(CommandBase.class);
		for (Class<? extends CommandBase> aClass : classes) {
			aClass.getDeclaredConstructor().newInstance();
		}
	}
	private void registerCommand(Command command, String name,Method method,Object o) {
		if (command.disable())
			return;
		commandMap.put(name.toLowerCase(),new Triplet<>(command,method, o));
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