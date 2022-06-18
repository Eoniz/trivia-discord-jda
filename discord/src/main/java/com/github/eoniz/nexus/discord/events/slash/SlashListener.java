package com.github.eoniz.nexus.discord.events.slash;

import com.github.eoniz.nexus.discord.commands.CommandsManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
public class SlashListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        log.info(
                String.format(
                        "%s (%s) - %s (%s) - %s (%s): %s\n",
                        event.getGuild().getName(),
                        event.getGuild().getId(),
                        event.getTextChannel().getName(),
                        event.getTextChannel().getId(),
                        Objects.requireNonNull(event.getMember()).getEffectiveName(),
                        Objects.requireNonNull(event.getMember()).getId(),
                        event.getName()
                )
        );

        CommandsManager.handleCommand(event.getName(), event);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String[] splitted = event.getComponentId().split("\\|");
        String command = splitted[0];
        String[] args = Arrays.asList(splitted)
                .subList(1, splitted.length)
                .toArray(new String[]{});

        CommandsManager.handleButtonInteraction(event, command, args);
    }
}
