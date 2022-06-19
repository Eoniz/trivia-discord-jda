package com.github.eoniz.nexus.persistence.entity.connectfour.grid;

import com.github.eoniz.nexus.model.connectfour.grid.ConnectFourGrid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConnectFourGridEntityMapper {

    public ConnectFourGrid of(ConnectFourGridEntity connectFourGridEntity) {
        return ConnectFourGrid.builder()
                .board(Long.parseLong(connectFourGridEntity.getBoard(), 2))
                .boardFirstPlayer(Long.parseLong(connectFourGridEntity.getBoardFirstPlayer(), 2))
                .boardSecondPlayer(Long.parseLong(connectFourGridEntity.getBoardSecondPlayer(), 2))
                .heights(connectFourGridEntity.getHeights())
                .build();
    }

    public ConnectFourGridEntity of(ConnectFourGrid connectFourGrid) {
        return ConnectFourGridEntity.builder()
                .board(Long.toBinaryString(connectFourGrid.getBoard()))
                .boardFirstPlayer(Long.toBinaryString(connectFourGrid.getBoardFirstPlayer()))
                .boardSecondPlayer(Long.toBinaryString(connectFourGrid.getBoardSecondPlayer()))
                .heights(connectFourGrid.getHeights())
                .build();
    }

}
