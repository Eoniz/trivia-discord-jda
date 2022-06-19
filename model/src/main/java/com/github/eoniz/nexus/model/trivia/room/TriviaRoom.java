package com.github.eoniz.nexus.model.trivia.room;

import com.github.eoniz.nexus.model.trivia.player.TriviaPlayer;
import com.github.eoniz.nexus.model.trivia.question.TriviaQuestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Builder
public class TriviaRoom {
    private final Map<String, TriviaPlayer> players = new HashMap<>();
    private final String roomId;
    private final String ownerId;
    private final List<TriviaQuestion> triviaQuestions;

    @Builder.Default
    private Map<String, String> playerAnswers = new HashMap<>();

    @Builder.Default
    private Map<String, Integer> playerScores = new HashMap<>();

    @Builder.Default
    private Integer currentQuestionIdx = 0;

    private GameState gameState;

    public void addPlayer(TriviaPlayer triviaPlayer) {
        this.players.put(triviaPlayer.getId(), triviaPlayer);
    }

    public boolean playerAlreadyJoined(String playerId) {
        return players.get(playerId) != null;
    }

    public void startGame() {
        gameState = GameState.IN_GAME;

        for (TriviaPlayer triviaPlayer : players.values()) {
            playerScores.put(triviaPlayer.getId(), 0);
        }
    }

    public boolean playerAlreadyAnswered(TriviaPlayer triviaPlayer) {
        return playerAnswers.containsKey(triviaPlayer.getId());
    }

    public boolean hasActualQuestionGivenId(String questionId) {
        return triviaQuestions.get(currentQuestionIdx).getId().toString().equals(questionId);
    }

    public void registerAnswer(TriviaPlayer triviaPlayer, String answer) {
        playerAnswers.put(triviaPlayer.getId(), answer);
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

    public TriviaQuestion getCurrentQuestion() {
        return triviaQuestions.get(currentQuestionIdx);
    }

    public Optional<TriviaQuestion> nextQuestion() {
        for (TriviaPlayer triviaPlayer : players.values()) {
            if (!playerAnswers.containsKey(triviaPlayer.getId()) || !playerScores.containsKey(triviaPlayer.getId())) {
                continue;
            }

            if (playerAnswers.get(triviaPlayer.getId()).equals(getCurrentQuestion().getAnswer())) {
                Integer lastScore = playerScores.get(triviaPlayer.getId());
                playerScores.put(triviaPlayer.getId(), lastScore + 1);
            }
        }

        playerAnswers.clear();
        currentQuestionIdx += 1;

        if (currentQuestionIdx >= triviaQuestions.size()) {
            return Optional.empty();
        }

        return Optional.of(triviaQuestions.get(currentQuestionIdx));
    }

    public Map<String, Integer> getReadableMapScore() {
        Map<String, Integer> scores = new HashMap<>();
        playerScores.forEach(((playerId, score) -> {
            if (!players.containsKey(playerId)) {
                return;
            }

            TriviaPlayer triviaPlayer = players.get(playerId);
            scores.put(triviaPlayer.getEffectiveName(), score);
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
