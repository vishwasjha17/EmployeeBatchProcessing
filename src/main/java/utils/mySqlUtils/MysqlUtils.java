package utils.mySqlUtils;

import constants.Status;
import entity.Employee;
import entity.RedisEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.dbservice.DBupdate;
import service.redisservice.RedisService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MysqlUtils {
    private static final Logger logger = LoggerFactory.getLogger(DBupdate.class);
    private static final Integer MAX_TRY = 3;

    public static void insertInBatch(List<Employee> listEmployees){
        Integer retryCounter = 0;
        Boolean mySqlUpdateSuccess = false;
        while(retryCounter++ < MAX_TRY  && !mySqlUpdateSuccess) {
            logger.info("\u001B[32m"+ String.format("[[MYSQL Retry For BATCH Update]] FOR SUCCESS COUNTER ::{%d}",retryCounter)+"\u001B[0m");
            Connection connection = null;
            int[] response = null;
            try {
                connection = MySqlClientPool.getDataSource().getConnection();
                connection.setAutoCommit(false);
                String sql = "INSERT INTO employee (empoffset,empid,empname,empphone,empmailid,validemployee) VALUES (?,?,?,?,?,?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                for (Employee employee : listEmployees) {
                    preparedStatement.setLong(1, Long.parseLong(employee.getOffSet()));
                    preparedStatement.setString(2, employee.getEmpId());
                    preparedStatement.setString(3, employee.getEmpName());
                    preparedStatement.setString(4, employee.getEmpPhoneNumber());
                    preparedStatement.setString(5, employee.getEmpEmailId());
                    preparedStatement.setString(6, employee.isValidEmployee() ? "YES" : "NO");
                    preparedStatement.addBatch();
                }
                response = preparedStatement.executeBatch();
                connection.commit();
                mySqlUpdateSuccess = true;
                logger.info("\u001B[32m" + "[[MYSQL_COMMIT_DONE]] IN memory Request of BatchSize :: " + listEmployees.size() + "\u001B[0m");
                updateRedisKeysState(listEmployees, response);
            } catch (Exception ex) {
                ex.printStackTrace();
                try {
                    connection.rollback();
                    if (ex instanceof SQLIntegrityConstraintViolationException) {
                        logger.info("\u001B[32m" + "[[MYSQL_COMMIT_DUPLICATE Key Exception]] IN memory Request of BatchSize :: " + listEmployees.size() + "\u001B[0m");
                        updateRedisKeysState(listEmployees, response);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                logger.info("\u001B[31m" + "[[MYSQL_FAILURE]] During Upate Error Msg :: " + ex.getMessage() + "\u001B[0m");
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (Exception ex) {
                    logger.info("\u001B[31m" + "[[MYSQL_FAILURE]] During Connection Closing :: " + ex.getMessage() + "\u001B[0m");
                }
            }
        }

     }
     private static List<RedisEntity> getBatchToUpdate(List<Employee>listOfEmployees,int [] response){
                  List<RedisEntity> rsEntities = new ArrayList<>();
                  logger.info("!! [[REDIS_BATCH_UPDATE]] Ready To Update to Redis From MYSQL size ::"+ response.length);
                  for(Integer empIdx =0; empIdx < listOfEmployees.size(); empIdx++){
                        Employee employee = listOfEmployees.get(empIdx);
                        rsEntities.add(new RedisEntity(employee.getOffSet().toString(), Status.COMPLETED.toString()));
                  }
                  return rsEntities;
    }

    private static void updateRedisKeysState(List<Employee> listOfEmployees,int [] response){
                        List<RedisEntity> rsEntity = getBatchToUpdate(listOfEmployees,response);
                        RedisService.batchUpdate(rsEntity);
    }
}
