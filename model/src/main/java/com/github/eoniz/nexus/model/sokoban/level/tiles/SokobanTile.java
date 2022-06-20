package com.github.eoniz.nexus.model.sokoban.level.tiles;

import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Builder
public class SokobanTile {

    public final static SokobanTile wall = SokobanTile.builder()
            .emoji(":blue_square:")
            .name("wall")
            .identifier('#')
            .build();

    public final static SokobanTile ground = SokobanTile.builder()
            .emoji(":black_large_square:")
            .name("ground")
            .identifier('.')
            .build();

    public final static SokobanTile player = SokobanTile.builder()
            .emoji(":disguised_face:")
            .name("player")
            .identifier('P')
            .build();

    public final static SokobanTile crate = SokobanTile.builder()
            .emoji(":brown_square:")
            .name("crate")
            .identifier('C')
            .build();

    public final static SokobanTile flag = SokobanTile.builder()
            .emoji(":triangular_flag_on_post:")
            .name("flag")
            .identifier('F')
            .build();

    private final char identifier;
    private final String emoji;
    private final String name;

    public static SokobanTile getFromIdentifier(char identifier) {
        List<SokobanTile> tiles = Arrays.stream(SokobanTile.class.getDeclaredFields())
                .filter(field -> {
                    if (!Modifier.isStatic(SokobanTile.class.getDeclaredFields()[0].getModifiers())) {
                        return false;
                    }

                    if (!Modifier.isPublic(SokobanTile.class.getDeclaredFields()[0].getModifiers())) {
                        return false;
                    }

                    return field.getType().equals(SokobanTile.class);
                })
                .map(c -> {
                    try {
                        return (SokobanTile) c.get(SokobanTile.class);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        Optional<SokobanTile> optTile = tiles.stream()
                .filter(tile -> tile.getIdentifier() == identifier)
                .findFirst();

        if (optTile.isEmpty()) {
            return SokobanTile.ground;
        }

        return optTile.get();
    }

}
