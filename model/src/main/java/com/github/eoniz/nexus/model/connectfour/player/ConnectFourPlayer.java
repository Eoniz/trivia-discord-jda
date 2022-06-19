package com.github.eoniz.nexus.model.connectfour.player;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConnectFourPlayer {
    private final String id;
    private final String effectiveName;
    private final String asMention;
}
