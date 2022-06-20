package com.github.eoniz.nexus.persistence.entity.sokoban.player;

import com.github.eoniz.nexus.model.sokoban.player.SokobanPlayer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SokobanPlayerEntityMapper {
    public SokobanPlayer of(SokobanPlayerEntity sokobanPlayerEntity) {
        return SokobanPlayer.builder()
                .id(sokobanPlayerEntity.getId())
                .asMention(sokobanPlayerEntity.getAsMention())
                .effectiveName(sokobanPlayerEntity.getEffectiveName())
                .build();
    }

    public SokobanPlayerEntity of(SokobanPlayer sokobanPlayer) {
        return SokobanPlayerEntity.builder()
                .id(sokobanPlayer.getId())
                .asMention(sokobanPlayer.getAsMention())
                .effectiveName(sokobanPlayer.getEffectiveName())
                .build();
    }
}
