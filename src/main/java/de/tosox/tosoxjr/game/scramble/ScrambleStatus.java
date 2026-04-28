package de.tosox.tosoxjr.game.scramble;

import java.awt.*;

public enum ScrambleStatus {
	ONGOING("🔀 Scramble", Color.CYAN),
	WIN("🏆 You Won!", Color.GREEN),
	DEFEAT("❌ You Lost!", Color.RED),
	TIMEOUT("⏱️ Timeout", Color.GRAY);

	private final String title;
	private final Color color;

	ScrambleStatus(String title, Color color) {
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
