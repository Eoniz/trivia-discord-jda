package com.github.eoniz.nexus.persistence.entity.rooms;

import com.github.eoniz.nexus.model.player.Player;
import com.github.eoniz.nexus.model.question.Question;
import com.github.eoniz.nexus.model.room.Room;
import com.github.eoniz.nexus.persistence.entity.players.PlayerEntity;
import com.github.eoniz.nexus.persistence.entity.players.PlayerEntityMapper;
import com.github.eoniz.nexus.persistence.entity.questions.QuestionEntity;
import com.github.eoniz.nexus.persistence.entity.questions.QuestionEntityMapper;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class RoomEntityMapper {
    private final PlayerEntityMapper playerEntityMapper = new PlayerEntityMapper();
    private final QuestionEntityMapper questionEntityMapper = new QuestionEntityMapper();

    public RoomEntity of(Room room) {
        List<PlayerEntity> playerEntities = room.getPlayers()
                .values()
                .stream()
                .map(playerEntityMapper::of)
                .collect(Collectors.toList());

        List<QuestionEntity> questions = room.getQuestions().stream()
                .map(questionEntityMapper::of)
                .collect(Collectors.toList());

        return RoomEntity.builder()
                .ownerId(room.getOwnerId())
                .roomId(room.getRoomId())
                .players(playerEntities)
                .gameState(room.getGameState().getLabel())
                .questions(questions)
                .currentQuestionIdx(room.getCurrentQuestionIdx())
                .playerScores(room.getPlayerScores())
                .playerAnswers(room.getPlayerAnswers())
                .build();
    }

    public Room of(RoomEntity roomEntity) {
        List<Player> players = roomEntity.getPlayers().stream()
                .map(playerEntityMapper::of)
                .collect(Collectors.toList());

        List<Question> questions = roomEntity.getQuestions().stream()
                .map(questionEntityMapper::of)
                .collect(Collectors.toList());

        Room room = Room.builder()
                .roomId(roomEntity.getRoomId())
                .ownerId(roomEntity.getOwnerId())
                .gameState(Room.GameState.of(roomEntity.getGameState()))
                .currentQuestionIdx(roomEntity.getCurrentQuestionIdx())
                .playerAnswers(roomEntity.getPlayerAnswers())
                .playerScores(roomEntity.getPlayerScores())
                .questions(questions)
                .build();

        players.forEach(room::addPlayer);

        return room;
    }

}
