package com.github.eoniz.nexus.core.trivia.game.service;

import com.github.eoniz.nexus.core.trivia.exceptions.*;
import com.github.eoniz.nexus.model.question.Question;
import com.github.eoniz.nexus.core.trivia.questions.QuestionsManager;
import com.github.eoniz.nexus.model.player.Player;
import com.github.eoniz.nexus.model.room.Room;
import com.github.eoniz.nexus.persistence.dao.rooms.JRoomDao;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class GameService {
    private final Integer MAX_ROOM_CHARS = 6;
    private final String[] ROOM_ALLOWED_CHARS = new String[]{
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
            "Y", "Z"
    };

    private final JRoomDao roomDao = new JRoomDao();

    public Room createRoom(Player owner, int numberOfQuestions) {
        List<Question> questions = QuestionsManager.getQuestions(numberOfQuestions);
        String roomId = generateRoomId();
        Room room = Room.builder()
                .roomId(roomId)
                .ownerId(owner.getId())
                .gameState(Room.GameState.LOBBY)
                .questions(questions)
                .build();

        room.addPlayer(owner);

        roomDao.save(room);
        return room;
    }

    public void joinRoom(String roomId, Player player)
            throws RoomDoesNotExistsException, AlreadyJoinedException {
        Optional<Room> optRoom = roomDao.getRoomById(roomId);
        if (optRoom.isEmpty()) {
            throw new RoomDoesNotExistsException();
        }

        Room room = optRoom.get();
        if (room.getPlayers().get(player.getId()) != null) {
            throw new AlreadyJoinedException();
        }

        room.addPlayer(player);
        roomDao.save(room);
    }

    public Question startGame(String roomId, Player player)
            throws RoomDoesNotExistsException, GameAlreadyStartedException, GameAlreadyFinishedException, OnlyOwnerCanStartGameException {
        Optional<Room> optRoom = roomDao.getRoomById(roomId);
        if (optRoom.isEmpty()) {
            throw new RoomDoesNotExistsException();
        }

        Room room = optRoom.get();
        if (room.getGameState() == Room.GameState.IN_GAME) {
            throw new GameAlreadyStartedException();
        }


        if (room.getGameState() == Room.GameState.FINISHED) {
            throw new GameAlreadyFinishedException();
        }

        if (!room.getOwnerId().equals(player.getId())) {
            throw new OnlyOwnerCanStartGameException();
        }

        room.startGame();
        roomDao.save(room);

        return room.getQuestions().get(room.getCurrentQuestionIdx());
    }

    public boolean answerToQuestion(String roomId, Player player, String questionId, String answer)
            throws RoomDoesNotExistsException, GameNotStartedException, GameAlreadyFinishedException, PlayerAlreadyAnsweredException, WrongQuestionAnsweredException {
        Optional<Room> optRoom = roomDao.getRoomById(roomId);
        if (optRoom.isEmpty()) {
            throw new RoomDoesNotExistsException();
        }

        Room room = optRoom.get();
        if (room.getGameState() == Room.GameState.LOBBY) {
            throw new GameNotStartedException();
        }

        if (room.getGameState() == Room.GameState.FINISHED) {
            throw new GameAlreadyFinishedException();
        }

        if (room.playerAlreadyAnswered(player)) {
            throw new PlayerAlreadyAnsweredException();
        }

        if (!room.hasActualQuestionGivenId(questionId)) {
            throw new WrongQuestionAnsweredException();
        }

        room.registerAnswer(player, answer);
        roomDao.save(room);

        return room.allPlayersAnswered();
    }

    public Map<String, String> getPlayerAnswers(String roomId) throws RoomDoesNotExistsException {
        Optional<Room> optRoom = roomDao.getRoomById(roomId);
        if (optRoom.isEmpty()) {
            throw new RoomDoesNotExistsException();
        }

        Room room = optRoom.get();
        return room.getReadablePlayerAnswers();
    }

    public Map<String, Integer> getReadableMapScores(String roomId) throws RoomDoesNotExistsException {
        Optional<Room> optRoom = roomDao.getRoomById(roomId);
        if (optRoom.isEmpty()) {
            throw new RoomDoesNotExistsException();
        }

        Room room = optRoom.get();
        return room.getReadableMapScore();
    }

    public Question getCurrentQuestion(String roomId) throws RoomDoesNotExistsException {
        Optional<Room> optRoom = roomDao.getRoomById(roomId);
        if (optRoom.isEmpty()) {
            throw new RoomDoesNotExistsException();
        }

        Room room = optRoom.get();
        return room.getCurrentQuestion();
    }

    public Optional<Question> nextQuestion(String roomId)
            throws RoomDoesNotExistsException {
        Optional<Room> optRoom = roomDao.getRoomById(roomId);
        if (optRoom.isEmpty()) {
            throw new RoomDoesNotExistsException();
        }

        Room room = optRoom.get();
        Optional<Question> question = room.nextQuestion();

        roomDao.save(room);

        return question;
    }

    private String generateRoomId() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < MAX_ROOM_CHARS; i++) {
            int charIndex = (int) Math.round(Math.random() * (ROOM_ALLOWED_CHARS.length - 1));
            stringBuilder.append(ROOM_ALLOWED_CHARS[charIndex]);
        }

        return stringBuilder.toString();
    }

    public void destroy(String roomId) {
        roomDao.destroy(roomId);
    }
}
