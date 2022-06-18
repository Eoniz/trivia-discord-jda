package com.github.eoniz.nexus.persistence.entity.rooms;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.eoniz.nexus.persistence.entity.players.PlayerEntity;
import com.github.eoniz.nexus.persistence.entity.questions.QuestionEntity;
import lombok.*;

import java.util.Collection;
import java.util.Map;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomEntity {
    @JsonProperty("ownerId")
    private String ownerId;
    @JsonProperty("roomId")
    private String roomId;
    @JsonProperty("players")
    private Collection<PlayerEntity> players;
    @JsonProperty("gameState")
    private String gameState;
    @JsonProperty("questions")
    private Collection<QuestionEntity> questions;
    @JsonProperty("currentQuestionIdx")
    private Integer currentQuestionIdx;
    @JsonProperty("playerAnswers")
    private Map<String, String> playerAnswers;
    @JsonProperty("playerScores")
    private Map<String, Integer> playerScores;
}
