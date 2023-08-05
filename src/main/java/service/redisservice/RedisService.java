package service.redisservice;


import constants.AppMetrics;
import constants.Status;
import entity.RedisEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import utils.redisUtils.RedisClientPool;

import java.util.ArrayList;
import java.util.List;


public class RedisService {
      private static final Logger log =  LoggerFactory.getLogger(RedisService.class);
      public static void batchUpdate(List<RedisEntity> redisSetEntities){
            log.info("\u001B[32m" + String.format("[[REDIS_REQUEST]] :: During Batch Update to Redis In size :: {%d}",redisSetEntities.size()) + "\u001B[0m");
            if(redisSetEntities.isEmpty()) {
                  log.info("\u001B[32m" + String.format("[[REDIS_REQUEST]] :: Empty Request Rejected :: {%d}",redisSetEntities.size()) + "\u001B[0m");
                  return;
            }
            try (Jedis jedis = RedisClientPool.getInstance().getJedis()) {
                  Pipeline pipeline = jedis.pipelined();
                  for (RedisEntity rdEntity : redisSetEntities) {
                        pipeline.set(rdEntity.getRedisKey(), rdEntity.getStatus());
                  }
                  pipeline.sync();
                  log.info("\u001B[32m" + String.format("[[REDIS_SUCCESS]] :: During Batch Update to Redis In size :: {%d}",redisSetEntities.size()) + "\u001B[0m");
            } catch (Exception ex) {
                  ex.printStackTrace();
                  log.error("\u001B[31m" + String.format("[[REDIS_ERROR]] :: During Batch Update to Redis message {%s}", "vikram ") + "\u001B[0m");
            }
      }

      public static String getKey(String redisKey){
            log.info("\u001B[32m" + String.format("[[REDIS_REQUEST]] :: lookup For Redis Key:: {%s}",redisKey) + "\u001B[0m");
            try (Jedis jedis = RedisClientPool.getInstance().getJedis()) {
                  return jedis.get(redisKey);
            } catch (Exception ex) {
                  ex.printStackTrace();
                  log.error("\u001B[31m" + String.format("[[ERROR]] :: During Batch Update to Redis message {%s}", "vikram ") + "\u001B[0m");
            }
            return null;
      }


}
