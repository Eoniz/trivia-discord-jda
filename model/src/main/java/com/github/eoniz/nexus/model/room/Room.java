package com.github.eoniz.nexus.model.room;

import com.github.eoniz.nexus.model.player.Player;
import com.github.eoniz.nexus.model.question.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Builder
public class Room {
    private final Map<String, Player> players = new HashMap<>();
    private final String roomId;
    private final String ownerId;
    private final List<Question> questions;

    @Builder.Default
    private Map<String, String> playerAnswers = new HashMap<>();

    @Builder.Default
    private Map<String, Integer> playerScores = new HashMap<>();

    @Builder.Default
    private Integer currentQuestionIdx = 0;

    private GameState gameState;

    public void addPlayer(Player player) {
        this.players.put(player.getId(), player);
    }

    public boolean playerAlreadyJoined(String playerId) {
        return players.get(playerId) != null;
    }

    public void startGame() {
        gameState = GameState.IN_GAME;

        for (Player player : players.values()) {
            playerScores.put(player.getId(), 0);
        }
    }

    public boolean playerAlreadyAnswered(Player player) {
        return playerAnswers.containsKey(player.getId());
    }

    public boolean hasActualQuestionGivenId(String questionId) {
        return questions.get(currentQuestionIdx).getId().toString().equals(questionId);
    }

    public void registerAnswer(Player player, String answer) {
        playerAnswers.put(player.getId(), answer);
    }

    public boolean allPlayersAnswered() {
        return playerAnswers.size() == players.size();
    }

    public Map<String, String> getReadablePlayerAnswers() {
        Map<String, String> readableAnswers = new HashMap<>();

        playerAnswers.forEach((playerId, answer) -> {
            if (!players.containsKey(playerId)) {
                return;
            }

            readableAnswers.put(players.get(playerId).getEffectiveName(), answer);
        });

        return readableAnswers;
    }

    public Question getCurrentQuestion() {
        return questions.get(currentQuestionIdx);
    }

    public Optional<Question> nextQuestion() {
        for (Player player : players.values()) {
            if (!playerAnswers.containsKey(player.getId()) || !playerScores.containsKey(player.getId())) {
                continue;
            }

            if (playerAnswers.get(player.getId()).equals(getCurrentQuestion().getAnswer())) {
                Integer lastScore = playerScores.get(player.getId());
                playerScores.put(player.getId(), lastScore + 1);
            }
        }

        playerAnswers.clear();
        currentQuestionIdx += 1;

        if (currentQuestionIdx >= questions.size()) {
            return Optional.empty();
        }

        return Optional.of(questions.get(currentQuestionIdx));
    }

    public Map<String, Integer> getReadableMapScore() {
        Map<String, Integer> scores = new HashMap<>();
        playerScores.forEach(((playerId, score) -> {
            if (!players.containsKey(playerId)) {
                return;
            }

            Player player = players.get(playerId);
            scores.put(player.getEffectiveName(), score);
        }));

        return scores;
    }

    @Getter
    @AllArgsConstructor
    public enum GameState {
        LOBBY("LOBBY"),
        IN_GAME("IN_GAME"),
        FINISHED("FINISHED");

        private final String label;

        public static GameState of(String value) {
            for (GameState state : GameState.values()) {
                if (state.label.equals(value)) {
                    return state;
                }
            }

            return GameState.FINISHED;
        }
    }
}
