package com.github.eoniz.nexus.discord.commands.ping;

import com.github.eoniz.nexus.discord.annotations.SlashCommand;
import com.github.eoniz.nexus.discord.commands.AbstractSlashCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommand(name = "ping", help = "Pong !")
public class Ping extends AbstractSlashCommand {

    @Override
    public void handleCommand(Member member, TextChannel textChannel, SlashCommandInteractionEvent event) {
        long time = System.currentTimeMillis();
        event.reply("Pong")
                .setEphemeral(true)
                .flatMap(v -> (
                        event.getHook()
                                .editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time)
                ))
                .queue();
    }
}
