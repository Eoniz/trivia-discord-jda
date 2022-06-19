package com.github.eoniz.nexus.model.snake.room;

import com.github.eoniz.nexus.model.common.Position;
import com.github.eoniz.nexus.model.snake.player.SnakePlayer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SnakeRoom {
    private String roomId;
    private Position applePosition;
    private List<Position> snake;
    private SnakePlayer owner;
    private String messageId;

    public EntityType getEntityType(int i, int j) {
        if (applePosition.equals(i, j)) {
            return EntityType.APPLE;
        }

        for (int k = 0; k < snake.size(); k++) {
            Position snakePosition = snake.get(k);
            if (snakePosition.equals(i, j)) {
                return k == 0 ? EntityType.SNAKE_HEAD : EntityType.SNAKE;
            }
        }

        return EntityType.NOTHING;
    }

    public enum EntityType {
        APPLE,
        SNAKE_HEAD,
        SNAKE,
        NOTHING
    }

}
