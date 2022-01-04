package net.badbird5907.jdacommand.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

@Getter
@RequiredArgsConstructor
public class CommandContext {
    private final Member member;


}
