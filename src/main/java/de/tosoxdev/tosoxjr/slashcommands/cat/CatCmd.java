package de.tosoxdev.tosoxjr.slashcommands.cat;

import de.tosoxdev.tosoxjr.slashcommands.SlashCommandBase;
import de.tosoxdev.tosoxjr.utils.APIRequest;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.JSONArray;

public class CatCmd extends SlashCommandBase {
    private static final String CAT_API = "https://api.thecatapi.com/v1/images/search";

    public CatCmd() {
        super("cat", "Get a random picture of a cat");
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        JSONArray response = (JSONArray) APIRequest.getJson(CAT_API);
        if ((response == null) || (response.isEmpty())) {
            event.reply("Unable to get a cat image :(").queue();
            return;
        }

        String url = response.getJSONObject(0).getString("url");
        event.reply(url).queue();
    }
}
