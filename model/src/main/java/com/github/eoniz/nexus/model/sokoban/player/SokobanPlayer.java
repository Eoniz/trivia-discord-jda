package com.github.eoniz.nexus.model.sokoban.player;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SokobanPlayer {
    private final String id;
    private final String effectiveName;
    private final String asMention;
}
