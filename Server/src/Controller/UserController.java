package Controller;

import model.Books;
import model.DaoInterface;
import model.Returns;
import model.Users;
import org.mindrot.jbcrypt.BCrypt;
import server.ServerRun;
import synchronization.insertLog;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserController extends UnicastRemoteObject implements DaoInterface<Users> {
    private Connection connection;
    private ArrayList<String> user_name_insystem = new ArrayList<>();
    private final Lock lock = new ReentrantLock();
    private static Map<String, Date> users_login_success = new HashMap<>();
    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&*()])[A-Za-z\\d@#$%^&*()]{8,}$";
    private static final Pattern pattern_pass = Pattern.compile(PASSWORD_REGEX);
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern pattern_email = Pattern.compile(EMAIL_REGEX);

    public UserController(Connection connection) throws RemoteException {
        this.connection = connection;
    }

    @Override
    public void client_access(String ip_client) throws RemoteException {

    }

    // Hàm mã hóa password
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Hàm kiểm tra password
    public static boolean checkPassword(String inputPassword, String hashedPassword) {
        return BCrypt.checkpw(inputPassword, hashedPassword);
    }

    // Mượn phương thức insert để thêm mới người dùng ( chức năng đăng ký )
    @Override
    public int insert(Users users) throws RemoteException {
        lock.lock();
        int check = 0;
        try {
            ArrayList<Users> list_user = selectAll();
            user_name_insystem.clear();
            for(Users user : list_user){
                user_name_insystem.add(user.getUsername());
            }
            if (user_name_insystem.contains(users.getUsername())) {
                System.out.println(users.getUsername() + " already exists");
                check = 2;
            } else {
                Matcher matcher_pass = pattern_pass.matcher(users.getPassword());
//                Matcher matcher_email = pattern_email.matcher(users.getUsername());
                if(matcher_pass.matches() == false){
                    System.out.println("Password isn't in correct format.");
                    check = 3;
                }
//                else if(matcher_email.matches() == false){
//                    System.out.println("Username isn't in correct format.");
//                    check = 4;
//                }
                else{
                    // Tạo ra đối tượng PreparedStatement
                    String sql = "INSERT INTO users(username, password, address, phonenumber)"
                            + " VALUE(?, ?, ?, ?)";
                    PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                    pst.setString(1, users.getUsername());
                    String hashedPassword = hashPassword(users.getPassword());
                    pst.setString(2, hashedPassword);
                    pst.setString(3,"VietNam");
                    pst.setString(4, "093452123");

                    // Thực thi 1 câu lệnh SQL
                    check = pst.executeUpdate();
                    // Xử lý kết quả
                    if(check == 1){
                        System.out.println("Insert user Success");
                        String data = users.getId() + "_" + users.getUsername() + "_" + users.getPassword()
                                + "_" + users.getAddress() + "_" + users.getPhonenumber();
                        new insertLog(connection, "INSERT", "users", data, true);
                    }else{
                        System.out.println("Insert user Failed");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return check;
    }



    @Override
    // Mượn phương thức update để check người dùng ( chức năng đăng nhập )
    public int update(Users users) throws RemoteException {
        lock.lock();
        int check = 0;
        try{
            ArrayList<Users> list_user = selectAll();
            String username_input = users.getUsername();
            String password_input = users.getPassword();
            for(Users user : list_user){
                boolean areEqual = user.getUsername().equals(username_input); // So sánh nghĩa chính xác
                if(areEqual == true){
                    // Kiểm tra mật khẩu
                    boolean areEqual2 = checkPassword(password_input, user.getPassword());
                    // boolean areEqual2 = user.getPassword().equals(password_input); // So sánh nghĩa chính xác
                    if(areEqual2 == true){
                        check = 1;
                        break;
                    }
                }
            }
            if(check == 1){
                java.util.Date currentDate = new Date();
                // Trường hợp username_input đã tồn tại, thì tiến hành ghi đè dữ liệu
                if (users_login_success.containsKey(username_input)) {
                    System.out.println(username_input+" logout at "+currentDate);
                }
                users_login_success.put(username_input, currentDate);
                System.out.println(username_input+" login successful at "+currentDate);

//                for (Map.Entry<String, Date> entry : users_login_success.entrySet()) {
//                    String user_name = entry.getKey();
//                    Date current_Date = entry.getValue();
//
//                    System.out.println("Người dùng: " + user_name + ", thời gian: " + current_Date);
//                }

//            new ServerRun().number_user_login(users);
            }else{
                System.out.println("Login failed");
            }
            return check;
        }finally {
            lock.unlock();
        }
    }

    // Mượn phương thức delete để check người dùng ( chức năng đăng xuất )
    @Override
    public int delete(Users users) throws RemoteException {
        lock.lock();
        int check = 0;
        try{
            boolean userExists = users_login_success.containsKey(users.getUsername());
            if(userExists == true){
                users_login_success.remove(users.getUsername());
                java.util.Date currentDate = new Date();
                System.out.println(users.getUsername()+" logout at "+currentDate);
            }else{
                System.out.println("Not exists user");
            }
            return check;
        }finally {
            lock.unlock();
        }

    }

    @Override
    public ArrayList<Users> selectAll() throws RemoteException {
        ArrayList<Users> check = new ArrayList<Users>();
        try {
            String sql = "SELECT * FROM users";

            PreparedStatement pst = connection.prepareStatement(sql);

            ResultSet rs = pst.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String address = rs.getString("address");
                String phonenumber = rs.getString("phonenumber");

                Users users = new Users(id, username, password, address, phonenumber);
                check.add(users);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    @Override
    public ArrayList<Users> searchByName(String nameInput) throws RemoteException {
        return null;
    }

    @Override
    public ArrayList<Users> selectListById(int id_suport) throws RemoteException {
        return null;
    }

    @Override
    public Users selectById(Users users) throws RemoteException {
        return null;
    }

    @Override
    public int insert_list(ArrayList<Users> t, int id_suport) throws RemoteException {
        return 0;
    }

    @Override
    public int delete_list(ArrayList<Users> t, int id_suport) throws RemoteException {
        return 0;
    }

    @Override
    public int update_list(ArrayList<Users> t, int id_suport) throws RemoteException {
        return 0;
    }

    @Override
    public int insert_comp(ArrayList<Books> books, Users users) throws RemoteException {
        return 0;
    }

    @Override
    public int update_comp(ArrayList<Books> books, Users users) throws RemoteException {
        return 0;
    }

    @Override
    public int delete_comp(ArrayList<Books> books, Users users) throws RemoteException {
        return 0;
    }

}
