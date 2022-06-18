package com.github.eoniz.nexus.discord.events.message;

import com.github.eoniz.nexus.discord.commands.CommandsManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        Message message = event.getMessage();

        if (!message.getContentRaw().startsWith("!")) {
            return;
        }

        log.info(
                String.format(
                        "%s (%s) - %s (%s) - %s (%s): %s\n",
                        event.getGuild().getName(),
                        event.getGuild().getId(),
                        event.getTextChannel().getName(),
                        event.getTextChannel().getId(),
                        Objects.requireNonNull(event.getMember()).getEffectiveName(),
                        Objects.requireNonNull(event.getMember()).getId(),
                        event.getMessage().getContentDisplay()
                )
        );

        String[] splittedMsg = message.getContentRaw().split(" ");
        String[] args = Arrays.copyOfRange(splittedMsg, 1, splittedMsg.length);

//        CommandsManager.handleCommand(splittedMsg[0].substring(1), event, args);
    }
}
