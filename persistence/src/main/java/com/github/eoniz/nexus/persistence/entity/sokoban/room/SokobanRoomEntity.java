package com.github.eoniz.nexus.persistence.entity.sokoban.room;

import com.github.eoniz.nexus.persistence.entity.sokoban.level.SokobanLevelEntity;
import com.github.eoniz.nexus.persistence.entity.sokoban.player.SokobanPlayerEntity;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SokobanRoomEntity {
    private String rootMessageId;
    private String gameMessageId;
    private SokobanPlayerEntity owner;
    private SokobanLevelEntity sokobanLevel;
}
