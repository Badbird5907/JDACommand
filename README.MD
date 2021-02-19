# JDACommand
[![](https://jitpack.io/v/Badbird-5907/JDACommand.svg)](https://jitpack.io/#Badbird-5907/JDACommand) <br>
[![HitCount](http://hits.dwyl.com/Badbird-5907/JDACommand.svg)](http://hits.dwyl.com/Badbird-5907/JDACommand) <br>
A simple JDA command library, still a WIP
# Building
1. Clone this repo `git clone https://github.com/Badbird-5907/JDACommand`
2. Open the folder "JDACommand" in cmd
3. Run `mvn clean package` (https://maven.apache.org/)

# Maven Dependency
```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
```xml
	<dependency>
	    <groupId>com.github.Badbird-5907</groupId>
	    <artifactId>JDACommand</artifactId>
	    <version>v1.1-STABLE</version>
	</dependency>
```

# Usage:

## Code Examples:

```java
import net.badbird5907.jdacommand.Command;
import net.badbird5907.jdacommand.JDACommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

public class YourClass {
    public static void main(String[] args) {
        JDA jda = new JDABuilder.create("token").build;
        JDACommand command = new JDACommand("prefix", jda);
        command.init();
        command.registerCommand(new YourCommandClass("Hello World", "Responds With \"Hello World\""));
    }
}

public class YourCommandClass extends Command {
    public YourCommandClass(String name, String description) {
        super(name, description);
    }

    @Override
    protected void execute(String[] args, CommandEvent event, User author, Guild guild, MessageChannel channel) {
        channel.sendMessage("Hello World").queue();
    }
}
```