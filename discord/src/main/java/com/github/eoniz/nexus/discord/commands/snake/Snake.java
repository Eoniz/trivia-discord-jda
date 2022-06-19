package com.github.eoniz.nexus.discord.commands.snake;

import com.github.eoniz.nexus.core.snake.service.SnakeGameService;
import com.github.eoniz.nexus.discord.annotations.SlashCommand;
import com.github.eoniz.nexus.discord.commands.AbstractSlashCommand;
import com.github.eoniz.nexus.model.snake.player.SnakePlayer;
import com.github.eoniz.nexus.model.snake.room.SnakeRoom;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Slf4j
@SlashCommand(name = "snake", help = "Démarrer une partie de Snake")
public class Snake extends AbstractSlashCommand {

    private final SnakeGameService snakeGameService = new SnakeGameService();

    @Override
    public void handleCommand(
            @NotNull Member member,
            @NotNull TextChannel textChannel,
            @NotNull SlashCommandInteractionEvent event
    ) {
        SnakePlayer snakePlayer = toSnakePlayer(event.getMember());
        SnakeRoom room = snakeGameService.createRoom(snakePlayer);

        StringBuilder stringBuilder = new StringBuilder();
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                SnakeRoom.EntityType entityType = room.getEntityType(x, y);

                if (entityType == SnakeRoom.EntityType.APPLE) {
                    stringBuilder.append(":apple:");
                } else if (entityType == SnakeRoom.EntityType.SNAKE_HEAD) {
                    stringBuilder.append(":orange_square:");
                } else if (entityType == SnakeRoom.EntityType.SNAKE) {
                    stringBuilder.append(":yellow_square:");
                } else {
                    stringBuilder.append(":blue_square:");
                }
            }
            stringBuilder.append("\n");
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription(stringBuilder.toString());

        event.reply("La partie va commencer")
                .setEphemeral(true)
                .queue();

        textChannel.sendMessageEmbeds(embedBuilder.build())
                .queue(embed -> {
                    room.setMessageId(embed.getId());
                    embed.addReaction("⬅️").queue();
                    embed.addReaction("⬆️").queue();
                    embed.addReaction("⬇️").queue();
                    embed.addReaction("➡️").queue();

                    snakeGameService.save(room);
                });
    }

    private SnakePlayer toSnakePlayer(@Nullable Member member) {
        if (member == null) {
            return SnakePlayer.builder()
                    .id("?")
                    .effectiveName("?")
                    .build();
        }

        return SnakePlayer.builder()
                .id(member.getId())
                .effectiveName(member.getEffectiveName())
                .build();
    }
}
