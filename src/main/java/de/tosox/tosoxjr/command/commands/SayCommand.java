package de.tosox.tosoxjr.command.commands;

import de.tosox.tosoxjr.command.CommandBase;
import de.tosox.tosoxjr.util.ArgumentParser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class SayCommand extends CommandBase {
    public SayCommand() {
        super("say", "Repeats the given message", List.of(
                new OptionData(OptionType.STRING, "message", "The contents of the message", true)
        ));
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String sayMsg = ArgumentParser.getString(event.getOption("message"), "");
        event.deferReply(true).queue(hook -> {
            hook.deleteOriginal().queue();
            event.getChannel().sendMessage(sayMsg).queue();
        });
    }
}
