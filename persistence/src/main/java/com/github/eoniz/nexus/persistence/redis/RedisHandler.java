package com.github.eoniz.nexus.persistence.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisHandler {

    private final JedisPool jedisPool;

    private RedisHandler() {
        jedisPool = new JedisPool("localhost", 6379);
    }

    public Jedis getJedisInstance() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis;
        }
    }

    private static RedisHandler _instance;

    public static RedisHandler getInstance() {
        if (_instance == null) {
            _instance = new RedisHandler();
        }

        return _instance;
    }
}
