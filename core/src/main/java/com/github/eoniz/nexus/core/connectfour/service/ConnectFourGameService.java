package com.github.eoniz.nexus.core.connectfour.service;

import com.github.eoniz.nexus.model.connectfour.grid.ConnectFourGrid;
import com.github.eoniz.nexus.model.connectfour.player.ConnectFourPlayer;
import com.github.eoniz.nexus.model.connectfour.room.ConnectFourRoom;
import com.github.eoniz.nexus.persistence.dao.rooms.connectfour.ConnectFourRoomDao;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class ConnectFourGameService {
    private final ConnectFourRoomDao connectFourRoomDao = new ConnectFourRoomDao();

    public ConnectFourRoom createRoom(ConnectFourPlayer firstPlayer, ConnectFourPlayer secondPlayer) {
        ConnectFourGrid connectFourGrid = ConnectFourGrid.builder().build();

        ConnectFourPlayer startingPlayer = Math.round(Math.random() * 1) == 0 ? firstPlayer : secondPlayer;

        return ConnectFourRoom.builder()
                .messageId(null)
                .firstPlayer(firstPlayer)
                .secondPlayer(secondPlayer)
                .connectFourGrid(connectFourGrid)
                .actualPlayerTurnId(startingPlayer.getId())
                .build();
    }

    public boolean place(ConnectFourRoom connectFourRoom, int x) {
        ConnectFourGrid.GridPlayer gridPlayer = (
                connectFourRoom.getActualPlayer().equals(connectFourRoom.getFirstPlayer())
                        ? ConnectFourGrid.GridPlayer.FIRST
                        : ConnectFourGrid.GridPlayer.SECOND
        );

        boolean placed = connectFourRoom.place(gridPlayer, x);

        if (!placed) {
            return false;
        }

        if (connectFourRoom.getActualPlayerTurnId().equals(connectFourRoom.getFirstPlayer().getId())) {
            connectFourRoom.setActualPlayerTurnId(connectFourRoom.getSecondPlayer().getId());
        } else {
            connectFourRoom.setActualPlayerTurnId(connectFourRoom.getFirstPlayer().getId());
        }

        save(connectFourRoom);
        return true;
    }

    public ConnectFourRoom save(ConnectFourRoom connectFourRoom) {
        connectFourRoomDao.save(connectFourRoom);
        return connectFourRoom;
    }

    public Optional<ConnectFourRoom> getConnectFourRoomByMessageId(String messageId) {
        return connectFourRoomDao.getRoomByMessageId(messageId);
    }

    public Optional<ConnectFourPlayer> checkIfWin(ConnectFourRoom connectFourRoom) {
        if (connectFourRoom.getConnectFourGrid().hasPlayerWon(ConnectFourGrid.GridPlayer.FIRST)) {
            return Optional.of(connectFourRoom.getFirstPlayer());
        }

        if (connectFourRoom.getConnectFourGrid().hasPlayerWon(ConnectFourGrid.GridPlayer.SECOND)) {
            return Optional.of(connectFourRoom.getSecondPlayer());
        }

        return Optional.empty();
    }

    public void destroy(String messageId) {
        connectFourRoomDao.destroy(messageId);
    }

    public boolean checkIfCorrectPlayer(ConnectFourRoom connectFourRoom, String id) {
        String actualPlayerTurnId = connectFourRoom.getActualPlayerTurnId();
        return actualPlayerTurnId.equals(id);
    }
}
