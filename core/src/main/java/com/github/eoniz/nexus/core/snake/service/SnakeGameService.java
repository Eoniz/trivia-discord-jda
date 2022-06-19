package com.github.eoniz.nexus.core.snake.service;

import com.github.eoniz.nexus.core.snake.exception.SnakeGameFinishedException;
import com.github.eoniz.nexus.core.snake.exception.SnakeHeadCollidesBodyException;
import com.github.eoniz.nexus.core.snake.exception.SnakeHeadCollidesWallException;
import com.github.eoniz.nexus.core.utils.RoomUtils;
import com.github.eoniz.nexus.model.common.Position;
import com.github.eoniz.nexus.model.snake.player.SnakePlayer;
import com.github.eoniz.nexus.model.snake.room.SnakeRoom;
import com.github.eoniz.nexus.persistence.dao.rooms.snake.SnakeRoomDao;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class SnakeGameService {

    private final SnakeRoomDao snakeRoomDao = new SnakeRoomDao();

    public SnakeRoom createRoom(SnakePlayer owner) {
        String roomId = RoomUtils.generateRoomId();

        SnakeRoom room = SnakeRoom.builder()
                .roomId(roomId)
                .applePosition(new Position(1, 1))
                .snake(List.of(new Position(5, 5)))
                .owner(owner)
                .build();

        Optional<Position> nextApplePosition = generateNextApplePosition(room);
        nextApplePosition.ifPresent(room::setApplePosition);

        return room;
    }

    public SnakeRoom save(SnakeRoom snakeRoom) {
        snakeRoomDao.save(snakeRoom);
        return snakeRoom;
    }

    public Optional<SnakeRoom> getSnakeRoomByMessageId(String messageId) {
        return snakeRoomDao.getRoomByMessageId(messageId);
    }

    public SnakeRoom moveRight(SnakeRoom snakeRoom)
            throws SnakeHeadCollidesBodyException, SnakeHeadCollidesWallException, SnakeGameFinishedException {
        Position head = snakeRoom.getSnake().get(0);
        List<Position> body = snakeRoom.getSnake().subList(1, snakeRoom.getSnake().size());

        Position nextHeadPosition = head.toBuilder()
                .x(head.getX() + 1)
                .y(head.getY())
                .build();

        boolean collidesWithApple = nextHeadPosition.equals(snakeRoom.getApplePosition());

        checkIfCollidesWithBody(body, nextHeadPosition);
        checkIfCollidesWithWalls(nextHeadPosition);

        Position[] nextPositions = generateNextPositions(snakeRoom, nextHeadPosition, collidesWithApple);
        snakeRoom.setSnake(List.of(nextPositions));

        handleGameFinished(snakeRoom, collidesWithApple);

        return snakeRoom;
    }

    private void handleGameFinished(SnakeRoom snakeRoom, boolean collidesWithApple) throws SnakeGameFinishedException {
        if (collidesWithApple) {
            Optional<Position> nextApplePosition = generateNextApplePosition(snakeRoom);
            if (nextApplePosition.isEmpty()) {
                throw new SnakeGameFinishedException();
            }

            snakeRoom.setApplePosition(nextApplePosition.get());
        }
    }

    public SnakeRoom moveLeft(SnakeRoom snakeRoom)
            throws SnakeHeadCollidesBodyException, SnakeHeadCollidesWallException, SnakeGameFinishedException {
        Position head = snakeRoom.getSnake().get(0);
        List<Position> body = snakeRoom.getSnake().subList(1, snakeRoom.getSnake().size());

        Position nextHeadPosition = head.toBuilder()
                .x(head.getX() - 1)
                .y(head.getY())
                .build();

        boolean collidesWithApple = nextHeadPosition.equals(snakeRoom.getApplePosition());

        checkIfCollidesWithBody(body, nextHeadPosition);
        checkIfCollidesWithWalls(nextHeadPosition);

        Position[] nextPositions = generateNextPositions(snakeRoom, nextHeadPosition, collidesWithApple);
        snakeRoom.setSnake(List.of(nextPositions));

        handleGameFinished(snakeRoom, collidesWithApple);

        return snakeRoom;
    }

    public SnakeRoom moveTop(SnakeRoom snakeRoom)
            throws SnakeHeadCollidesBodyException, SnakeHeadCollidesWallException, SnakeGameFinishedException {
        Position head = snakeRoom.getSnake().get(0);
        List<Position> body = snakeRoom.getSnake().subList(1, snakeRoom.getSnake().size());

        Position nextHeadPosition = head.toBuilder()
                .x(head.getX())
                .y(head.getY() - 1)
                .build();

        boolean collidesWithApple = nextHeadPosition.equals(snakeRoom.getApplePosition());

        checkIfCollidesWithBody(body, nextHeadPosition);
        checkIfCollidesWithWalls(nextHeadPosition);

        Position[] nextPositions = generateNextPositions(snakeRoom, nextHeadPosition, collidesWithApple);
        snakeRoom.setSnake(List.of(nextPositions));

        handleGameFinished(snakeRoom, collidesWithApple);

        return snakeRoom;
    }

    public SnakeRoom moveDown(SnakeRoom snakeRoom)
            throws SnakeHeadCollidesBodyException, SnakeHeadCollidesWallException, SnakeGameFinishedException {
        Position head = snakeRoom.getSnake().get(0);
        List<Position> body = snakeRoom.getSnake().subList(1, snakeRoom.getSnake().size());

        Position nextHeadPosition = head.toBuilder()
                .x(head.getX())
                .y(head.getY() + 1)
                .build();

        boolean collidesWithApple = nextHeadPosition.equals(snakeRoom.getApplePosition());

        checkIfCollidesWithBody(body, nextHeadPosition);
        checkIfCollidesWithWalls(nextHeadPosition);

        Position[] nextPositions = generateNextPositions(snakeRoom, nextHeadPosition, collidesWithApple);
        snakeRoom.setSnake(List.of(nextPositions));

        handleGameFinished(snakeRoom, collidesWithApple);

        return snakeRoom;
    }

    private Optional<Position> generateNextApplePosition(SnakeRoom snakeRoom) {
        List<Position> positions = new ArrayList<>();
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                positions.add(new Position(x, y));
            }
        }

        List<Position> filteredPositions = positions.stream()
                .filter(pos -> (
                        snakeRoom.getSnake().stream()
                                .noneMatch(bodyPos -> bodyPos.equals(pos))
                ))
                .collect(Collectors.toList());

        if (filteredPositions.size() == 0) {
            return Optional.empty();
        }

        int nextPositionIdx = (int) Math.round(Math.random() * (filteredPositions.size() - 1));
        return Optional.of(filteredPositions.get(nextPositionIdx));
    }

    private Position[] generateNextPositions(SnakeRoom snakeRoom, Position nextHeadPosition, boolean collidesWithApple) {
        Position[] nextPositions = new Position[snakeRoom.getSnake().size() + (collidesWithApple ? 1 : 0)];

        if (collidesWithApple) {
            nextPositions[nextPositions.length - 1] = snakeRoom.getSnake().get(snakeRoom.getSnake().size() - 1);
        }

        for (int i = snakeRoom.getSnake().size() - 1; i > 0; i--) {
            Position parent = snakeRoom.getSnake().get(i - 1);
            nextPositions[i] = (
                    Position.builder()
                            .x(parent.getX())
                            .y(parent.getY())
                            .build()
            );
        }
        nextPositions[0] = nextHeadPosition;
        return nextPositions;
    }

    private void checkIfCollidesWithBody(List<Position> body, Position nextHeadPosition) throws SnakeHeadCollidesBodyException {
        for (Position bodyPos : body) {
            if (bodyPos.equals(nextHeadPosition)) {
                throw new SnakeHeadCollidesBodyException();
            }
        }
    }

    private void checkIfCollidesWithWalls(Position nextHeadPosition) throws SnakeHeadCollidesWallException {
        if (nextHeadPosition.getX() < 0) {
            throw new SnakeHeadCollidesWallException();
        }

        if (nextHeadPosition.getY() < 0) {
            throw new SnakeHeadCollidesWallException();
        }

        if (nextHeadPosition.getX() >= 10) {
            throw new SnakeHeadCollidesWallException();
        }

        if (nextHeadPosition.getY() >= 10) {
            throw new SnakeHeadCollidesWallException();
        }
    }

    public void destroyRoom(String messageId) {
        snakeRoomDao.destroy(messageId);
    }
}
