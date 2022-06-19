package com.github.eoniz.nexus.persistence.entity.snake.player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnakePlayerEntity {
    private String id;
    private String effectiveName;
}
