package Database;

import com.mysql.cj.jdbc.Driver;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCUtil {
    public static Connection getConnection_1() {
        Connection c = null;
        String dbName = "rmi_quanlythuvien";
        try {
            // Đăng ký mySQL Driver với DriverManager
            Driver driver = new Driver();
            DriverManager.registerDriver(driver);

            // Các thông số để kết nối
            String url = "jdbc:mySQL://localhost:3306/" + dbName;
            String username = "root";
            String password = "";

            c = DriverManager.getConnection(url, username, password);
        }catch (SQLException e){
            e.printStackTrace();
        }

        return c;
    }

    public static Connection getConnection_2() {
        Connection c = null;
        String dbName = "rmi_quanlythuvien_2";
        try {
            // Đăng ký mySQL Driver với DriverManager
            Driver driver = new Driver();
            DriverManager.registerDriver(driver);

            // Các thông số để kết nối
            String url = "jdbc:mySQL://localhost:3306/" + dbName;
            String username = "root";
            String password = "";

            c = DriverManager.getConnection(url, username, password);
        }catch (SQLException e){
            e.printStackTrace();
        }

        return c;
    }



    public static void closeConnection(Connection c){
        try {
            if (c != null) {
                c.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void printInfo(Connection c){
        try {
            if(c != null){
                DatabaseMetaData mtdt = c.getMetaData();
                System.out.println(mtdt.getDatabaseProductName());
                System.out.println(mtdt.getDatabaseProductVersion());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
