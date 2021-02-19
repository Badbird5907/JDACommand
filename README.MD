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
	    <version>v1.1.1-STABLE</version>
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
        command.registerCommand(new YourCommandClass("Hello", "Responds With \"Hello World\""));
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
# License
MIT License
```
    Copyright (C) 2021  Badbird-5907

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
```
tl;dr: You can use this code as you wish.
Also see [this](https://github.com/Badbird-5907/JDACommand/blob/master/license.md)