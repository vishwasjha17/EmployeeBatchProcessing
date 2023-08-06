package service.producer;

import constants.AppMetrics;
import constants.Status;
import entity.Employee;
import entity.RedisEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.consumer.EmployeeDataProcessor;
import service.redisservice.RedisService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class EmployeeQueue implements Runnable{
    public static LinkedBlockingQueue<Employee> inMemoryQueue = new LinkedBlockingQueue<>();
    private static Logger logger = LoggerFactory.getLogger(EmployeeQueue.class);
    private BufferedReader bufferedReader;

    private Integer rowOffSet;
    private String line = "";
    public EmployeeQueue(){
        try {
            this.rowOffSet = 0;
            bufferedReader = new BufferedReader(new FileReader(AppMetrics.EMPLOYEE_SOURCE_FILE_PATH));
            logger.info("\u001B[32m" + String.format("[[SUCCESS]] :: Employee Redis Queue Producer Initialization Started::")+ "\u001B[0m");
        }catch (Exception ex){
            logger.error("\u001B[31m" + String.format("[[ERROR]] :: Employee Redis Queue Producer cause :: {%s}", ex.getMessage()) + "\u001B[0m");
        }
    }
    public EmployeeQueue(String filePath){
         try{
             this.rowOffSet = 0;
             bufferedReader = new BufferedReader(new FileReader(filePath));
             bufferedReader.readLine(); // skipping Header ..
             logger.info("\u001B[32m" + String.format("[[SUCCESS]] :: Employee Redis Queue Producer Initialization Started::")+ "\u001B[0m");
         }catch (Exception ex){
             logger.error("\u001B[31m" + String.format("[[ERROR]] :: Employee Redis Queue Producer cause :: {%s}", ex.getMessage()) + "\u001B[0m");
         }
    }

    @Override
    public void run() {
        System.out.println("Inside the Runner Producer ::");
        try {
            String empId , empName,empPhoneNumber,empMailId;
            List<RedisEntity> redisEntityList = new ArrayList<>();
            while (true) {
                try {
                    if (inMemoryQueue.size() >= AppMetrics.MAX_RAM_SIZE) {
                        logger.info("\u001B[32m"+"[[PRODUCER]] in producer employee cause current Size of InMemoryQueue :: " + inMemoryQueue.size()+"\u001B[0m");
                        Thread.sleep(10000);
                    }else{
                        line = bufferedReader.readLine();
                        if(line == null) {
                            if(!redisEntityList.isEmpty()){
                                RedisService.batchUpdate(redisEntityList);
                                redisEntityList.clear();
                            }
                            logger.info("\u001B[32m"+"[[PRODUCER]] in producer Reading End of File :: " +rowOffSet +"\u001B[0m");
                            break;
                        }
                        String []employeeContent = line.split(",");
                        empId = employeeContent[0].trim();
                        empName = employeeContent[1].trim();
                        empPhoneNumber = employeeContent[2].trim();
                        empMailId = employeeContent[3].trim();
                        String lookUpRedis = RedisService.getKey(rowOffSet.toString());
                        if(lookUpRedis == null || lookUpRedis.equals(Status.IN_PROCESS.toString())){
                            inMemoryQueue.add(new Employee(rowOffSet.toString(),empId,empName,empMailId,empPhoneNumber));
                            redisEntityList.add(new RedisEntity(rowOffSet.toString(),Status.IN_PROCESS.toString()));
                        }else{
                            logger.info("\u001B[32m"+"[[PRODUCDER]] in producer employee Done Completion for  offSet:: " +rowOffSet +"\u001B[0m");
                        }
                        if(!redisEntityList.isEmpty() && redisEntityList.size() >= AppMetrics.REDIS_BATCH_SIZE){
                            RedisService.batchUpdate(redisEntityList);
                            redisEntityList.clear();
                        }
                        rowOffSet++;
                    }
                } catch (Exception ex) {
                    logger.error("\u001B[31m"+"[[Error]] in producer employee :: " + ex.getMessage()+"\u001B[0m");
                    if(line!=null){
                        logger.error("!![[PRODUCER]] while loading the  File Format Exception most Likely "+ex.getMessage());
                    }
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            logger.error("\u001B[31m"+ "[[Error]] in producer employee while loadint the file ::" + ex.getMessage()+"\u001B[0m");
        }finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }catch (Exception ex){
                logger.error("\u001B[31m"+ "[[Error]] Issue With Closing the BufferedReader ::" + ex.getMessage()+"\u001B[0m");
            }
        }

    }

    public static void main(String[] args) throws  Exception{
            BufferedWriter bw = new BufferedWriter(new FileWriter(AppMetrics.EMPLOYEE_SOURCE_FILE_PATH));
            bw.write("empId,empName,empPhoneNumber,empEmailId");
            bw.newLine();
            Integer rowNumber = 0;
            while(rowNumber <=155230L){
                Long randomNumber = ThreadLocalRandom.current().nextLong(10001);
                String empId = "123456";
                String empName = "empName vishwas";
                String empPhoneNumber = "7003381857";
                String empEmailId = "empEmailId"+randomNumber+"@gmail.com";
                if(randomNumber>=400L) {
                    empPhoneNumber = "7252542452452";
                    empName ="014Vishwas kumar";
                }
                bw.write(empId+","+empName+","+empPhoneNumber+","+empEmailId);
                bw.newLine();
                bw.flush();
                rowNumber++;

            }
        System.out.println("Row Number :: "+ rowNumber);
    }
}
