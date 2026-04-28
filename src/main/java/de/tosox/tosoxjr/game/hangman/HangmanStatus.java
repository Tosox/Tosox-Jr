package de.tosox.tosoxjr.game.hangman;

import java.awt.*;

public enum HangmanStatus {
	ONGOING("🎯 Hangman", Color.BLUE),
	WIN("🏆 You Won!", Color.GREEN),
	DEFEAT("💀 You Lost!", Color.RED),
	TIMEOUT("⏱️ Timeout", Color.GRAY);

	private final String title;
	private final Color color;

	HangmanStatus(String title, Color color) {
		this.title = title;
		this.color = color;
	}

	public String getTitle() {
		return title;
	}

	public Color getColor() {
		return color;
	}
}
