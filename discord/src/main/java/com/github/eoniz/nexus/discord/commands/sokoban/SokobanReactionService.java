package com.github.eoniz.nexus.discord.commands.sokoban;

import com.github.eoniz.nexus.core.sokoban.service.SokobanGameService;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import com.github.eoniz.nexus.model.sokoban.room.SokobanRoom;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SokobanReactionService {

    private final SokobanGameService sokobanGameService = new SokobanGameService();

    public void handleReaction(MessageReactionAddEvent event, SokobanRoom sokobanRoom) {
        String emoji = event.getReaction().getReactionEmote().getEmoji();

        if ("⬆️".equals(emoji)) {
            handleUp(event, sokobanRoom);
        } else if ("⬇️".equals(emoji)) {
            handleDown(event, sokobanRoom);
        } else if ("⬅️".equals(emoji)) {
            handleLeft(event, sokobanRoom);
        } else if ("➡️".equals(emoji)) {
            handleRight(event, sokobanRoom);
        } else {
            return;
        }

        event.getReaction().removeReaction(event.getUser()).queue();
    }

    private void printBoard(MessageReactionAddEvent event, SokobanRoom sokobanRoom) {
        event.getChannel()
                .retrieveMessageById(event.getMessageId())
                .queue(
                        (message) -> {
                            MessageEmbed messageEmbed = SokobanUtils.buildLevel(sokobanRoom);

                            message.editMessageEmbeds(messageEmbed).queue();
                        },
                        (error) -> {
                            System.out.println(error);
                        }
                );
    }

    private void handleRight(MessageReactionAddEvent event, SokobanRoom sokobanRoom) {
        if (sokobanGameService.moveRight(sokobanRoom)) {
            sokobanGameService.save(sokobanRoom);
            printBoard(event, sokobanRoom);
        }
    }

    private void handleLeft(MessageReactionAddEvent event, SokobanRoom sokobanRoom) {
        if (sokobanGameService.moveLeft(sokobanRoom)) {
            sokobanGameService.save(sokobanRoom);
            printBoard(event, sokobanRoom);
        }
    }

    private void handleDown(MessageReactionAddEvent event, SokobanRoom sokobanRoom) {
        if(sokobanGameService.moveDown(sokobanRoom)) {
            sokobanGameService.save(sokobanRoom);
            printBoard(event, sokobanRoom);
        }

    }

    private void handleUp(MessageReactionAddEvent event, SokobanRoom sokobanRoom) {
        if (sokobanGameService.moveUp(sokobanRoom)) {
            sokobanGameService.save(sokobanRoom);
            printBoard(event, sokobanRoom);
        }
    }
}
