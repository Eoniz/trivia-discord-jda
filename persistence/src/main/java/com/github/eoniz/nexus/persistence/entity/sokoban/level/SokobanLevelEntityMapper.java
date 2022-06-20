package com.github.eoniz.nexus.persistence.entity.sokoban.level;

import com.github.eoniz.nexus.model.common.Position;
import com.github.eoniz.nexus.model.sokoban.level.SokobanLevel;
import com.github.eoniz.nexus.persistence.entity.common.PositionEntity;
import com.github.eoniz.nexus.persistence.entity.common.PositionEntityMapper;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class SokobanLevelEntityMapper {

    private final PositionEntityMapper positionEntityMapper = new PositionEntityMapper();

    public SokobanLevel of(SokobanLevelEntity sokobanLevelEntity) {
        Position[] flagPositions = positionEntityMapper.ofEntities(sokobanLevelEntity.getFlagPositions())
                .toArray(Position[]::new);

        Position[] cratePositions = positionEntityMapper.ofEntities(sokobanLevelEntity.getCratePositions())
                .toArray(Position[]::new);

        return SokobanLevel.builder()
                .id(sokobanLevelEntity.getId())
                .label(sokobanLevelEntity.getLabel())
                .level(sokobanLevelEntity.getLevel())
                .width(sokobanLevelEntity.getWidth())
                .height(sokobanLevelEntity.getHeight())
                .playerPosition(positionEntityMapper.of(sokobanLevelEntity.getPlayerPosition()))
                .flagPositions(flagPositions)
                .cratePositions(cratePositions)
                .build();
    }

    public SokobanLevelEntity of(SokobanLevel sokobanLevel) {

        List<PositionEntity> flagPositions = positionEntityMapper.ofModels(List.of(sokobanLevel.getFlagPositions()));
        List<PositionEntity> cratePositions = positionEntityMapper.ofModels(List.of(sokobanLevel.getCratePositions()));

        return SokobanLevelEntity.builder()
                .id(sokobanLevel.getId())
                .label(sokobanLevel.getLabel())
                .level(sokobanLevel.getLevel())
                .width(sokobanLevel.getWidth())
                .height(sokobanLevel.getHeight())
                .playerPosition(positionEntityMapper.of(sokobanLevel.getPlayerPosition()))
                .flagPositions(flagPositions)
                .cratePositions(cratePositions)
                .build();
    }
}
