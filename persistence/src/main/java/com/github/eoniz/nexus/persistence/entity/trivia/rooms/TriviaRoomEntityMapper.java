package com.github.eoniz.nexus.persistence.entity.trivia.rooms;

import com.github.eoniz.nexus.model.trivia.player.TriviaPlayer;
import com.github.eoniz.nexus.model.trivia.question.TriviaQuestion;
import com.github.eoniz.nexus.model.trivia.room.TriviaRoom;
import com.github.eoniz.nexus.persistence.entity.trivia.players.TriviaPlayerEntity;
import com.github.eoniz.nexus.persistence.entity.trivia.players.TriviaPlayerEntityMapper;
import com.github.eoniz.nexus.persistence.entity.trivia.questions.TriviaQuestionEntity;
import com.github.eoniz.nexus.persistence.entity.trivia.questions.TriviaQuestionEntityMapper;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class TriviaRoomEntityMapper {
    private final TriviaPlayerEntityMapper triviaPlayerEntityMapper = new TriviaPlayerEntityMapper();
    private final TriviaQuestionEntityMapper triviaQuestionEntityMapper = new TriviaQuestionEntityMapper();

    public TriviaRoomEntity of(TriviaRoom triviaRoom) {
        List<TriviaPlayerEntity> playerEntities = triviaRoom.getPlayers()
                .values()
                .stream()
                .map(triviaPlayerEntityMapper::of)
                .collect(Collectors.toList());

        List<TriviaQuestionEntity> questions = triviaRoom.getTriviaQuestions().stream()
                .map(triviaQuestionEntityMapper::of)
                .collect(Collectors.toList());

        return TriviaRoomEntity.builder()
                .ownerId(triviaRoom.getOwnerId())
                .roomId(triviaRoom.getRoomId())
                .players(playerEntities)
                .gameState(triviaRoom.getGameState().getLabel())
                .questions(questions)
                .currentQuestionIdx(triviaRoom.getCurrentQuestionIdx())
                .playerScores(triviaRoom.getPlayerScores())
                .playerAnswers(triviaRoom.getPlayerAnswers())
                .build();
    }

    public TriviaRoom of(TriviaRoomEntity triviaRoomEntity) {
        List<TriviaPlayer> triviaPlayers = triviaRoomEntity.getPlayers().stream()
                .map(triviaPlayerEntityMapper::of)
                .collect(Collectors.toList());

        List<TriviaQuestion> triviaQuestions = triviaRoomEntity.getQuestions().stream()
                .map(triviaQuestionEntityMapper::of)
                .collect(Collectors.toList());

        TriviaRoom triviaRoom = TriviaRoom.builder()
                .roomId(triviaRoomEntity.getRoomId())
                .ownerId(triviaRoomEntity.getOwnerId())
                .gameState(TriviaRoom.GameState.of(triviaRoomEntity.getGameState()))
                .currentQuestionIdx(triviaRoomEntity.getCurrentQuestionIdx())
                .playerAnswers(triviaRoomEntity.getPlayerAnswers())
                .playerScores(triviaRoomEntity.getPlayerScores())
                .triviaQuestions(triviaQuestions)
                .build();

        triviaPlayers.forEach(triviaRoom::addPlayer);

        return triviaRoom;
    }

}
