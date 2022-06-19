package com.github.eoniz.nexus.core.trivia.game.service;

import com.github.eoniz.nexus.core.trivia.exceptions.*;
import com.github.eoniz.nexus.core.utils.RoomUtils;
import com.github.eoniz.nexus.model.trivia.question.TriviaQuestion;
import com.github.eoniz.nexus.core.trivia.questions.TriviaQuestionsManager;
import com.github.eoniz.nexus.model.trivia.player.TriviaPlayer;
import com.github.eoniz.nexus.model.trivia.room.TriviaRoom;
import com.github.eoniz.nexus.persistence.dao.rooms.trivia.TriviaRoomDao;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class TriviaGameService {

    private final TriviaRoomDao triviaRoomDao = new TriviaRoomDao();

    public TriviaRoom createRoom(TriviaPlayer owner, int numberOfQuestions) {
        List<TriviaQuestion> triviaQuestions = TriviaQuestionsManager.getTRIVIA_QUESTIONS(numberOfQuestions);
        String roomId = RoomUtils.generateRoomId();
        TriviaRoom triviaRoom = TriviaRoom.builder()
                .roomId(roomId)
                .ownerId(owner.getId())
                .gameState(TriviaRoom.GameState.LOBBY)
                .triviaQuestions(triviaQuestions)
                .build();

        triviaRoom.addPlayer(owner);

        triviaRoomDao.save(triviaRoom);
        return triviaRoom;
    }

    public void joinRoom(String roomId, TriviaPlayer triviaPlayer)
            throws TriviaRoomDoesNotExistsException, TriviaAlreadyJoinedException {
        Optional<TriviaRoom> optRoom = triviaRoomDao.getRoomById(roomId);
        if (optRoom.isEmpty()) {
            throw new TriviaRoomDoesNotExistsException();
        }

        TriviaRoom triviaRoom = optRoom.get();
        if (triviaRoom.getPlayers().get(triviaPlayer.getId()) != null) {
            throw new TriviaAlreadyJoinedException();
        }

        triviaRoom.addPlayer(triviaPlayer);
        triviaRoomDao.save(triviaRoom);
    }

    public TriviaQuestion startGame(String roomId, TriviaPlayer triviaPlayer)
            throws TriviaRoomDoesNotExistsException, TriviaGameAlreadyStartedException, TriviaGameAlreadyFinishedException, TriviaOnlyOwnerCanStartGameException {
        Optional<TriviaRoom> optRoom = triviaRoomDao.getRoomById(roomId);
        if (optRoom.isEmpty()) {
            throw new TriviaRoomDoesNotExistsException();
        }

        TriviaRoom triviaRoom = optRoom.get();
        if (triviaRoom.getGameState() == TriviaRoom.GameState.IN_GAME) {
            throw new TriviaGameAlreadyStartedException();
        }


        if (triviaRoom.getGameState() == TriviaRoom.GameState.FINISHED) {
            throw new TriviaGameAlreadyFinishedException();
        }

        if (!triviaRoom.getOwnerId().equals(triviaPlayer.getId())) {
            throw new TriviaOnlyOwnerCanStartGameException();
        }

        triviaRoom.startGame();
        triviaRoomDao.save(triviaRoom);

        return triviaRoom.getTriviaQuestions().get(triviaRoom.getCurrentQuestionIdx());
    }

    public boolean answerToQuestion(String roomId, TriviaPlayer triviaPlayer, String questionId, String answer)
            throws TriviaRoomDoesNotExistsException, TriviaGameNotStartedException, TriviaGameAlreadyFinishedException, TriviaPlayerAlreadyAnsweredException, TriviaWrongQuestionAnsweredException {
        Optional<TriviaRoom> optRoom = triviaRoomDao.getRoomById(roomId);
        if (optRoom.isEmpty()) {
            throw new TriviaRoomDoesNotExistsException();
        }

        TriviaRoom triviaRoom = optRoom.get();
        if (triviaRoom.getGameState() == TriviaRoom.GameState.LOBBY) {
            throw new TriviaGameNotStartedException();
        }

        if (triviaRoom.getGameState() == TriviaRoom.GameState.FINISHED) {
            throw new TriviaGameAlreadyFinishedException();
        }

        if (triviaRoom.playerAlreadyAnswered(triviaPlayer)) {
            throw new TriviaPlayerAlreadyAnsweredException();
        }

        if (!triviaRoom.hasActualQuestionGivenId(questionId)) {
            throw new TriviaWrongQuestionAnsweredException();
        }

        triviaRoom.registerAnswer(triviaPlayer, answer);
        triviaRoomDao.save(triviaRoom);

        return triviaRoom.allPlayersAnswered();
    }

    public Map<String, String> getPlayerAnswers(String roomId) throws TriviaRoomDoesNotExistsException {
        Optional<TriviaRoom> optRoom = triviaRoomDao.getRoomById(roomId);
        if (optRoom.isEmpty()) {
            throw new TriviaRoomDoesNotExistsException();
        }

        TriviaRoom triviaRoom = optRoom.get();
        return triviaRoom.getReadablePlayerAnswers();
    }

    public Map<String, Integer> getReadableMapScores(String roomId) throws TriviaRoomDoesNotExistsException {
        Optional<TriviaRoom> optRoom = triviaRoomDao.getRoomById(roomId);
        if (optRoom.isEmpty()) {
            throw new TriviaRoomDoesNotExistsException();
        }

        TriviaRoom triviaRoom = optRoom.get();
        return triviaRoom.getReadableMapScore();
    }

    public TriviaQuestion getCurrentQuestion(String roomId) throws TriviaRoomDoesNotExistsException {
        Optional<TriviaRoom> optRoom = triviaRoomDao.getRoomById(roomId);
        if (optRoom.isEmpty()) {
            throw new TriviaRoomDoesNotExistsException();
        }

        TriviaRoom triviaRoom = optRoom.get();
        return triviaRoom.getCurrentQuestion();
    }

    public Optional<TriviaQuestion> nextQuestion(String roomId)
            throws TriviaRoomDoesNotExistsException {
        Optional<TriviaRoom> optRoom = triviaRoomDao.getRoomById(roomId);
        if (optRoom.isEmpty()) {
            throw new TriviaRoomDoesNotExistsException();
        }

        TriviaRoom triviaRoom = optRoom.get();
        Optional<TriviaQuestion> question = triviaRoom.nextQuestion();

        triviaRoomDao.save(triviaRoom);

        return question;
    }

    public void destroy(String roomId) {
        triviaRoomDao.destroy(roomId);
    }
}
