package com.github.eoniz.nexus.discord;

import com.github.eoniz.nexus.discord.annotations.SlashCommand;
import com.github.eoniz.nexus.discord.annotations.SlashCommandOption;
import com.github.eoniz.nexus.discord.annotations.SlashCommandOptions;
import com.github.eoniz.nexus.discord.commands.CommandsManager;
import com.github.eoniz.nexus.discord.config.PropertiesLoader;
import com.github.eoniz.nexus.discord.events.message.MessageListener;
import com.github.eoniz.nexus.discord.events.reaction.ReactionListener;
import com.github.eoniz.nexus.discord.events.ready.ReadyListener;
import com.github.eoniz.nexus.discord.events.select.SelectListener;
import com.github.eoniz.nexus.discord.events.slash.SlashListener;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Application {

    @SneakyThrows
    public static void main(String[] args) {
        String token = PropertiesLoader.getInstance().getProperty("discord.token");
        JDA jda = JDABuilder
                .createLight(token)
                .setActivity(Activity.playing("Type !ping"))
                .build();

        jda.addEventListener(new ReadyListener());
        jda.addEventListener(new MessageListener());
        jda.addEventListener(new SlashListener());
        jda.addEventListener(new ReactionListener());
        jda.addEventListener(new SelectListener());

        CommandsManager.getSlashCommands()
                .values()
                .forEach((slashCommands) -> {
                    SlashCommand annotation = slashCommands
                            .getClass()
                            .getAnnotation(SlashCommand.class);
                    SlashCommandOptions slashCommandOptionsFound = slashCommands
                            .getClass()
                            .getAnnotation(SlashCommandOptions.class);

                    SlashCommandOption slashCommandOptionFound = slashCommands
                            .getClass()
                            .getAnnotation(SlashCommandOption.class);

                    List<SlashCommandOption> slashCommandOptions = new ArrayList<>();
                    if (slashCommandOptionFound != null) {
                        slashCommandOptions.add(slashCommandOptionFound);
                    }
                    if (slashCommandOptionsFound != null) {
                        slashCommandOptions.addAll(List.of(slashCommandOptionsFound.value()));
                    }

                    CommandCreateAction commandCreateAction = jda.upsertCommand(annotation.name(), annotation.help());
                    slashCommandOptions.stream()
                            .sorted(((o1, o2) -> Boolean.compare(o2.required(), o1.required())))
                            .forEach(option -> {
                                commandCreateAction.addOption(
                                        option.optionType(),
                                        option.name(),
                                        option.description(),
                                        option.required(),
                                        option.autoComplete()
                                ).queue();
                            });

                    commandCreateAction.queue();
                });


        jda.awaitReady();
    }

}
