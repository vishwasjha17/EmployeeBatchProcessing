package utils.redisUtils;

import constants.AppMetrics;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisClientPool {
    private static JedisPool jedisPool;
    private static final Object SingleTonLock = new Object();
    private volatile static RedisClientPool redisClientPool = null;

    private RedisClientPool(){
            initRedisConfig();
    }

    private static void initRedisConfig(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(AppMetrics.REDIS_MAX_TOTAL_CONNECTIONS);
        poolConfig.setMaxIdle(AppMetrics.REDIS_MAX_IDLE_CONNECTIONS);
        poolConfig.setMinIdle(AppMetrics.REDIS_MIN_IDLE_CONNECTIONS);
        jedisPool = new JedisPool(poolConfig, AppMetrics.REDIS_HOST, AppMetrics.REDIS_PORT);
    }

    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    public static RedisClientPool getInstance() {
        if (redisClientPool == null) {
            synchronized (SingleTonLock) {
                if (redisClientPool == null) {
                    System.out.println("!! Object is created once");
                    redisClientPool = new RedisClientPool();
                }
            }
        }
        return redisClientPool;
    }

    public void releaseJedis(Jedis jedis) {
        jedis.close();
    }

    public  void shutdown() {
        jedisPool.close();
    }

}
