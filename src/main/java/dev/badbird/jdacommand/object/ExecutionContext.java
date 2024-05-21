package dev.badbird.jdacommand.object;

import dev.badbird.jdacommand.annotation.CommandButton;
import dev.badbird.jdacommand.inject.parameter.impl.CommandParameterWrapper;
import dev.badbird.jdacommand.object.command.ExecutableCommand;
import dev.badbird.jdacommand.object.command.impl.CommandInfo;
import dev.badbird.jdacommand.session.ExecutionSession;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateRequest;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

@Getter
@Setter
public class ExecutionContext {
    private final Member member;
    private final SlashCommandInteractionEvent event;
    private final MessageChannel channel;
    private final Guild guild;
    private final ExecutableCommand executable;

    private Map<String, Method> buttons = new HashMap<>();

    private ExecutionSession session;

    public ExecutionContext(SlashCommandInteractionEvent event, ExecutableCommand executable) {
        this.event = event;
        this.member = event.getMember();
        this.channel = event.getChannel();
        this.guild = event.getGuild();
        this.executable = executable;

        for (Method declaredMethod : getCommandInfo().getClazz().getDeclaredMethods()) {
            if (declaredMethod.isAnnotationPresent(CommandButton.class)) {
                CommandButton button = declaredMethod.getAnnotation(CommandButton.class);
                buttons.put(button.id(), declaredMethod);
            }
        }
    }

    public CommandInfo getCommandInfo() {
        if (executable instanceof CommandInfo) {
            return (CommandInfo) executable;
        } else throw new IllegalStateException("executable is not an instance of CommandInfo!");
    }

    public String getCommandName() {
        return getCommandInfo().getName();
    }

    public OptionMapping getOption(String name) {
        return event.getOption(name);
    }

    public Optional<OptionMapping> get(String name) {
        return Optional.ofNullable(event.getOption(name));
    }

    public boolean hasOption(String name) {
        return event.getOption(name) != null;
    }


    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private List<List<ItemComponent>> actionRows = new ArrayList<>();

    public ExecutionContext actionRow(ItemComponent... components) {
        getActionRows().add(Arrays.asList(components));
        return this;
    }

    public ExecutionContext button(Button... buttons) {
        return actionRow(buttons);
    }

    private static final Pattern FORMATTED_EMOJI_PATTERN = Pattern.compile("<a?:\\w{2,32}:(\\d{17,19})>");

    public ExecutionContext buttons(String... ids) {
        List<Button> buttons = new ArrayList<>();
        for (String id : ids) {
            buttons.add(resolveButton(id));
        }
        return actionRow(buttons.toArray(new Button[0]));
    }

    private Button resolveButton(String id) {
        String buttonId = "jdacmd:" + session.getSessionId() + ":" + id;
        CommandButton cmdBtn = buttons.get(id).getAnnotation(CommandButton.class);
        Button button = Button.of(cmdBtn.style(), buttonId, cmdBtn.label());
        if (!cmdBtn.emoji().isEmpty()) {
            String str = cmdBtn.emoji();
            Emoji emoji;
            if (FORMATTED_EMOJI_PATTERN.matcher(str).matches()) {
                emoji = Emoji.fromFormatted(str);
            } else {
                emoji = Emoji.fromUnicode(str);
            }
            button = button.withEmoji(emoji);
        }
        return button;
    }

    private <T extends MessageCreateRequest<?>> T applyActionRows(T action) {
        for (List<ItemComponent> actionRow : getActionRows()) {
            //noinspection unchecked
            action = (T) action.addActionRow(actionRow);
        }
        return action;
    }

    public void reply(String message) {
        reply(message, false);
    }

    public void reply(String message, boolean ephemeral) {
        if (event.isAcknowledged())
            applyActionRows(event.getHook().sendMessage(message)).setEphemeral(ephemeral).queue();
        else applyActionRows(event.reply(message)).setEphemeral(ephemeral).queue();
    }

    public void reply(MessageEmbed embed, MessageEmbed... embeds) {
        if (event.isAcknowledged())
            applyActionRows(event.getHook().sendMessageEmbeds(embed, embeds)).queue();
        else applyActionRows(event.replyEmbeds(embed, embeds)).queue();
    }

    public void replyEphemeralEmbeds(MessageEmbed embed, MessageEmbed... embeds) {
        if (event.isAcknowledged())
            applyActionRows(event.getHook().sendMessageEmbeds(embed, embeds)).setEphemeral(true).queue();
        else applyActionRows(event.replyEmbeds(embed, embeds).setEphemeral(true)).queue();
    }

    public void setOriginal(String message) {
        WebhookMessageEditAction<Message> action = event.getHook().editOriginal(message);
        if (!getActionRows().isEmpty()) {
            for (List<ItemComponent> actionRow : getActionRows()) {
                action.setActionRow(actionRow); // TODO: make sure this works
            }
        }
        action.queue();
    }

    public void setOriginal(MessageEmbed... embeds) {
        WebhookMessageEditAction<Message> action = event.getHook().editOriginalEmbeds(embeds);
        if (!getActionRows().isEmpty()) {
            for (List<ItemComponent> actionRow : getActionRows()) {
                action.setActionRow(actionRow); // TODO: make sure this works
            }
        }
        action.queue();
    }

    public void deferReply() {
        event.deferReply().queue();
    }

    public User getUser() {
        return event.getUser();
    }

    public Member getMember() {
        return event.getMember();
    }

    public static class Provider implements dev.badbird.jdacommand.provider.Provider<ExecutionContext> {

        @Override
        public ExecutionContext provide(ExecutionContext context, CommandParameterWrapper commandParameterContext, CommandInfo commandInfo, ParameterInfo parameterInfo) {
            return context;
        }

        @Override
        public OptionType getOptionType(ParameterInfo parameter) {
            return null;
        }
    }
}
