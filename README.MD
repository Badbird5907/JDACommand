# JDACommand
[![Maven Central](https://img.shields.io/maven-central/v/net.badbird5907/JDACommand.svg?label=Maven%20Central&style=for-the-badge)](https://search.maven.org/search?q=g:%22net.badbird5907%22%20AND%20a:%22JDACommand%22)
[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2FBadbird-5907%2FJDACommand&count_bg=%236EFF00&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=Views+%28as+of+21%2F05%2F06%29&edge_flat=true)](https://hits.seeyoufarm.com)
[![javadoc](https://javadoc.io/badge2/net.badbird5907/JDACommand/javadoc.svg)](https://javadoc.io/doc/net.badbird5907/JDACommand)

JavaDocs: [here](https://www.javadoc.io/doc/net.badbird5907/JDACommand/latest/index.html)



# Building
1. Clone this repo `git clone https://github.com/Badbird-5907/JDACommand`
2. Open the folder "JDACommand" in cmd
3. Run `mvn clean install` (https://maven.apache.org/)

# Dependency
The library is hosted on the maven central repo so just simply add this:
```xml
<dependency>
    <groupId>net.badbird5907</groupId>
    <artifactId>JDACommand</artifactId>
    <version>4.0.1-REL</version>
</dependency>
```
Or alternatively, if you use gradle:
```groovy
dependencies {
    implementation 'net.badbird5907:JDACommand:4.0.1-REL'
}
```
# Important
To allow command argument names to work correctly, you need to pass `-parameters` to your compiler <br/>
Maven:
```xml
<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>${maven-compiler-plugin.version}</version>
        <configuration>
            <parameters>true</parameters>
        </configuration>
    </plugin>
</plugins>
```
Gradle:
```groovy
compileJava {
    options.compilerArgs << '-parameters'
}
```
# Wiki
|  Name  |  Link  |
|---|---|
|  Main Wiki Page  |  [Here](https://github.com/Badbird5907/JDACommand/wiki)  |
|  Getting Started  |  [Here](https://github.com/Badbird5907/JDACommand/wiki/Getting-Started)  |
|  More Info About the @Command Annotation  |  [Here](https://github.com/Badbird5907/JDACommand/wiki/The-@Command-Annotation---In-Depth)  |


# Usage:
## Code Examples:

Initialization:
```java
public class YourMainClass {
    public static void main(String[] args) {
        JDA jda = JDABuilder.createDefault("token").build();
        JDACommand command = new JDACommand(jda);
        command.registerCommandsInPackage("your.package.commands");
    }
}

```
Commands:
```java
import net.badbird5907.jdacommand.annotation.Command;
import net.badbird5907.jdacommand.annotation.Required;
import net.badbird5907.jdacommand.annotation.Sender;
import net.badbird5907.jdacommand.context.CommandContext;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class ExampleCommand {
    @Command(name = "hello", description = "Replies with \"Hello!\"")
    public void hello(CommandContext ctx, @Sender User sender) {
        ctx.reply("Hello, " + sender.getAsTag() + "!");
    }
    @Command(name = "ban",description = "Ban a member",permission = Permission.BAN_MEMBERS)
    public void ban(CommandContext ctx, @Required Member target, @Required String reason, boolean delmessages) {
        ctx.getGuild().ban(target,delmessages ? 7 : 0,reason).queue();
        ctx.reply("Banned " + target.getUser().getAsMention() + " for `" + reason + "`!");
    }
}

```
### OR:

You can do this:

```java
import net.badbird5907.jdacommand.annotation.Command;
import net.badbird5907.jdacommand.CommandResult;
import net.badbird5907.jdacommand.JDACommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

public class YourClass {
    public static void main(String[] args) {
        JDA jda = JDABuilder.createDefault("token").build();
        JDACommand command = new JDACommand(jda);
        command.registerCommand(new ExampleCommand());
    }
}
```


# License
GNU General Public License
```
Copyright (C) 2022  Badbird-5907

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
Also see [this](/blob/master/license.md)
