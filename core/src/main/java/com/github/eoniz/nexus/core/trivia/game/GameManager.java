package com.github.eoniz.nexus.core.trivia.game;

import com.github.eoniz.nexus.model.room.Room;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class GameManager {

    private static GameManager _instance;

    private final Map<String, Room> roomsMapping = new HashMap<String, Room>();

    public static GameManager getInstance() {
        if (_instance == null) {
            _instance = new GameManager();
        }

        return _instance;
    }

    public Optional<Room> getRoomById(String id) {
        return Optional.ofNullable(roomsMapping.get(id));
    }


    public void closeRoom(String roomId) {
        roomsMapping.remove(roomId);
    }
}
