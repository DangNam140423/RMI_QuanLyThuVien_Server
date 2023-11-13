package synchronization;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class insertLog {
    private Connection connection;
    private String operation;
    private String table;
    private String data;
    private boolean isSynchronization;

    public insertLog(Connection connection, String operation, String table, String data, boolean isSynchronization){
        this.connection = connection;
        this.operation = operation;
        this.table = table;
        this.data = data;
        this.isSynchronization = isSynchronization;

        insert();
    }

    public void insert(){
        int check = 0;
        try {
            String logQuery = "INSERT INTO data_log(operation, `table`, data)"
                               + " VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(logQuery);
            preparedStatement.setString(1, operation);
            preparedStatement.setString(2, table);
            preparedStatement.setString(3, data);
            check = preparedStatement.executeUpdate();

            if(isSynchronization == true){
                if(check > 0){
                    // Xử lý quá trình đồng bộ hóa
                    new DataSynchronization();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
