package com.github.eoniz.nexus.discord.events.reaction;

import com.github.eoniz.nexus.core.snake.service.SnakeGameService;
import com.github.eoniz.nexus.discord.commands.snake.SnakeReactionService;
import com.github.eoniz.nexus.model.snake.room.SnakeRoom;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@AllArgsConstructor
public class ReactionListener extends ListenerAdapter {

    private final SnakeGameService snakeGameService = new SnakeGameService();
    private final SnakeReactionService snakeReactionService = new SnakeReactionService();

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (event.getReaction().isSelf()) {
            return;
        }

        Optional<SnakeRoom> snakeRoom = snakeGameService.getSnakeRoomByMessageId(event.getMessageId());
        if (snakeRoom.isPresent()) {
            this.handleSnakeGameReaction(event, snakeRoom.get());
        }
    }

    private void handleSnakeGameReaction(MessageReactionAddEvent event, SnakeRoom room) {
        snakeReactionService.handleReaction(event, room);
    }
}
