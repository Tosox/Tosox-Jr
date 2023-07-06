package de.tosoxdev.tosoxjr.commands.hangman;

enum GameState {
    ONGOING("Hangman"),
    WIN("You won!"),
    DEFEAT("You lost!"),
    TIMEOUT("Timeout");

    private final String title;

    GameState(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
