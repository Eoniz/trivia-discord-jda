package com.github.eoniz.nexus.model.sokoban.room;

import com.github.eoniz.nexus.model.sokoban.level.SokobanLevel;
import com.github.eoniz.nexus.model.sokoban.player.SokobanPlayer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class SokobanRoom {
    private String rootMessageId;
    private String gameMessageId;
    private SokobanPlayer owner;
    private SokobanLevel sokobanLevel;
}
