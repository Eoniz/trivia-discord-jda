package com.github.eoniz.nexus.persistence.entity.connectfour.room;

import com.github.eoniz.nexus.persistence.entity.connectfour.grid.ConnectFourGridEntity;
import com.github.eoniz.nexus.persistence.entity.connectfour.player.ConnectFourPlayerEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectFourRoomEntity {
    private String messageId;
    private ConnectFourPlayerEntity firstPlayer;
    private ConnectFourPlayerEntity secondPlayer;
    private ConnectFourGridEntity connectFourGrid;
    private String actualPlayerTurnId;
}
