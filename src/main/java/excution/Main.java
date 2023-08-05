package excution;

import constants.AppMetrics;
import service.consumer.EmployeeDataProcessor;
import service.producer.EmployeeQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class Main {
    public static ExecutorService esProducer,esConsumer;
    private static Integer NO_OF_CONSUMER_THREAD = 10;

    static {
        AppMetrics.AppMetricsInit();
        esConsumer = Executors.newFixedThreadPool(NO_OF_CONSUMER_THREAD);
        esProducer = Executors.newSingleThreadExecutor();
    }

    public static void main(String[] args)  throws Exception{
        List<Future<?>> futureList = new ArrayList<>();
        for(int task = 0;task<NO_OF_CONSUMER_THREAD;task++) {
            futureList.add(esConsumer.submit(new EmployeeDataProcessor()));
        }
        futureList.add(esProducer.submit(new EmployeeQueue(AppMetrics.EMPLOYEE_SOURCE_FILE_PATH)));
        waitingForThreadToComplete(futureList);
        esConsumer.shutdown();
        esProducer.shutdown();
    }
    private static void waitingForThreadToComplete(List<Future<?>> futureList) throws InterruptedException, ExecutionException {
             while(true){
                    boolean isAllDone = true;
                    for(Future<?> future: futureList){
                        if(!future.isDone()){
                            isAllDone = false;
                            break;
                        }
                    }
                    if(isAllDone)break;
             }
    }

}