package service.dbservice;

import entity.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.consumer.EmployeeDataProcessor;
import utils.mySqlUtils.MySqlClientPool;
import utils.mySqlUtils.MysqlUtils;

import java.io.FileInputStream;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class DBupdate {
    private static final Logger logger = LoggerFactory.getLogger(DBupdate.class);
    public static void saveTomsSqlDB(List<Employee> batchUpdate){
        logger.info("\u001B[32m"+"[[MYSQL_REQUEST]]  Update in Batch of BatchSize:: "+batchUpdate.size()+"\u001B[0m");
        MysqlUtils.insertInBatch(batchUpdate);
    }

    public static void main(String[] args) throws Exception {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + "application.properties";
        System.out.println("appConfigPath ::" + appConfigPath);
        Properties appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));
        Integer REDIS_PORT = Integer.parseInt(Optional.ofNullable(appProps.getProperty("REDIS_PORT")).orElse("6379"));
        System.out.println("Redis Port :: " + REDIS_PORT);
    }
}