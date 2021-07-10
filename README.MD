# JDACommand
[![Maven Central](https://img.shields.io/maven-central/v/net.badbird5907/JDACommand.svg?label=Maven%20Central&style=for-the-badge)](https://search.maven.org/search?q=g:%22net.badbird5907%22%20AND%20a:%22JDACommand%22)
[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2FBadbird-5907%2FJDACommand&count_bg=%236EFF00&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=Views+%28as+of+21%2F05%2F06%29&edge_flat=true)](https://hits.seeyoufarm.com)

A simple JDA command library, still a WIP
# Building
1. Clone this repo `git clone https://github.com/Badbird-5907/JDACommand`
2. Open the folder "JDACommand" in cmd
3. Run `mvn clean package` (https://maven.apache.org/)

# Dependency
The library is hosted on the maven central repo so just simply add this:
```xml
<dependency>
    <groupId>net.badbird5907</groupId>
    <artifactId>JDACommand</artifactId>
    <version>2.0.0-REL</version>
</dependency>
```
Or alternatively, if you use gradle:
```groovy
dependencies{
    implementation 'net.badbird5907:JDACommand:2.0.0-REL'
}
```
# Wiki
|  Name  |  Link  |
|---|---|
|  Main Wiki Page  |  [Here](https://github.com/Badbird-5907/JDACommand/wiki)  |
|  Getting Started  |  [Here](https://github.com/Badbird-5907/JDACommand/wiki/Getting-Started)  |
|  More Info About the @Command Annotation  |  [Here](https://github.com/Badbird-5907/JDACommand/wiki/The-@Command-Annotation---In-Depth)  |


# Usage:

## Code Examples:

```java
import net.badbird5907.jdacommand.Command;
import net.badbird5907.jdacommand.CommandBase;
import net.badbird5907.jdacommand.JDACommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

public class YourClass {
    public static void main(String[] args) {
        JDA jda = new JDABuilder.create("token").build;
        JDACommand command = new JDACommand("prefix", jda);
        command.registerCommandsInPackage("your.package.commands");
    }
}

//in package your.package.commands
public class YourCommandClass extends CommandBase {
    @Command(name = "hello",description = "Responds with \"Hello World!\"")
    protected void execute(String[] args, CommandEvent event, User author, Member member, Guild guild, MessageChannel channel) {
        channel.sendMessage("Hello World!").queue();
    }
}
public class YourCommandClass1 extends CommandBase {
    @Command(name = "hello1",description = "Responds with \"Hello World!\"")
    protected void execute(String[] args, CommandEvent event, User author, Member member, Guild guild, MessageChannel channel) {
        channel.sendMessage("Hello World! (1)").queue();
    }
}
public class YourCommandClass2 extends CommandBase {
    @Command(name = "hello2",description = "Responds with \"Hello World!\"")
    protected void execute(String[] args, CommandEvent event, User author, Member member, Guild guild, MessageChannel channel) {
        channel.sendMessage("Hello World! (2)").queue();
    }
}
```
### OR:

You can do this:
```java
import net.badbird5907.jdacommand.Command;
import net.badbird5907.jdacommand.CommandBase;
import net.badbird5907.jdacommand.JDACommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

public class YourClass {
    public static void main(String[] args) {
        JDA jda = new JDABuilder.create("token").build;
        JDACommand command = new JDACommand("prefix", jda);
        command.registerCommand(new YourCommandClass());
    }
}

public class YourCommandClass extends CommandBase {
    @Command(name = "hello",description = "Responds with \"Hello World!\"")
    protected void execute(String[] args, CommandEvent event, User author, Member member, Guild guild, MessageChannel channel) {
        channel.sendMessage("Hello World!").queue();
    }
}
```


# License
GNU General Public License
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
