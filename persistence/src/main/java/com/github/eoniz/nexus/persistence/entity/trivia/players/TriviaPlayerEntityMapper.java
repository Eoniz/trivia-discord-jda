package com.github.eoniz.nexus.persistence.entity.trivia.players;

import com.github.eoniz.nexus.model.trivia.player.TriviaPlayer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TriviaPlayerEntityMapper {
    public TriviaPlayerEntity of(TriviaPlayer triviaPlayer) {
        return TriviaPlayerEntity.builder()
                .id(triviaPlayer.getId())
                .effectiveName(triviaPlayer.getEffectiveName())
                .build();
    }

    public TriviaPlayer of(TriviaPlayerEntity player) {
        return TriviaPlayer.builder()
                .id(player.getId())
                .effectiveName(player.getEffectiveName())
                .build();
    }
}
