package dev.badbird.jdacommand.object;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class ButtonExecutionContext extends ExecutionContext {
    private final ButtonInteractionEvent btnEvent;

    public ButtonExecutionContext(ExecutionContext ctx, ButtonInteractionEvent btnEvent) {
        super(ctx.getEvent(), ctx.getExecutable());
        this.btnEvent = btnEvent;
    }

    @Override
    public void finishAck() {
        btnEvent.deferEdit().queue();
    }
}
