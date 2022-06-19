package com.github.eoniz.nexus.persistence.dao.rooms.snake;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.eoniz.nexus.model.snake.room.SnakeRoom;
import com.github.eoniz.nexus.persistence.entity.snake.room.SnakeRoomEntity;
import com.github.eoniz.nexus.persistence.entity.snake.room.SnakeRoomEntityMapper;
import com.github.eoniz.nexus.persistence.redis.RedisHandler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class SnakeRoomDao {

    private final static int MAX_TTL = 60 * 10;

    private final RedisHandler redisHandler = RedisHandler.getInstance();
    private final SnakeRoomEntityMapper snakeRoomEntityMapper = new SnakeRoomEntityMapper();


    public boolean save(SnakeRoom snakeRoom) {

        SnakeRoomEntity snakeRoomEntity = snakeRoomEntityMapper.of(snakeRoom);

        Jedis jedisInstance = redisHandler.getJedisInstance();
        String jsonifiedRoom = toJson(snakeRoomEntity);
        String key = formatRedisRoomQualifier(snakeRoomEntity.getMessageId());
        String result = jedisInstance.set(key, jsonifiedRoom);
        jedisInstance.expire(key, MAX_TTL);

        if (!result.equals("OK")) {
            log.error("Error inserting room: " + result + " ; " + jsonifiedRoom);
            return false;
        }

        return true;
    }

    @SneakyThrows
    public Optional<SnakeRoom> getRoomByMessageId(String id) {
        Jedis jedisInstance = redisHandler.getJedisInstance();
        String result = jedisInstance.get(formatRedisRoomQualifier(id));
        if (result == null || result.length() == 0) {
            return Optional.empty();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        SnakeRoomEntity snakeRoomEntity = objectMapper
                .readerFor(SnakeRoomEntity.class)
                .readValue(result);

        return Optional.of(snakeRoomEntityMapper.of(snakeRoomEntity));
    }

    private String formatRedisRoomQualifier(String id) {
        return String.format("rooms#snake#%s", id);
    }

    public void destroy(String id) {
        Jedis jedisInstance = redisHandler.getJedisInstance();
        jedisInstance.del(formatRedisRoomQualifier(id));
    }

    @SneakyThrows
    public String toJson(SnakeRoomEntity snakeRoomEntity) {
        final ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(snakeRoomEntity);
    }
}
