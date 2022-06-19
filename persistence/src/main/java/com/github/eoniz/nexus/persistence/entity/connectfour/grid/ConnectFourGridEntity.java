package com.github.eoniz.nexus.persistence.entity.connectfour.grid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectFourGridEntity {
    private String board;
    private String boardFirstPlayer;
    private String boardSecondPlayer;
    private int[] heights;
}
