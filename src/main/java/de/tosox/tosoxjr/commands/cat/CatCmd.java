package de.tosox.tosoxjr.commands.cat;

import de.tosox.tosoxjr.commands.CommandBase;
import de.tosox.tosoxjr.utils.APIRequest;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.JSONArray;

public class CatCmd extends CommandBase {
    private static final String CAT_API = "https://api.thecatapi.com/v1/images/search";

    public CatCmd() {
        super("cat", "Get a random picture of a cat", null);
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
