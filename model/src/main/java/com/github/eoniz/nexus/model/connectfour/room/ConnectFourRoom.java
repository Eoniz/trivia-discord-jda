package com.github.eoniz.nexus.model.connectfour.room;

import com.github.eoniz.nexus.model.connectfour.grid.ConnectFourGrid;
import com.github.eoniz.nexus.model.connectfour.player.ConnectFourPlayer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class ConnectFourRoom {
    private String messageId;
    private ConnectFourPlayer firstPlayer;
    private ConnectFourPlayer secondPlayer;
    private ConnectFourGrid connectFourGrid;
    private String actualPlayerTurnId;

    public ConnectFourPlayer getActualPlayer() {
        return (
                actualPlayerTurnId.equals(firstPlayer.getId())
                    ? firstPlayer
                    : secondPlayer
        );
    }

    public boolean place(ConnectFourGrid.GridPlayer player, int x) {
        return connectFourGrid.place(player, x);
    }
}
