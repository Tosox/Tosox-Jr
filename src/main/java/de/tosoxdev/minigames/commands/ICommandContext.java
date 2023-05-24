package de.tosoxdev.minigames.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;

public interface ICommandContext {
    default Guild getGuild() {
        return this.event().getGuild();
    }

    MessageReceivedEvent event();

    default TextChannel getChannel() {
        return (TextChannel) this.event().getChannel();
    }

    default Message getMessage() {
        return this.event().getMessage();
    }

    default User getAuthor() {
        return this.event().getAuthor();
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
