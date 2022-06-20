package com.github.eoniz.nexus.persistence.dao.rooms.sokoban;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.eoniz.nexus.model.snake.room.SnakeRoom;
import com.github.eoniz.nexus.model.sokoban.room.SokobanRoom;
import com.github.eoniz.nexus.persistence.entity.snake.room.SnakeRoomEntity;
import com.github.eoniz.nexus.persistence.entity.sokoban.room.SokobanRoomEntity;
import com.github.eoniz.nexus.persistence.entity.sokoban.room.SokobanRoomEntityMapper;
import com.github.eoniz.nexus.persistence.redis.RedisHandler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class SokobanRoomDao {
    private final static int MAX_TTL = 60 * 10;

    private final RedisHandler redisHandler = RedisHandler.getInstance();

    private final SokobanRoomEntityMapper sokobanRoomEntityMapper = new SokobanRoomEntityMapper();

    public boolean save(SokobanRoom sokobanRoom) {

        SokobanRoomEntity sokobanRoomEntity = sokobanRoomEntityMapper.of(sokobanRoom);

        Jedis jedisInstance = redisHandler.getJedisInstance();
        String jsonifiedRoom = toJson(sokobanRoomEntity);
        String key = formatRedisRoomQualifier(sokobanRoomEntity.getGameMessageId());
        String result = jedisInstance.set(key, jsonifiedRoom);
        jedisInstance.expire(key, MAX_TTL);

        if (!result.equals("OK")) {
            log.error("Error inserting room: " + result + " ; " + jsonifiedRoom);
            return false;
        }

        return true;
    }

    @SneakyThrows
    public Optional<SokobanRoom> getRoomByMessageId(String id) {
        Jedis jedisInstance = redisHandler.getJedisInstance();
        String result = jedisInstance.get(formatRedisRoomQualifier(id));
        if (result == null || result.length() == 0) {
            return Optional.empty();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        SokobanRoomEntity sokobanRoomEntity = objectMapper
                .readerFor(SokobanRoomEntity.class)
                .readValue(result);

        return Optional.of(sokobanRoomEntityMapper.of(sokobanRoomEntity));
    }

    private String formatRedisRoomQualifier(String id) {
        return String.format("rooms#sokoban#%s", id);
    }

    public void destroy(String id) {
        Jedis jedisInstance = redisHandler.getJedisInstance();
        jedisInstance.del(formatRedisRoomQualifier(id));
    }

    @SneakyThrows
    public String toJson(SokobanRoomEntity sokobanRoomEntity) {
        final ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(sokobanRoomEntity);
    }

}
