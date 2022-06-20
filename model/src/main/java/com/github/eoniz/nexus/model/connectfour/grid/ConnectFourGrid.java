package com.github.eoniz.nexus.model.connectfour.grid;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ConnectFourGrid {

    public final static Integer WIDTH = 7;
    public final static Integer HEIGHT = 7;

    @Builder.Default
    private long board              = 0b1000000_0000000_0000000_0000000_0000000_0000000_0000000_0000000_0000000L;
    @Builder.Default
    private long boardFirstPlayer   = 0b1000000_0000000_0000000_0000000_0000000_0000000_0000000_0000000_0000000L;
    @Builder.Default
    private long boardSecondPlayer  = 0b1000000_0000000_0000000_0000000_0000000_0000000_0000000_0000000_0000000L;
    @Builder.Default
    private int[] heights = new int[] { 0, 1 * HEIGHT, 2 * HEIGHT, 3 * HEIGHT, 4 * HEIGHT, 5 * HEIGHT, 6 * HEIGHT };

    private static final int[] INITIAL_HEIGHTS = new int[] { 0, 1 * HEIGHT, 2 * HEIGHT, 3 * HEIGHT, 4 * HEIGHT, 5 * HEIGHT, 6 * HEIGHT };

    public boolean hasPlayerWon(GridPlayer player) {
        long bitboard = player == GridPlayer.FIRST ? boardFirstPlayer : boardSecondPlayer;

        // diagonal \
        if (((bitboard & (bitboard >> 6) & (bitboard >> 12) & (bitboard >> 18)) >> 1) != 0)
            return true;

        // diagonal /
        if (((bitboard & (bitboard >> 8) & (bitboard >> 16) & (bitboard >> 24)) >> 1) != 0)
            return true;

        // horizontal
        if (((bitboard & (bitboard >> 7) & (bitboard >> 14) & (bitboard >> 21)) >> 1) != 0)
            return true;

        // vertical
        if (((bitboard & (bitboard >> 1) & (bitboard >>  2) & (bitboard >>  3)) >> 1) != 0)
            return true;

        return false;
    }

    public boolean place(GridPlayer player, int x) {
        if (heights[x] + 1 >= INITIAL_HEIGHTS[x] + HEIGHT) {
            return false;
        }

        long move = 1L << heights[x]++;

        if (player == GridPlayer.FIRST) {
            boardFirstPlayer ^= move;
        } else {
            boardSecondPlayer ^= move;
        }

        return true;
    }

    public static int[][] defaultGrid() {
        int[][] newGrid = new int[WIDTH][HEIGHT];

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                newGrid[x][y] = 0;
            }
        }

        return newGrid;
    }

    public enum GridPlayer {
        FIRST,
        SECOND
    }
}
