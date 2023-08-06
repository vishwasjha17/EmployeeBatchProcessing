package service.redisservice;


import constants.AppMetrics;
import constants.Status;
import entity.RedisEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import utils.redisUtils.RedisClientPool;

import java.util.ArrayList;
import java.util.List;


public class RedisService {
      private static final Logger log =  LoggerFactory.getLogger(RedisService.class);
      private static final Integer MAX_RETRY = 3;
      public static void batchUpdate(List<RedisEntity> redisSetEntities){
            Integer redisRetryCounter = 0;
            Boolean redisSuccess = false;
            log.info("\u001B[32m" + String.format("[[REDIS_REQUEST]] :: During Batch Update to Redis In size :: {%d}",redisSetEntities.size()) + "\u001B[0m");
            if(redisSetEntities.isEmpty()) {
                  log.info("\u001B[32m" + String.format("[[REDIS_REQUEST]] :: Empty Request Rejected :: {%d}",redisSetEntities.size()) + "\u001B[0m");
                  return ;
            }
            log.info("\u001B[32m"+ String.format("[[REDIS RETRY COUNT BATCH UPDATE]] FOR SUCCESS COUNTER ::{%d}",redisRetryCounter));

            while(!redisSuccess && redisRetryCounter++< MAX_RETRY) {
                  try {
                        Jedis jedis = null;
                        jedis = RedisClientPool.getInstance().getJedis();
                        Pipeline pipeline = jedis.pipelined();
                        for (RedisEntity rdEntity : redisSetEntities) {
                              pipeline.set(rdEntity.getRedisKey(), rdEntity.getStatus());
                        }
                        pipeline.sync();
                        jedis.close();
                        log.info("\u001B[32m" + String.format("BATCH Update Status From Redis :: [[%s]", "SUCCESS") + "\u001B[0m");
                  } catch (Exception ex) {
                        ex.printStackTrace();
                        log.error("\u001B[31m" + String.format("[[REDIS_ERROR]] :: During Batch Update to Redis message {%s}", ex.getMessage()) + "\u001B[0m");
                  }
            }
      }



      public static String getKey(String redisKey){
            log.info("\u001B[32m" + String.format("[[REDIS_REQUEST]] :: lookup For Redis Key:: {%s}",redisKey) + "\u001B[0m");
            try (Jedis jedis = RedisClientPool.getInstance().getJedis()) {
                  return jedis.get(redisKey);
            } catch (Exception ex) {
                  ex.printStackTrace();
                  log.error("\u001B[31m" + String.format("[[ERROR]] :: During Batch Update to Redis message {%s}", ex.getMessage()) + "\u001B[0m");
            }
            return null;
      }


}
