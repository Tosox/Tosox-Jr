package de.tosoxdev.minigames.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public record CommandContext(MessageReceivedEvent event, List<String> args) { }
