# Tosox-Jr

A personal Discord bot built with JDA 5.

## Building

```bash
mvn package
```

This produces `target/tosox-jr.jar` as a shaded (fat) jar.

## Running

```bash
java -jar target/tosox-jr.jar
```

## Bot Setup

When inviting the bot to a server, the following permissions and scopes are required:

**Scopes:** `bot`, `applications.commands`

**Permissions:** Send Messages, Manage Messages

## Commands

### `/cat`
Sends a random cat image.

### `/cs-stats <user> [stat]`
Fetches CS2 statistics for a Steam player.
- `user` — SteamID64 or vanity URL
- `stat` *(optional)* — name of a specific statistic (e.g. `total_kills`)

### `/hangman [lang] [coop]`
Starts a game of Hangman.
- `lang` — language of the word (`en`, `de`). Use `list` to see all options. Defaults to `en`.
- `coop` — if `true`, anyone on the server can guess letters.

React with 🇦–🇿 to guess a letter, 🃏 to reveal a hint (owner only), 🛑 to end the game.

### `/joke [category]`
Tells a random joke.
- `category` — `chuck-norris`, `programming`, `pun`. Use `list` to see all options.

### `/quote [category]`
Shows a random quote.
- `category` — `breaking-bad`, `inspirational`, `motivational`. Use `list` to see all options.

### `/say <message>`
Makes the bot repeat a message.

### `/scramble [lang] [coop]`
Starts a game of Scramble — unscramble the shuffled word.
- `lang` — language of the word (`en`, `de`). Use `list` to see all options. Defaults to `en`.
- `coop` — if `true`, anyone on the server can submit answers.

Type your answer in chat to guess, or react with 🛑 to end the game.

## Dependencies

| Library | Purpose |
|---|---|
| [JDA 5](https://github.com/discord-jda/JDA) | Discord API wrapper |
| [OkHttp](https://square.github.io/okhttp/) | HTTP client |
| [org.json](https://github.com/stleary/JSON-java) | JSON parsing |
| [Logback](https://logback.qos.ch/) | Logging |
