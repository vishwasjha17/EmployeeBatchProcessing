package entity;

public class RedisEntity {
        private String redisKey;
        private String status;
        public RedisEntity(String redisKey,String status){
              this.redisKey = redisKey;
              this.status   = status;
        }
        public String getRedisKey() {
            return redisKey;
       }

        public String getStatus() {
             return status;
        }

        public void setRedisKey(String redisKey) {
            this.redisKey = redisKey;
       }

        public void setStatus(String status) {
            this.status = status;
       }
}

