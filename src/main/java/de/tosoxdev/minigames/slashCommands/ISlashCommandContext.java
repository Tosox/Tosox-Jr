package de.tosoxdev.minigames.slashCommands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.sharding.ShardManager;

public interface ISlashCommandContext {
    default Guild getGuild() {
        return this.event().getGuild();
    }

    SlashCommandInteractionEvent event();

    default TextChannel getChannel() {
        return (TextChannel) this.event().getChannel();
    }

    default String getMessage() {
        return this.event().getName();
    }

    default User getAuthor() {
        return this.event().getUser();
    }

    default Member getMember() {
        return this.event().getMember();
    }

    default JDA getJDA() {
        return this.event().getJDA();
    }

    default ShardManager getShardManager() {
        return this.getJDA().getShardManager();
    }

    default User getSelfUser() {
        return this.getJDA().getSelfUser();
    }

    default Member getSelfMember() {
        return this.getGuild().getSelfMember();
    }
}
