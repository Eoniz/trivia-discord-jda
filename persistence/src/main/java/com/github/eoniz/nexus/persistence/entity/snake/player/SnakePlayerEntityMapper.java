package com.github.eoniz.nexus.persistence.entity.snake.player;

import com.github.eoniz.nexus.model.snake.player.SnakePlayer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SnakePlayerEntityMapper {
    public SnakePlayer of(SnakePlayerEntity snakePlayerEntity) {
        return SnakePlayer.builder()
                .id(snakePlayerEntity.getId())
                .effectiveName(snakePlayerEntity.getEffectiveName())
                .build();
    }

    public SnakePlayerEntity of(SnakePlayer snakePlayer) {
        return SnakePlayerEntity.builder()
                .id(snakePlayer.getId())
                .effectiveName(snakePlayer.getEffectiveName())
                .build();
    }
}
