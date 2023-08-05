package constants;

import java.io.FileInputStream;
import java.util.Properties;

public class AppMetrics {
       public static  String REDIS_HOST = "localhost";
       public static  Integer REDIS_PORT = 6379;
       public static  Integer REDIS_MAX_TOTAL_CONNECTIONS = 10;
       public static  Integer REDIS_MAX_IDLE_CONNECTIONS = 5;
       public static  Integer REDIS_MIN_IDLE_CONNECTIONS = 1;
       public static Integer REDIS_BATCH_SIZE = 200;
       public static  Integer MAX_RAM_SIZE = 2000;
       public static  Integer EMPLOYEE_QUEUE_BATCH_SIZE = 200;
       public static  String EMPLOYEE_SOURCE_FILE_PATH = "/Users/vishwaskumar/Desktop/employee.csv";
       public static  String  MYSQL_URL = "jdbc:mysql://localhost:3306/emp_db";
       public static  String  MYSQL_USERNAME = "root";
       public static  String  MYSQL_PASSWORD = "my-secret-pw";
       public static  Integer MYSQL_MAX_POOL_SIZE = 10;
       public static  Integer MYSQL_MIN_IDLE_CONNECTIONS = 5;
       public static  Long  MYSQL_MAX_CONNECTION_TIMEOUT = 30000L;

       public static void AppMetricsInit() {
              try {
                     String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
                     String appConfigPath = rootPath + "application.properties";
                     Properties appProps = new Properties();
                     appProps.load(new FileInputStream(appConfigPath));

                     REDIS_HOST = appProps.getProperty("REDIS_HOST");
                     REDIS_PORT = Integer.parseInt(appProps.getProperty("REDIS_PORT"));
                     REDIS_MAX_TOTAL_CONNECTIONS = Integer.parseInt(appProps.getProperty("REDIS_MAX_TOTAL_CONNECTIONS"));
                     REDIS_MAX_IDLE_CONNECTIONS = Integer.parseInt(appProps.getProperty("REDIS_MAX_IDLE_CONNECTIONS"));
                     REDIS_MIN_IDLE_CONNECTIONS = Integer.parseInt(appProps.getProperty("REDIS_MIN_IDLE_CONNECTIONS"));
                     REDIS_BATCH_SIZE           = Integer.parseInt(appProps.getProperty("REDIS_BATCH_SIZE"));

                     MAX_RAM_SIZE = Integer.parseInt(appProps.getProperty("MAX_RAM_SIZE"));

                     MYSQL_URL = appProps.getProperty("MYSQL_URL");
                     MYSQL_USERNAME = appProps.getProperty("MYSQL_USERNAME");
                     MYSQL_PASSWORD = appProps.getProperty("MYSQL_PASSWORD");
                     MYSQL_MAX_POOL_SIZE = Integer.parseInt(appProps.getProperty("MYSQL_MAX_POOL_SIZE"));
                     MYSQL_MIN_IDLE_CONNECTIONS = Integer.parseInt(appProps.getProperty("MYSQL_MIN_IDLE_CONNECTIONS"));
                     MYSQL_MAX_CONNECTION_TIMEOUT = Long.parseLong(appProps.getProperty("MYSQL_MAX_CONNECTION_TIMEOUT"));

                     EMPLOYEE_SOURCE_FILE_PATH = appProps.getProperty("EMPLOYEE_SOURCE_FILE_PATH");
                     EMPLOYEE_QUEUE_BATCH_SIZE = Integer.parseInt(appProps.getProperty("EMPLOYEE_QUEUE_BATCH_SIZE"));

              }catch (Exception ex){
                     System.out.println("[[ERROR]] while Loading AppMetrics ");
              }
       }
}
