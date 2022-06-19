package com.github.eoniz.nexus.model.snake.player;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SnakePlayer {
    private final String id;
    private final String effectiveName;
}
