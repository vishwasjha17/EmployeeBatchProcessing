package utils.mySqlUtils;

import constants.Status;
import entity.Employee;
import entity.RedisEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.dbservice.DBupdate;
import service.redisservice.RedisService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MysqlUtils {
    private static final Logger logger = LoggerFactory.getLogger(DBupdate.class);
    public static void insertInBatch(List<Employee> listEmployees){
        logger.info("\u001B[32m"+"[[MYSQL_REQUEST]] IN memory Request of BatchSize :: "+listEmployees.size()+"\u001B[0m");
        Connection connection =  null;
                    try{
                         connection = MySqlClientPool.getDataSource().getConnection();
                         connection.setAutoCommit(false);
                         String sql = "INSERT INTO employee (empoffset,empid,empname,empphone,empmailid,validemployee) VALUES (?,?,?,?,?,?)";
                         PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                         for(Employee employee : listEmployees){
                             preparedStatement.setLong(1,Long.parseLong(employee.getOffSet()));
                             preparedStatement.setString(2, employee.getEmpId());
                             preparedStatement.setString(3, employee.getEmpName());
                             preparedStatement.setString(4, employee.getEmpPhoneNumber());
                             preparedStatement.setString(5, employee.getEmpEmailId());
                             preparedStatement.setString(6, employee.isValidEmployee()?"YES":"NO");
                             preparedStatement.addBatch();
                         }
                         int [] response = preparedStatement.executeBatch();
                         connection.commit();
                         logger.info("\u001B[32m"+"[[MYSQL_COMMIT_DONE]] IN memory Request of BatchSize :: "+listEmployees.size()+"\u001B[0m");
                         updateRedisKeysState(listEmployees,response);
                    }catch (Exception ex) {
                        ex.printStackTrace();
                        try {
                            connection.rollback();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        logger.info("\u001B[31m" + "[[MYSQL_FAILURE]] During Upate Error Msg :: " + ex.getMessage() + "\u001B[0m");
                    }finally {
                        try {
                            if (connection != null) {
                                connection.close();
                            }
                        }catch (Exception ex){
                            logger.info("\u001B[31m" + "[[MYSQL_FAILURE]] During Connection Closing :: " + ex.getMessage() + "\u001B[0m");
                        }
                    }

             }
             private static List<RedisEntity> getBatchToUpdate(List<Employee>listOfEmployees,int [] response){
                    List<RedisEntity> rsEntities = new ArrayList<>();
                   /* if(response.length != listOfEmployees.size()){
                        logger.error("!!{{MAJOR ISSUE FROM MYSQL UPDATE TO REDIS}} Because of Some Issue From DB lets Not Update This Batch And Reprocess");
                        return rsEntities;
                    }*/
                 logger.info("!! [[REDIS_BATCH_UPDATE]] Ready To Update to Redis From MYSQL size ::"+ response.length);
                 for(Integer empIdx =0; empIdx < listOfEmployees.size(); empIdx++){
                        Employee employee = listOfEmployees.get(empIdx);
                       // if(response[empIdx]>=0) {
                            rsEntities.add(new RedisEntity(employee.getOffSet().toString(), Status.COMPLETED.toString()));
                        //}
                    }
                    return rsEntities;
             }
             private static void updateRedisKeysState(List<Employee> listOfEmployees,int [] response){
                        List<RedisEntity> rsEntity = getBatchToUpdate(listOfEmployees,response);
                        RedisService.batchUpdate(rsEntity);
             }
}
