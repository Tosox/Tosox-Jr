package de.tosoxdev.tosoxjr.commands;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public abstract class GameBase extends CommandBase {
    public GameBase(String name, String description, List<OptionData> options) {
        super(name, description, options);
    }

    public abstract void handleEvent(Event event);
}
