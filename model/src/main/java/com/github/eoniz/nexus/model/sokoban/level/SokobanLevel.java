package com.github.eoniz.nexus.model.sokoban.level;

import com.github.eoniz.nexus.model.common.Position;
import com.github.eoniz.nexus.model.sokoban.level.tiles.SokobanTile;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Builder()
public class SokobanLevel {
    private final String id;
    private final String label;
    private final String level;

    private Integer width;
    private Integer height;

    private SokobanTile[][] sokobanTiles;

    private Position playerPosition;
    private Position[] cratePositions;
    private Position[] flagPositions;

    public SokobanTile getTileAt(int x, int y) {
        if (x < 0 || x >= width) {
            return SokobanTile.ground;
        }
        if (y < 0 || y >= height) {
            return SokobanTile.ground;
        }

        if (playerPosition.equals(x, y)) {
            return SokobanTile.player;
        }

        Optional<Position> optCrate = Arrays.stream(cratePositions).filter(c -> c.equals(x, y)).findFirst();
        if (optCrate.isPresent()) {
            return SokobanTile.crate;
        }
        Optional<Position> optFlag = Arrays.stream(flagPositions).filter(c -> c.equals(x, y)).findFirst();
        if (optFlag.isPresent()) {
            return SokobanTile.flag;
        }

        return sokobanTiles[x][y];
    }

    public static SokobanLevelBuilder builder() {
        return new FromEntitySokobanLevelBuilder();
    }

    public static SokobanLevelBuilder baseBuilder() {
        return new BaseSokobanLevelBuilder();
    }


    private static class FromEntitySokobanLevelBuilder extends SokobanLevelBuilder {
        @Override
        public SokobanLevel build() {
            SokobanLevel sokobanLevel = super.build();

            parseLevel(sokobanLevel);

            return sokobanLevel;
        }

        private void parseLevel(SokobanLevel sokobanLevel) {
            final String[] rows = sokobanLevel.getLevel().split("\n");

            SokobanTile[][] grid = new SokobanTile[sokobanLevel.getWidth()][sokobanLevel.getHeight()];

            for (int i = 0; i < sokobanLevel.getWidth(); i++) {
                for (int j = 0; j < sokobanLevel.getHeight(); j++) {
                    char identifier = rows[j].charAt(i);
                    SokobanTile sokobanTile = SokobanTile.getFromIdentifier(identifier);

                    if (sokobanTile == SokobanTile.flag) {
                        grid[i][j] = SokobanTile.ground;
                        continue;
                    }

                    if (sokobanTile == SokobanTile.crate) {;
                        grid[i][j] = SokobanTile.ground;
                        continue;
                    }

                    if (sokobanTile == SokobanTile.player) {
                        grid[i][j] = SokobanTile.ground;
                        continue;
                    }

                    grid[i][j] = sokobanTile;
                }
            }

            sokobanLevel.sokobanTiles = grid;
        }
    }

    private static class BaseSokobanLevelBuilder extends SokobanLevelBuilder {
        @Override
        public SokobanLevel build() {
            SokobanLevel sokobanLevel = super.build();

            parseLevel(sokobanLevel);

            return sokobanLevel;
        }

        private void parseLevel(SokobanLevel sokobanLevel) {
            final String[] rows = sokobanLevel.getLevel().split("\n");
            final int levelWidth = rows[0].length();
            final int levelHeight = rows.length;

            List<Position> flagPositions = new ArrayList<>();
            List<Position> cratePositions = new ArrayList<>();

            sokobanLevel.width = levelWidth;
            sokobanLevel.height = levelHeight;

            SokobanTile[][] grid = new SokobanTile[levelWidth][levelHeight];

            for (int i = 0; i < levelWidth; i++) {
                for (int j = 0; j < levelHeight; j++) {
                    char identifier = rows[j].charAt(i);
                    SokobanTile sokobanTile = SokobanTile.getFromIdentifier(identifier);

                    if (sokobanTile == SokobanTile.flag) {
                        Position flagPosition = Position.builder().x(i).y(j).build();
                        flagPositions.add(flagPosition);
                        grid[i][j] = SokobanTile.ground;
                        continue;
                    }

                    if (sokobanTile == SokobanTile.crate) {
                        Position flagPosition = Position.builder().x(i).y(j).build();
                        cratePositions.add(flagPosition);
                        grid[i][j] = SokobanTile.ground;
                        continue;
                    }

                    if (sokobanTile == SokobanTile.player && sokobanLevel.playerPosition == null) {
                        sokobanLevel.playerPosition = Position.builder().x(i).y(j).build();
                        grid[i][j] = SokobanTile.ground;
                        continue;
                    }

                    grid[i][j] = sokobanTile;
                }
            }

            sokobanLevel.cratePositions = cratePositions.toArray(Position[]::new);
            sokobanLevel.flagPositions = flagPositions.toArray(Position[]::new);
            sokobanLevel.sokobanTiles = grid;
        }

    }

}
