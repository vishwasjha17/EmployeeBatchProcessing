package service.consumer;

import constants.AppMetrics;
import entity.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.dbservice.DBupdate;
import service.producer.EmployeeQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class EmployeeDataProcessor implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(EmployeeDataProcessor.class);
    public static AtomicLong counter = new AtomicLong(0);
    public static void saveToDB(List<Employee> employeeBatch){
           DBupdate.saveTomsSqlDB(employeeBatch);
    }

    @Override
    public void run() {
        while(true){
            try {
                if (EmployeeQueue.inMemoryQueue.isEmpty()) {
                    logger.info("\u001B[32m"+"[[CONSUMER]] IN memory IS Empty Consumer Going for sleep ::>>>> :: "+"\u001B[0m");
                    Thread.sleep(4000);
                }else {
                    List<Employee> employeeBatch = new ArrayList<>();
                    EmployeeQueue.inMemoryQueue.drainTo(employeeBatch, AppMetrics.EMPLOYEE_QUEUE_BATCH_SIZE);
                    saveToDB(employeeBatch);
                    logger.info("\u001B[32m"+"[[CONSUMER]] Total Consumed Elements Till Now :: "+counter.getAndAdd(employeeBatch.size())+"\u001B[0m");
                }
            }catch (Exception ex){
                logger.error("\u001B[31m"+"[[CONSUMER]] Error In Consumer Thread:: "+ex.getMessage()+"\u001B[0m");
            }
        }
    }
}
