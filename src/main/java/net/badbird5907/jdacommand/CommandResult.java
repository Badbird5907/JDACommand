package net.badbird5907.jdacommand;

import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;

@Getter
public enum CommandResult {
    ERROR("There was an error processing the command!"),
    SUCCESS(""),
    NO_PERMS("You do not have sufficient permissions."),
    OTHER("");
    CommandResult(String message){
        this.message =  message;
    }
    private String message = null;
}
