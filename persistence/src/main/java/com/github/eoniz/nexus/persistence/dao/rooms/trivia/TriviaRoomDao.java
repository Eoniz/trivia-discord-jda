package com.github.eoniz.nexus.persistence.dao.rooms.trivia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.eoniz.nexus.model.trivia.room.TriviaRoom;
import com.github.eoniz.nexus.persistence.entity.trivia.rooms.TriviaRoomEntity;
import com.github.eoniz.nexus.persistence.entity.trivia.rooms.TriviaRoomEntityMapper;
import com.github.eoniz.nexus.persistence.redis.RedisHandler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class TriviaRoomDao {

    private final static int MAX_TTL = 60 * 10;

    private final RedisHandler redisHandler = RedisHandler.getInstance();
    private final TriviaRoomEntityMapper triviaRoomEntityMapper = new TriviaRoomEntityMapper();


    public boolean save(TriviaRoom triviaRoom) {
        TriviaRoomEntity triviaRoomEntity = triviaRoomEntityMapper.of(triviaRoom);
        Jedis jedisInstance = redisHandler.getJedisInstance();
        String jsonifiedRoom = toJson(triviaRoomEntity);
        String key = formatRedisRoomQualifier(triviaRoomEntity.getRoomId());
        String result = jedisInstance.set(key, jsonifiedRoom);
        jedisInstance.expire(key, MAX_TTL);

        if (!result.equals("OK")) {
            log.error("Error inserting room: " + result + " ; " + jsonifiedRoom);
            return false;
        }

        return true;
    }

    @SneakyThrows
    public Optional<TriviaRoom> getRoomById(String id) {
        Jedis jedisInstance = redisHandler.getJedisInstance();
        String result = jedisInstance.get(formatRedisRoomQualifier(id));
        if (result == null || result.length() == 0) {
            return Optional.empty();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        TriviaRoomEntity triviaRoomEntity = objectMapper
                .readerFor(TriviaRoomEntity.class)
                .readValue(result);

        return Optional.of(triviaRoomEntityMapper.of(triviaRoomEntity));
    }

    private String formatRedisRoomQualifier(String id) {
        return String.format("rooms#trivia#%s", id);
    }

    public void destroy(String id) {
        Jedis jedisInstance = redisHandler.getJedisInstance();
        jedisInstance.del(formatRedisRoomQualifier(id));
    }

    @SneakyThrows
    public String toJson(TriviaRoomEntity triviaRoomEntity) {
        final ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(triviaRoomEntity);
    }
}
