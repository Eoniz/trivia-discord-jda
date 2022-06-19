package com.github.eoniz.nexus.persistence.redis;

import com.github.eoniz.nexus.persistence.config.PropertiesLoader;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisHandler {

    private final JedisPool jedisPool;
    private final String password;

    private RedisHandler() {
        String url = PropertiesLoader.getInstance().getProperty("redis.url");
        int port = Integer.parseInt(PropertiesLoader.getInstance().getProperty("redis.port"));
        jedisPool = new JedisPool(url, port);

        this.password = PropertiesLoader.getInstance().getProperty("redis.password");
    }

    public Jedis getJedisInstance() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.auth(password);
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
