package net.badbird5907.jdacommand;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import org.javatuples.Pair;
import org.javatuples.Tuple;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.out;

public class JDACommand {
	@Getter
	private static Map<String, Pair<Command,Method>> commandMap = new ConcurrentHashMap<>();
	@Getter
	private static JDACommand instance;
	public String prefix;
	public JDA jda;
	private List<Long> owners = new ArrayList<>();

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
		for (Method m : o.getClass().getMethods()){
			if (m.getAnnotation(Command.class) != null) {
				Command command = m.getAnnotation(Command.class);
				//String[] args, CommandEvent event, User author, Member member, Guild guild, MessageChannel channel
				if (m.getParameterTypes().length != 6 && m.getParameterTypes()[0] != String[].class || m.getParameterTypes()[1] != CommandEvent.class || m.getParameterTypes()[2] != User.class
				|| m.getParameterTypes()[3] != Member.class	|| m.getParameterTypes()[4] != Guild.class || m.getParameterTypes()[5] != MessageChannel.class) {
					throw new IllegalArgumentException("Invalid method arguments for " + m.getName());
				}
				registerCommand(command, command.name(), m);
				for (String alias : command.aliases()) {
					registerCommand(command, alias, m);
				}
			}
		}
	}
	public void registerCommandsInPackage(String p){
		List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		classLoadersList.add(ClasspathHelper.staticClassLoader());

		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
				.setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
				.filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(p))));
		Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);
		for (Class<?> aClass : classes) {
			registerCommand(aClass);
		}
	}
	private void registerCommand(Command command, String name,Method method){
		if (command.disable())
			return;
		commandMap.put(name,new Pair<>(command,method));
	}
	public boolean isOwner(User user){
		return owners.contains(user.getIdLong());
	}
}