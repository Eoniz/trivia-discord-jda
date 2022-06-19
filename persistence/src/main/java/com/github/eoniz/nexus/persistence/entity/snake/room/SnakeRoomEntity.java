package com.github.eoniz.nexus.persistence.entity.snake.room;

import com.github.eoniz.nexus.persistence.entity.common.PositionEntity;
import com.github.eoniz.nexus.persistence.entity.snake.player.SnakePlayerEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnakeRoomEntity {
    private String roomId;
    private PositionEntity applePosition;
    private List<PositionEntity> snake;
    private SnakePlayerEntity owner;
    private String messageId;
}
