package com.github.eoniz.nexus.persistence.entity.connectfour.room;

import com.github.eoniz.nexus.model.connectfour.player.ConnectFourPlayer;
import com.github.eoniz.nexus.model.connectfour.room.ConnectFourRoom;
import com.github.eoniz.nexus.persistence.entity.connectfour.grid.ConnectFourGridEntityMapper;
import com.github.eoniz.nexus.persistence.entity.connectfour.player.ConnectFourPlayerEntity;
import com.github.eoniz.nexus.persistence.entity.connectfour.player.ConnectFourPlayerEntityMapper;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConnectFourRoomEntityMapper {

    private final ConnectFourPlayerEntityMapper connectFourPlayerEntityMapper = new ConnectFourPlayerEntityMapper();
    private final ConnectFourGridEntityMapper connectFourGridEntityMapper = new ConnectFourGridEntityMapper();

    public ConnectFourRoom of(ConnectFourRoomEntity connectFourRoomEntity) {
        return ConnectFourRoom.builder()
                .messageId(connectFourRoomEntity.getMessageId())
                .firstPlayer(connectFourPlayerEntityMapper.of(connectFourRoomEntity.getFirstPlayer()))
                .secondPlayer(connectFourPlayerEntityMapper.of(connectFourRoomEntity.getSecondPlayer()))
                .connectFourGrid(connectFourGridEntityMapper.of(connectFourRoomEntity.getConnectFourGrid()))
                .actualPlayerTurnId(connectFourRoomEntity.getActualPlayerTurnId())
                .build();
    }

    public ConnectFourRoomEntity of(ConnectFourRoom connectFourRoom) {
        return ConnectFourRoomEntity.builder()
                .messageId(connectFourRoom.getMessageId())
                .firstPlayer(connectFourPlayerEntityMapper.of(connectFourRoom.getFirstPlayer()))
                .secondPlayer(connectFourPlayerEntityMapper.of(connectFourRoom.getSecondPlayer()))
                .connectFourGrid(connectFourGridEntityMapper.of(connectFourRoom.getConnectFourGrid()))
                .actualPlayerTurnId(connectFourRoom.getActualPlayerTurnId())
                .build();
    }
}
