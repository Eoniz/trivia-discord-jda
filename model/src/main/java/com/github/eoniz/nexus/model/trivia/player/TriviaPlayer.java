package com.github.eoniz.nexus.model.trivia.player;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TriviaPlayer {
    private final String id;
    private final String effectiveName;
}
