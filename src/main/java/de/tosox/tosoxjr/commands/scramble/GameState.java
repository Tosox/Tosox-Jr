package de.tosox.tosoxjr.commands.scramble;

enum GameState {
    ONGOING("Scramble"),
    WIN("You won!"),
    DEFEAT("You lose!"),
    TIMEOUT("Timeout");

    private final String title;

    GameState(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
