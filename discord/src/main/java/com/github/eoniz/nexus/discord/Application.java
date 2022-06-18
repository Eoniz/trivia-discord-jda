package com.github.eoniz.nexus.discord;

import com.github.eoniz.nexus.discord.annotations.SlashCommand;
import com.github.eoniz.nexus.discord.annotations.SlashCommandOptions;
import com.github.eoniz.nexus.discord.commands.CommandsManager;
import com.github.eoniz.nexus.discord.config.PropertiesLoader;
import com.github.eoniz.nexus.discord.events.message.MessageListener;
import com.github.eoniz.nexus.discord.events.ready.ReadyListener;
import com.github.eoniz.nexus.discord.events.slash.SlashListener;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;

import java.util.Arrays;

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

        CommandsManager.getSlashCommands()
                .values()
                .forEach((slashCommands) -> {
                    SlashCommand annotation = slashCommands
                            .getClass()
                            .getAnnotation(SlashCommand.class);
                    SlashCommandOptions slashCommandOptions = slashCommands
                            .getClass()
                            .getAnnotation(SlashCommandOptions.class);

                    CommandCreateAction commandCreateAction = jda.upsertCommand(annotation.name(), annotation.help());
                    if (slashCommandOptions != null) {
                        Arrays.stream(slashCommandOptions.value())
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
                    }

                    commandCreateAction.queue();
                });


        jda.awaitReady();
    }

}
