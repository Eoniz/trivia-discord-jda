package com.github.eoniz.nexus.model.player;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Player {
    private final String id;
    private final String effectiveName;
}
