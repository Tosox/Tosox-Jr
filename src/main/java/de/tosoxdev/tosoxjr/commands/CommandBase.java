package de.tosoxdev.tosoxjr.commands;

import de.tosoxdev.tosoxjr.GenericCommandBase;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class CommandBase extends GenericCommandBase<MessageReceivedEvent> {
    public CommandBase(String name, String description) {
        super(name, description);
    }
}
