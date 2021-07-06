package net.badbird5907.jdacommand;

import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.javatuples.Triplet;
import org.reflections.Reflections;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.lang.System.out;

public class JDACommand {
	@Getter
	private static Map<String, Triplet<Command, Method,Object>> commandMap = new ConcurrentHashMap<>();
	@Getter
	private static JDACommand instance;
	public String prefix;
	public JDA jda;
	@Getter
	private List<Long> owners = new ArrayList<>();
	private List<Object> alreadyInit = new ArrayList<>();

	public JDACommand(String prefix, JDA jda) {
		this.prefix = prefix;
		this.jda = jda;
		init();
	}

	public JDACommand(String prefix, JDA jda,Long[] owners) {
		this.prefix = prefix;
		this.jda = jda;
		for (Long owner : owners) {
			this.owners.add(owner);
		}
		init();
	}

	public static JDACommand getInstance() {
		return instance;
	}

	public void init() {
		instance = this;
		jda.addEventListener(new MessageListener());
	}

	public void debugPrintCommands() {
		commandMap.forEach((name,pair)->{
			out.println("Command: Name: " + name + " Annotation:" + pair.getValue0() + " Method: " + pair.getValue1());
		});
	}

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
	}
	public void addOwner(long l){
		owners.add(l);
	}
	public boolean isOwner(User user){
		return owners.contains(user.getIdLong());
	}
}