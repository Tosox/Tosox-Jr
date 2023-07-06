package de.tosoxdev.tosoxjr.commands.scramble;

enum GameState {
    ONGOING("Scramble"),
    WIN("You won!"),
    TIMEOUT("Timeout");

    private final String title;

    GameState(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
