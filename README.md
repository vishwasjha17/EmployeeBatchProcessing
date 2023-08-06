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
      $ docker run --name some-mysql -p3030:3030 -e MYSQL_ROOT_PASSWORD=my-secret-pw -d mysql:tag<br>
      $ docker ps<br>

   for more detail follow offical doc<br>
   https://hub.docker.com/_/mysql<br>
5. Need to Mention the EMPLOYEE_SOURCE_FILE_PATH in application.properties <br>
6. Need to Create a employee table to get the recoreds of employess<br>
7.     create table emplpoyee(
            empoffset bigint, empid varchar(255), empname varchar(255), empphone varchar(255), empmailid varchar(255), validemployee varchar(10)
        )<br>
<br>
8. There are Two Status in Redis corrosponding to each offset IN_PROCESS or COMPLETED so that even if application restarts next time
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



<br>
<hr>    The End Feel Free to Provide andy further improvments which can be done :) <hr>
<br>






