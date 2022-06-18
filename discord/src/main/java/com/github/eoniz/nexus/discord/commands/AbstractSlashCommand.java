package com.github.eoniz.nexus.discord.commands;

import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@NoArgsConstructor
public abstract class AbstractSlashCommand {
    public abstract void handleCommand(
            Member member,
            TextChannel textChannel,
            SlashCommandInteractionEvent event
    );
}
