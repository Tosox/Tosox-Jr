package de.tosoxdev.tosoxjr.commands.cat;

import de.tosoxdev.tosoxjr.commands.ICommand;
import de.tosoxdev.tosoxjr.utils.APIRequest;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import java.util.List;

public class CatCmd implements ICommand {
    private static final String CAT_API = "https://api.thecatapi.com/v1/images/search";

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        JSONObject response = APIRequest.getJson(CAT_API);
        if (response == null) {
            event.getChannel().sendMessage("Unable to get a cat image :(").queue();
            return;
        }

        String url = response.getString("url");
        event.getChannel().sendMessage(url).queue();
    }

    @Override
    public String getName() {
        return "cat";
    }

    @Override
    public String getHelp() {
        return "Get a random picture of a cat";
    }
}
