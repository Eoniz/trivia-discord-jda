package com.github.eoniz.nexus.persistence.entity.snake.room;

import com.github.eoniz.nexus.model.common.Position;
import com.github.eoniz.nexus.model.snake.player.SnakePlayer;
import com.github.eoniz.nexus.model.snake.room.SnakeRoom;
import com.github.eoniz.nexus.persistence.entity.common.PositionEntity;
import com.github.eoniz.nexus.persistence.entity.common.PositionEntityMapper;
import com.github.eoniz.nexus.persistence.entity.snake.player.SnakePlayerEntity;
import com.github.eoniz.nexus.persistence.entity.snake.player.SnakePlayerEntityMapper;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class SnakeRoomEntityMapper {
    private final PositionEntityMapper positionEntityMapper = new PositionEntityMapper();
    private final SnakePlayerEntityMapper snakePlayerEntityMapper = new SnakePlayerEntityMapper();

    public SnakeRoomEntity of(SnakeRoom snakeRoom) {
        PositionEntity applePosition = positionEntityMapper.of(snakeRoom.getApplePosition());
        List<PositionEntity> snake = positionEntityMapper.ofModels(snakeRoom.getSnake());

        SnakePlayerEntity owner = snakePlayerEntityMapper.of(snakeRoom.getOwner());

        return SnakeRoomEntity.builder()
                .roomId(snakeRoom.getRoomId())
                .applePosition(applePosition)
                .snake(snake)
                .owner(owner)
                .messageId(snakeRoom.getMessageId())
                .build();
    }

    public SnakeRoom of(SnakeRoomEntity snakeRoomEntity) {
        Position applePosition = positionEntityMapper.of(snakeRoomEntity.getApplePosition());
        List<Position> snake = positionEntityMapper.ofEntities(snakeRoomEntity.getSnake());

        SnakePlayer owner = snakePlayerEntityMapper.of(snakeRoomEntity.getOwner());

        return SnakeRoom.builder()
                .roomId(snakeRoomEntity.getRoomId())
                .applePosition(applePosition)
                .snake(snake)
                .owner(owner)
                .messageId(snakeRoomEntity.getMessageId())
                .build();
    }

}
