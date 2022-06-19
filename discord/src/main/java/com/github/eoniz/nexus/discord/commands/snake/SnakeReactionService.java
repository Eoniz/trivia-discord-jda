package com.github.eoniz.nexus.discord.commands.snake;

import com.github.eoniz.nexus.core.snake.exception.SnakeGameFinishedException;
import com.github.eoniz.nexus.core.snake.exception.SnakeHeadCollidesBodyException;
import com.github.eoniz.nexus.core.snake.exception.SnakeHeadCollidesWallException;
import com.github.eoniz.nexus.core.snake.service.SnakeGameService;
import com.github.eoniz.nexus.model.snake.room.SnakeRoom;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

@AllArgsConstructor
public class SnakeReactionService {

    private final SnakeGameService snakeGameService = new SnakeGameService();

    public void handleReaction(MessageReactionAddEvent event, SnakeRoom snakeRoom) {
        String emoji = event.getReaction().getReactionEmote().getEmoji();

        try {
            if ("⬆️".equals(emoji)) {
                handleUp(event, snakeRoom);
            } else if ("⬇️".equals(emoji)) {
                handleDown(event, snakeRoom);
            } else if ("⬅️".equals(emoji)) {
                handleLeft(event, snakeRoom);
            } else if ("➡️".equals(emoji)) {
                handleRight(event, snakeRoom);
            } else {
                return;
            }
        } catch (SnakeHeadCollidesBodyException e) {
            event.getTextChannel().sendMessage(
                    event.getMember().getAsMention() + " tu as perdu loser !"
            ).queue();

            printBoardLose(event, snakeRoom);

            snakeGameService.destroyRoom(event.getMessageId());
            return;
        } catch (SnakeGameFinishedException e) {
            event.getTextChannel().sendMessage(
                    event.getMember().getAsMention() + " tu as gagné ! trop fort bg :sunglasses:"
            ).queue();

            snakeGameService.destroyRoom(event.getMessageId());
        } catch (SnakeHeadCollidesWallException e) {}

        event.getReaction().removeReaction(event.getUser()).queue();
    }

    private void handleRight(MessageReactionAddEvent event, SnakeRoom snakeRoom) throws SnakeHeadCollidesWallException, SnakeHeadCollidesBodyException, SnakeGameFinishedException {
        SnakeRoom updatedSnakeRoom = snakeGameService.moveRight(snakeRoom);

        printBoard(event, updatedSnakeRoom);

        snakeGameService.save(updatedSnakeRoom);
    }

    private void handleLeft(MessageReactionAddEvent event, SnakeRoom snakeRoom) throws SnakeHeadCollidesWallException, SnakeHeadCollidesBodyException, SnakeGameFinishedException {
        SnakeRoom updatedSnakeRoom = snakeGameService.moveLeft(snakeRoom);

        printBoard(event, updatedSnakeRoom);

        snakeGameService.save(updatedSnakeRoom);
    }

    private void handleDown(MessageReactionAddEvent event, SnakeRoom snakeRoom) throws SnakeHeadCollidesWallException, SnakeHeadCollidesBodyException, SnakeGameFinishedException {
        SnakeRoom updatedSnakeRoom = snakeGameService.moveDown(snakeRoom);

        printBoard(event, updatedSnakeRoom);

        snakeGameService.save(updatedSnakeRoom);
    }

    private void handleUp(MessageReactionAddEvent event, SnakeRoom snakeRoom) throws SnakeHeadCollidesWallException, SnakeHeadCollidesBodyException, SnakeGameFinishedException {
        SnakeRoom updatedSnakeRoom = snakeGameService.moveTop(snakeRoom);

        printBoard(event, updatedSnakeRoom);

        snakeGameService.save(updatedSnakeRoom);
    }

    private void printBoard(MessageReactionAddEvent event, SnakeRoom updatedSnakeRoom) {
        event.getChannel()
                .retrieveMessageById(event.getMessageId())
                .queue(
                        (message) -> {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int y = 0; y < 10; y++) {
                                for (int x = 0; x < 10; x++) {
                                    SnakeRoom.EntityType entityType = updatedSnakeRoom.getEntityType(x, y);

                                    if (entityType == SnakeRoom.EntityType.SNAKE_HEAD) {
                                        stringBuilder.append(":orange_square:");
                                    } else if (entityType == SnakeRoom.EntityType.SNAKE) {
                                        stringBuilder.append(":yellow_square:");
                                    } else if (entityType == SnakeRoom.EntityType.APPLE) {
                                        stringBuilder.append(":apple:");
                                    } else {
                                        stringBuilder.append(":blue_square:");
                                    }
                                }
                                stringBuilder.append("\n");
                            }

                            EmbedBuilder embedBuilder = new EmbedBuilder();
                            embedBuilder.setDescription(stringBuilder.toString());

                            message.editMessageEmbeds(embedBuilder.build()).queue();
                        },
                        (error) -> {
                            event.getTextChannel().sendMessage(
                                    event.getMember().getAsMention() + " Une erreur est survenue !"
                            ).queue();
                        }
                );
    }

    private void printBoardLose(MessageReactionAddEvent event, SnakeRoom updatedSnakeRoom) {
        event.getChannel()
                .retrieveMessageById(event.getMessageId())
                .queue(
                        (message) -> {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int y = 0; y < 10; y++) {
                                for (int x = 0; x < 10; x++) {
                                    SnakeRoom.EntityType entityType = updatedSnakeRoom.getEntityType(x, y);

                                    if (entityType == SnakeRoom.EntityType.SNAKE_HEAD) {
                                        stringBuilder.append(":orange_square:");
                                    } else if (entityType == SnakeRoom.EntityType.SNAKE) {
                                        stringBuilder.append(":yellow_square:");
                                    } else if (entityType == SnakeRoom.EntityType.APPLE) {
                                        stringBuilder.append(":apple:");
                                    } else {
                                        stringBuilder.append(":red_square:");
                                    }
                                }
                                stringBuilder.append("\n");
                            }

                            EmbedBuilder embedBuilder = new EmbedBuilder();
                            embedBuilder.setDescription(stringBuilder.toString());

                            message.editMessageEmbeds(embedBuilder.build()).queue();
                        },
                        (error) -> {
                            event.getTextChannel().sendMessage(
                                    event.getMember().getAsMention() + " Une erreur est survenue !"
                            ).queue();
                        }
                );
    }

}
