# EmployeeBatchProcessing
Some Dependencies Need to Be installed <br>
1. You need a redis up and running<br>
   use the below command ::<br>
      $ docker run --name my-redis -p 6379:6379 -d redis<br>
      $ docker exec -it my-redis /bin/bash (to enter in redis)<br>
   make sure it is up and running with :: $ docker ps <br>
       <br>
       <br>
3. You need mysql up and running<br>
      $ docker run --name some-mysql -p3306:3306 -e MYSQL_ROOT_PASSWORD=my-secret-pw -d mysql:tag<br>
      $ docker ps<br>

   for more detail follow offical doc<br>
   https://hub.docker.com/_/mysql<br>
5. Need to Mention the EMPLOYEE_SOURCE_FILE_PATH in application.properties <br>
6. Need to Create a employee table to get the recoreds of employess<br>
<br>

  create table employee(empoffset bigint not null primary key,empid varchar(255),empname varchar(255),empphone varchar(255),empmailid 
 varchar(255),validemployee varchar(10)
   )
<br>
<br>
9. There are Two Status in Redis corrosponding to each offset IN_PROCESS or COMPLETED so that even if application restarts next time
   we know only which element to push in inMemory Queue in two cases either we don't find in redis or its status is inprocess because likely possible that we have marked the status in_process for some employee and even before updation to db our applicaiton shutdowns
   so in both cases we will requeue else if completed in db in batch trancation no need to consider that offset again in case of app shutdonw.
   <br>
   <br>
   <br>
     you can see Valid Record :: select * from employee whre validemployee="YES"  <br>    
   
     Note :- validemployee(NO/YES) filed is used to get the Case where Employee is not valid employee as per constraint "YES" if valid "NO" elsewise empoffset (Represent Key for Redis Line Offset) and other field i have taken as string as i am storing valid and invalid in same table  <br>

Below are the Important Paramameter of application properties <br>
REDIS_HOST=localhost <br>
REDIS_PORT=6379      <br>
REDIS_MAX_TOTAL_CONNECTIONS=10 <br>
REDIS_MAX_IDLE_CONNECTIONS=5    <br>
REDIS_MIN_IDLE_CONNECTIONS=1   <br>
REDIS_BATCH_SIZE=500    <br>
MAX_RAM_SIZE=2000   <br>
EMPLOYEE_QUEUE_BATCH_SIZE=200 <br>
EMPLOYEE_SOURCE_FILE_PATH=/Users/vishwaskumar/Desktop/employee.csv  <br>
MYSQL_URL=jdbc:mysql://localhost:3306/emp_db <br>
MYSQL_USERNAME=root <br>
MYSQL_PASSWORD=my-secret-pw <br>
MYSQL_MAX_POOL_SIZE=10 <br>
MYSQL_MIN_IDLE_CONNECTIONS=5 <br>
MYSQL_MAX_CONNECTION_TIMEOUT=30000 <br>



Important Note :-

Three Diffrent Approaches taken based upon Problem Faced  ::
Branch master 
<br>
<br>
Initial Approach was Assuming Redis Batch Upates Working Properly
But it's not the Case the Completed stage were not updating properly even Though it's getting updated in msSql
<br>
<br>
Branch masterRetry ::
<br>
Have Tried the Updates in Batches In redis Transaction for consistency and even pipelined approach 
giving with Retry Included Still For Some of the Cases the updates were not happing properly in redis
<br>
<br>
Branch masterFinal ::
<br>
Either at the Time of Confirmation If my App Restarts i can assume purely source of truth to mysql table being indexing on offset but 
still to not put the Pressure over Mssql Db have Used the Redis in case if there is nothing in redis means we can directly consider the
element else in case if the state is marked as in_Process but we make the confirmation before putting to internal queue else if it is 
completed no need to consider.

The Third Approach Seems Better in Case we don't wan't to put the pressure of mssql else we coule have completly 
removed the redis from the picture and use the mssql being indexing on the empoffset for lookup.

<hr>    The End Feel Free to Provide andy further improvments which can be done :) <hr>
<br>



