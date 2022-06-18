package com.github.eoniz.nexus.persistence.entity.players;

import com.github.eoniz.nexus.model.player.Player;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlayerEntityMapper {
    public PlayerEntity of(Player player) {
        return PlayerEntity.builder()
                .id(player.getId())
                .effectiveName(player.getEffectiveName())
                .build();
    }

    public Player of(PlayerEntity player) {
        return Player.builder()
                .id(player.getId())
                .effectiveName(player.getEffectiveName())
                .build();
    }
}
