package com.github.eoniz.nexus.discord.events.select;

import com.github.eoniz.nexus.discord.commands.CommandsManager;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SelectListener extends ListenerAdapter {
    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        String[] splitted = event.getComponentId().split("\\|");
        String command = splitted[0];
        String[] args = Arrays.asList(splitted)
                .subList(1, splitted.length)
                .toArray(new String[]{});

        CommandsManager.handleSelectInteraction(event, command, args);
    }
}
