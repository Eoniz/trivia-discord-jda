package com.github.eoniz.nexus.persistence.entity.connectfour.player;

import com.github.eoniz.nexus.model.connectfour.player.ConnectFourPlayer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConnectFourPlayerEntityMapper {
    public ConnectFourPlayer of(ConnectFourPlayerEntity connectFourPlayerEntity) {
        return ConnectFourPlayer.builder()
                .id(connectFourPlayerEntity.getId())
                .effectiveName(connectFourPlayerEntity.getEffectiveName())
                .asMention(connectFourPlayerEntity.getAsMention())
                .build();
    }

    public ConnectFourPlayerEntity of(ConnectFourPlayer connectFourPlayer) {
        return ConnectFourPlayerEntity.builder()
                .id(connectFourPlayer.getId())
                .effectiveName(connectFourPlayer.getEffectiveName())
                .asMention(connectFourPlayer.getAsMention())
                .build();
    }
}
