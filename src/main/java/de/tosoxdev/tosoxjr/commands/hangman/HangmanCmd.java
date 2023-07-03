package de.tosoxdev.tosoxjr.commands.hangman;

import de.tosoxdev.tosoxjr.commands.CommandBase;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class HangmanCmd extends CommandBase {
    public HangmanCmd() {
        super("hangman", "Play a game of hangman", null);
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {

    }
}
