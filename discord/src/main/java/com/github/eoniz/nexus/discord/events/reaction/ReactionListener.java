package com.github.eoniz.nexus.discord.events.reaction;

import com.github.eoniz.nexus.core.connectfour.service.ConnectFourGameService;
import com.github.eoniz.nexus.core.snake.service.SnakeGameService;
import com.github.eoniz.nexus.core.sokoban.service.SokobanGameService;
import com.github.eoniz.nexus.discord.commands.sokoban.SokobanReactionService;
import com.github.eoniz.nexus.discord.commands.connectfour.ConnectFourReactionService;
import com.github.eoniz.nexus.discord.commands.snake.SnakeReactionService;
import com.github.eoniz.nexus.model.connectfour.room.ConnectFourRoom;
import com.github.eoniz.nexus.model.snake.room.SnakeRoom;
import com.github.eoniz.nexus.model.sokoban.room.SokobanRoom;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@AllArgsConstructor
public class ReactionListener extends ListenerAdapter {

    private final SnakeGameService snakeGameService = new SnakeGameService();
    private final SnakeReactionService snakeReactionService = new SnakeReactionService();

    private final SokobanGameService sokobanGameService = new SokobanGameService();

    private final ConnectFourGameService connectFourGameService = new ConnectFourGameService();
    private final ConnectFourReactionService connectFourReactionService = new ConnectFourReactionService();
    private final SokobanReactionService sokobanReactionService = new SokobanReactionService();

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (event.getReaction().isSelf()) {
            return;
        }

        Optional<SokobanRoom> sokobanRoom = (
            sokobanGameService.getSokobanRoomByMessageId(event.getMessageId())
        );

        Optional<SnakeRoom> snakeRoom = (
                snakeGameService.getSnakeRoomByMessageId(event.getMessageId())
        );

        Optional<ConnectFourRoom> connectFourRoom = (
                connectFourGameService.getConnectFourRoomByMessageId(event.getMessageId())
        );

        sokobanRoom.ifPresent(room -> this.handleSokobanGameReaction(event, room));
        snakeRoom.ifPresent(room -> this.handleSnakeGameReaction(event, room));
        connectFourRoom.ifPresent(room -> this.handleConnectFourGameReaction(event, room));
    }

    private void handleSokobanGameReaction(MessageReactionAddEvent event, SokobanRoom room) {
        sokobanReactionService.handleReaction(event, room);
    }

    private void handleConnectFourGameReaction(MessageReactionAddEvent event, ConnectFourRoom room) {
        connectFourReactionService.handleReaction(event, room);
    }

    private void handleSnakeGameReaction(MessageReactionAddEvent event, SnakeRoom room) {
        snakeReactionService.handleReaction(event, room);
    }
}
