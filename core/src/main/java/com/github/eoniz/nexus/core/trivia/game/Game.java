package com.github.eoniz.nexus.core.trivia.game;

import com.github.eoniz.nexus.model.question.Question;
import com.github.eoniz.nexus.core.trivia.questions.QuestionsManager;
import com.github.eoniz.nexus.model.player.Player;
import com.github.eoniz.nexus.model.room.Room;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
public class Game {

    private GameState gameState;
    private List<Question> questions;
    private Integer currentQuestion;
    private Map<String, String> answersPerPlayers;
    private Map<String, Integer> playerScores;
    private Room room;

    public Game() {
        gameState = GameState.JOINING;
        questions = QuestionsManager.getQuestions(10);
        currentQuestion = 0;
        answersPerPlayers = new HashMap<>();
        playerScores = new HashMap<>();
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void startGame() {
        gameState = GameState.STARTED;

        room.getPlayers().forEach((playerId, player) -> {
            playerScores.put(playerId, 0);
        });
    }

    public Question getCurrentQuestion() {
        return questions.get(currentQuestion);
    }

    public String toStringQuestionCounter() {
        return (currentQuestion + 1) + " / " + questions.size();
    }

    public boolean nextQuestion() {
        answersPerPlayers.forEach((playerId, answer) -> {
            if (answer.equals(questions.get(currentQuestion).getAnswer())) {
                Integer score = playerScores.get(playerId);
                if (score == null) {
                    return;
                }

                playerScores.put(playerId, score + 1);
            }
        });

        currentQuestion += 1;
        answersPerPlayers = new HashMap<>();
        return currentQuestion < questions.size();
    }

    public boolean registerAnswerForPlayerId(String playerId, String answer) {
        this.answersPerPlayers.put(playerId, answer);

        log.info(
                String.format(
                        "answers size: %s | players size: %s",
                        answersPerPlayers.size(),
                        room.getPlayers().size()
                )
        );

        return this.answersPerPlayers.size() == this.room.getPlayers().size();
    }

    public Map<String, String> getPlayerAnswers() {
        Map<String, String> answers = new HashMap<>();
        answersPerPlayers.forEach(((playerId, answer) -> {
            Player player = room.getPlayers().get(playerId);
            if (player == null) {
                return;
            }

            answers.put(player.getEffectiveName(), answer);
        }));

        return answers;
    }

    public Map<String, Integer> getReadableMapScore() {
        Map<String, Integer> scores = new HashMap<>();
        playerScores.forEach(((playerId, score) -> {
            Player player = room.getPlayers().get(playerId);
            if (player == null) {
                return;
            }

            scores.put(player.getEffectiveName(), score);
        }));

        return scores;
    }

    public enum GameState {
        JOINING,
        STARTED,
        FINISHED
    }
}
