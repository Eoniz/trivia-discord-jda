package com.github.eoniz.nexus.core.sokoban.service;

import com.github.eoniz.nexus.model.sokoban.level.SokobanLevel;

import java.util.Arrays;
import java.util.Optional;

public class SokobanLevelService {

    private final SokobanLevel[] levels = new SokobanLevel[] {
            SokobanLevel.baseBuilder()
                    .id("1")
                    .label("level-1")
                    .level(
                            "##########\n" +
                            "#......F.#\n" +
                            "#.C.C....#\n" +
                            "#........#\n" +
                            "#....P...#\n" +
                            "#........#\n" +
                            "##########"
                    )
                    .build()
    };

    public Optional<SokobanLevel> getSokobanLevelByLabel(String label) {
        return Arrays.stream(levels)
                .filter(level -> level.getLabel().equals(label))
                .findFirst();
    }

}
