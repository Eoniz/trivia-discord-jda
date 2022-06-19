package com.github.eoniz.nexus.persistence.dao.rooms.connectfour;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.eoniz.nexus.model.connectfour.room.ConnectFourRoom;
import com.github.eoniz.nexus.model.snake.room.SnakeRoom;
import com.github.eoniz.nexus.persistence.entity.connectfour.room.ConnectFourRoomEntity;
import com.github.eoniz.nexus.persistence.entity.connectfour.room.ConnectFourRoomEntityMapper;
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
public class ConnectFourRoomDao {

    private final static int MAX_TTL = 60 * 10;

    private final RedisHandler redisHandler = RedisHandler.getInstance();
    private final ConnectFourRoomEntityMapper connectFourRoomEntityMapper = new ConnectFourRoomEntityMapper();


    public boolean save(ConnectFourRoom connectFourRoom) {

        ConnectFourRoomEntity connectFourRoomEntity = connectFourRoomEntityMapper.of(connectFourRoom);

        Jedis jedisInstance = redisHandler.getJedisInstance();
        String jsonifiedRoom = toJson(connectFourRoomEntity);
        String key = formatRedisRoomQualifier(connectFourRoomEntity.getMessageId());
        String result = jedisInstance.set(key, jsonifiedRoom);
        jedisInstance.expire(key, MAX_TTL);

        if (!result.equals("OK")) {
            log.error("Error inserting room: " + result + " ; " + jsonifiedRoom);
            return false;
        }

        return true;
    }

    @SneakyThrows
    public Optional<ConnectFourRoom> getRoomByMessageId(String id) {
        Jedis jedisInstance = redisHandler.getJedisInstance();
        String result = jedisInstance.get(formatRedisRoomQualifier(id));
        if (result == null || result.length() == 0) {
            return Optional.empty();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ConnectFourRoomEntity connectFourRoomEntity = objectMapper
                .readerFor(ConnectFourRoomEntity.class)
                .readValue(result);

        return Optional.of(connectFourRoomEntityMapper.of(connectFourRoomEntity));
    }

    private String formatRedisRoomQualifier(String id) {
        return String.format("rooms#connect4#%s", id);
    }

    public void destroy(String id) {
        Jedis jedisInstance = redisHandler.getJedisInstance();
        jedisInstance.del(formatRedisRoomQualifier(id));
    }

    @SneakyThrows
    public String toJson(ConnectFourRoomEntity connectFourRoomEntity) {
        final ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(connectFourRoomEntity);
    }
}
