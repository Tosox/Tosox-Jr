package de.tosoxdev.tosoxjr.commands.say;

import de.tosoxdev.tosoxjr.commands.CommandBase;
import de.tosoxdev.tosoxjr.utils.ArgumentParser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class SayCmd extends CommandBase {
    public SayCmd() {
        super("say", "Repeats the given message", List.of(
                new OptionData(OptionType.STRING, "message", "The contents of the message", true)
        ));
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String sayMsg = ArgumentParser.getStringForced(event.getOption("message"));
        event.deferReply().queue(m -> m.deleteOriginal().queue());
        event.getChannel().sendMessage(sayMsg).queue();
    }
}
