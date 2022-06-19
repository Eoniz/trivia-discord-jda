package com.github.eoniz.nexus.persistence.entity.common;

import com.github.eoniz.nexus.model.common.Position;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class PositionEntityMapper {
    public PositionEntity of(Position position) {
        return PositionEntity.builder()
                .x(position.getX())
                .y(position.getY())
                .build();
    }

    public Position of(PositionEntity positionEntity) {
        return Position.builder()
                .x(positionEntity.getX())
                .y(positionEntity.getY())
                .build();
    }

    public List<PositionEntity> ofModels(List<Position> snake) {
        return snake.stream().map(this::of).collect(Collectors.toList());
    }

    public List<Position> ofEntities(List<PositionEntity> snake) {
        return snake.stream().map(this::of).collect(Collectors.toList());
    }
}
