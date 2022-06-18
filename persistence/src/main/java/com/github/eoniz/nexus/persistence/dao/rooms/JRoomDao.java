package com.github.eoniz.nexus.persistence.dao.rooms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.eoniz.nexus.model.room.Room;
import com.github.eoniz.nexus.persistence.entity.rooms.RoomEntity;
import com.github.eoniz.nexus.persistence.entity.rooms.RoomEntityMapper;
import com.github.eoniz.nexus.persistence.redis.RedisHandler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class JRoomDao {

    private final static int MAX_TTL = 60 * 10;

    private final RedisHandler redisHandler = RedisHandler.getInstance();
    private final RoomEntityMapper roomEntityMapper = new RoomEntityMapper();


    public boolean save(Room room) {
        RoomEntity roomEntity = roomEntityMapper.of(room);
        Jedis jedisInstance = redisHandler.getJedisInstance();
        String jsonifiedRoom = toJson(roomEntity);
        String key = String.format("rooms#%s", roomEntity.getRoomId());
        String result = jedisInstance.set(key, jsonifiedRoom);
        jedisInstance.expire(key, MAX_TTL);

        if (!result.equals("OK")) {
            log.error("Error inserting room: " + result + " ; " + jsonifiedRoom);
            return false;
        }

        return true;
    }

    @SneakyThrows
    public Optional<Room> getRoomById(String id) {
        Jedis jedisInstance = redisHandler.getJedisInstance();
        String result = jedisInstance.get(String.format("rooms#%s", id));
        if (result == null || result.length() == 0) {
            return Optional.empty();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        RoomEntity roomEntity = objectMapper
                .readerFor(RoomEntity.class)
                .readValue(result);

        return Optional.of(roomEntityMapper.of(roomEntity));
    }

    public void destroy(String id) {
        Jedis jedisInstance = redisHandler.getJedisInstance();
        jedisInstance.del(String.format("rooms#%s", id));
    }

    @SneakyThrows
    public String toJson(RoomEntity roomEntity) {
        final ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(roomEntity);
    }
}
