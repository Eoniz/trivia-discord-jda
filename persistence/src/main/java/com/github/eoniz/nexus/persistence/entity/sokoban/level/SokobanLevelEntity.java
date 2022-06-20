package com.github.eoniz.nexus.persistence.entity.sokoban.level;

import com.github.eoniz.nexus.persistence.entity.common.PositionEntity;
import lombok.*;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SokobanLevelEntity {
    private String id;
    private String label;
    private String level;
    private Integer height;
    private Integer width;
    private PositionEntity playerPosition;
    private List<PositionEntity> cratePositions;
    private List<PositionEntity> flagPositions;
}
