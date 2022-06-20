package com.github.eoniz.nexus.core.sokoban.service;

import com.github.eoniz.nexus.model.common.Position;
import com.github.eoniz.nexus.model.sokoban.level.tiles.SokobanTile;
import com.github.eoniz.nexus.model.sokoban.player.SokobanPlayer;
import com.github.eoniz.nexus.model.sokoban.room.SokobanRoom;
import com.github.eoniz.nexus.persistence.dao.rooms.sokoban.SokobanRoomDao;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class SokobanGameService {

    private final SokobanRoomDao sokobanRoomDao = new SokobanRoomDao();

    public SokobanRoom createRoom(SokobanPlayer owner) {
        SokobanRoom room = SokobanRoom.builder()
                .owner(owner)
                .build();

        return room;
    }

    public void save(SokobanRoom sokobanRoom) {
        sokobanRoomDao.save(sokobanRoom);
    }

    public Optional<SokobanRoom> getSokobanRoomByMessageId(String messageId) {
        return sokobanRoomDao.getRoomByMessageId(messageId);
    }

    public boolean moveRight(SokobanRoom sokobanRoom) {
        Position playerPosition = sokobanRoom.getSokobanLevel().getPlayerPosition();
        Position nextPlayerPosition = Position.builder()
                .x(playerPosition.getX() + 1)
                .y(playerPosition.getY())
                .build();

        if (!handleCrates(sokobanRoom, PlayerDirection.RIGHT, nextPlayerPosition)) {
            return false;
        }

        sokobanRoom.getSokobanLevel().setPlayerPosition(nextPlayerPosition);
        return true;
    }

    public boolean moveLeft(SokobanRoom sokobanRoom) {
        Position playerPosition = sokobanRoom.getSokobanLevel().getPlayerPosition();
        Position nextPlayerPosition = Position.builder()
                .x(playerPosition.getX() - 1)
                .y(playerPosition.getY())
                .build();

        if (!handleCrates(sokobanRoom, PlayerDirection.LEFT, nextPlayerPosition)) {
            return false;
        }

        sokobanRoom.getSokobanLevel().setPlayerPosition(nextPlayerPosition);
        return true;
    }

    public boolean moveDown(SokobanRoom sokobanRoom) {
        Position playerPosition = sokobanRoom.getSokobanLevel().getPlayerPosition();
        Position nextPlayerPosition = Position.builder()
                .x(playerPosition.getX())
                .y(playerPosition.getY() + 1)
                .build();

        if (!handleCrates(sokobanRoom, PlayerDirection.DOWN, nextPlayerPosition)) {
            return false;
        }

        sokobanRoom.getSokobanLevel().setPlayerPosition(nextPlayerPosition);
        return true;
    }

    public boolean moveUp(SokobanRoom sokobanRoom) {
        Position playerPosition = sokobanRoom.getSokobanLevel().getPlayerPosition();
        Position nextPlayerPosition = Position.builder()
                .x(playerPosition.getX())
                .y(playerPosition.getY() - 1)
                .build();

        if (!handleCrates(sokobanRoom, PlayerDirection.UP, nextPlayerPosition)) {
            return false;
        }

        sokobanRoom.getSokobanLevel().setPlayerPosition(nextPlayerPosition);
        return true;
    }

    private boolean handleCrates(SokobanRoom sokobanRoom, PlayerDirection playerDirection, Position nextPlayerPosition) {
        for (int i = 0; i < sokobanRoom.getSokobanLevel().getCratePositions().length; i++) {
            if (nextPlayerPosition.equals(sokobanRoom.getSokobanLevel().getCratePositions()[i])) {
                Position nextCratePosition = sokobanRoom.getSokobanLevel().getCratePositions()[i];
                if (playerDirection == PlayerDirection.UP) {
                    nextCratePosition = nextCratePosition.toBuilder()
                            .y(nextCratePosition.getY() - 1)
                            .build();
                }

                if (playerDirection == PlayerDirection.DOWN) {
                    nextCratePosition = nextCratePosition.toBuilder()
                            .y(nextCratePosition.getY() + 1)
                            .build();
                }

                if (playerDirection == PlayerDirection.LEFT) {
                    nextCratePosition = nextCratePosition.toBuilder()
                            .x(nextCratePosition.getX() - 1)
                            .build();
                }

                if (playerDirection == PlayerDirection.RIGHT) {
                    nextCratePosition = nextCratePosition.toBuilder()
                            .x(nextCratePosition.getX() + 1)
                            .build();
                }

                SokobanTile tile = sokobanRoom.getSokobanLevel()
                        .getTileAt(nextCratePosition.getX(), nextCratePosition.getY());

                if (tile == SokobanTile.crate || tile == SokobanTile.wall) {
                    return false;
                }

                sokobanRoom.getSokobanLevel().getCratePositions()[i] = nextCratePosition;
            }
        }

        return true;
    }

    enum PlayerDirection {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }
}
