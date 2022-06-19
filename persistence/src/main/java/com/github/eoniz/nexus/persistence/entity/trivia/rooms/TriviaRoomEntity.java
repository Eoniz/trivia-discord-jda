package com.github.eoniz.nexus.persistence.entity.trivia.rooms;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.eoniz.nexus.persistence.entity.trivia.players.TriviaPlayerEntity;
import com.github.eoniz.nexus.persistence.entity.trivia.questions.TriviaQuestionEntity;
import lombok.*;

import java.util.Collection;
import java.util.Map;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriviaRoomEntity {
    @JsonProperty("ownerId")
    private String ownerId;
    @JsonProperty("roomId")
    private String roomId;
    @JsonProperty("players")
    private Collection<TriviaPlayerEntity> players;
    @JsonProperty("gameState")
    private String gameState;
    @JsonProperty("questions")
    private Collection<TriviaQuestionEntity> questions;
    @JsonProperty("currentQuestionIdx")
    private Integer currentQuestionIdx;
    @JsonProperty("playerAnswers")
    private Map<String, String> playerAnswers;
    @JsonProperty("playerScores")
    private Map<String, Integer> playerScores;
}
