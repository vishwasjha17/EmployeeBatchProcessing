package utils.mySqlUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import constants.AppMetrics;
import javax.sql.DataSource;
import java.sql.*;

public  class MySqlClientPool {
    private static final DataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(AppMetrics.MYSQL_URL);
        config.setUsername(AppMetrics.MYSQL_USERNAME);
        config.setPassword(AppMetrics.MYSQL_PASSWORD);
        config.setMaximumPoolSize(AppMetrics.MYSQL_MAX_POOL_SIZE);
        config.setMinimumIdle(AppMetrics.MYSQL_MIN_IDLE_CONNECTIONS);
        config.setConnectionTimeout(AppMetrics.MYSQL_MAX_CONNECTION_TIMEOUT);
        dataSource = new HikariDataSource(config);
    }
    public static DataSource getDataSource() {
            return dataSource;
    }

    public static void main1(String[] args) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/emp_db";
        String username = "root";
        String password = "my-secret-pw";
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Connection established successfully!");

            String insertQuery = "INSERT INTO employee (empoffset,empid,empname,empphone,empmailid,validemployee) VALUES (?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1,1L);
            preparedStatement.setString(2, "12341235");
            preparedStatement.setString(3, "vishwas kumar");
            preparedStatement.setString(4, "7091444909");
            preparedStatement.setString(5, "abc@gmail.com");
            preparedStatement.setString(6, "YES");
            preparedStatement.addBatch();

            preparedStatement.setLong(1,2L);
            preparedStatement.setString(2, "1234124341234");
            preparedStatement.setString(3, "vikram kumar");
            preparedStatement.setString(4, "7003381857");
            preparedStatement.setString(5, "xyz@gmail.com");
            preparedStatement.setString(6, "YES");
            preparedStatement.addBatch();

            preparedStatement.setLong(1,3L);
            preparedStatement.setString(2, "123412434141234");
            preparedStatement.setString(3, "vikram kumar jha");
            preparedStatement.setString(4, "7003381857343214");
            preparedStatement.setString(5, "helloworld@gmail.com");
            preparedStatement.setString(6, "NO");
            preparedStatement.addBatch();

            int[] rowsAffected = preparedStatement.executeBatch();

            System.out.println("rows Affected Count ::"+ rowsAffected.length);

            for(int vales : rowsAffected){
                System.out.println("rows affected::" +vales);
            }

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            int rowCount = 0;
            System.out.println( "generated keyw nex col value ::" +generatedKeys.next());
            while (generatedKeys.next()) {
                long generatedKey = generatedKeys.getLong(2); // Use the appropriate column index
                System.out.println("Generated Key for row " + (rowCount + 1) + ": " + generatedKey);
                rowCount++;
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}




